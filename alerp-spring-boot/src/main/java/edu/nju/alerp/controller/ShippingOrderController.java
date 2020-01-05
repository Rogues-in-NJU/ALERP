package edu.nju.alerp.controller;

import edu.nju.alerp.common.*;
import edu.nju.alerp.dto.ShippingOrderDTO;
import edu.nju.alerp.entity.*;
import edu.nju.alerp.enums.*;
import edu.nju.alerp.service.*;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.vo.ProductVO;
import edu.nju.alerp.vo.ShippingArrearRelationVO;
import edu.nju.alerp.vo.ShippingOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 出货单Controller层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:27
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/shipping-order")
public class ShippingOrderController {
    @Autowired
    ShippingOrderService shippingOrderService;
    @Autowired
    ProductService productService;
    @Autowired
    ProcessOrderService processOrderService;
    @Autowired
    ArrearOrderService arrearOrderService;
    @Autowired
    CustomerService customerService;
    @Resource
    private DocumentsIdFactory documentsIdFactory;

    /**
     * 删除出货单
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<Integer> delete(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        int arrearOrderId = shippingOrderService.getShippingOrder(id).getArrearOrderId();
        ArrearOrder arrearOrder = arrearOrderService.getOne(arrearOrderId);
        if (arrearOrder.getReceivedCash() > 0) {
            //如果已收金额大于0，则不能被出货单不能被废弃。
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "收款单已收款，无法废弃");
        }
        int res = shippingOrderService.deleteShippingOrder(id);
        //修改所有对应加工单的状态为"未完成"
        List<Integer> processingIdList = shippingOrderService.getProcessingListById(id);
//        根据id获取加工单，修改状态
        processingIdList.forEach(p -> {
            ProcessingOrder processingOrder = processOrderService.getOne(p);
            processingOrder.setStatus(ProcessingOrderStatus.UNFINISHED.getCode());
            processOrderService.savaProcessingOrder(processingOrder);
        });
        arrearOrder.setStatus(ArrearOrderStatus.ABANDONED.getCode());
        arrearOrderService.saveArrearOrder(arrearOrder);
        return ResponseResult.ok(res);
    }

    /**
     * 获取出货单列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult<ListResponse> list(@RequestParam(value = "pageIndex") int pageIndex,
                                             @RequestParam(value = "pageSize") int pageSize,
                                             @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                             @RequestParam(value = "status") int status,
                                             @RequestParam(value = "createAtStartTime") String createAtStartTime,
                                             @RequestParam(value = "createAtEndTime") String createAtEndTime) {
        Page<ShippingOrder> page = shippingOrderService.getShippingOrderList(PageRequest.of(pageIndex - 1, pageSize), name, status, createAtStartTime, createAtEndTime);
        return ResponseResult.ok(ListResponseUtils.generateResponse(page, pageIndex, pageSize));
    }

    /**
     * 新增出货单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<ShippingArrearRelationVO> saveShippingOrder(@Valid @RequestBody ShippingOrderDTO shippingOrderDTO) {
        try {
            int result = shippingOrderService.addShippingOrder(shippingOrderDTO);
            int userId = CommonUtils.getUserId();
            Customer customer = customerService.getCustomer(shippingOrderDTO.getCustomerId());
            if (customer == null) {
                throw new NJUException(ExceptionEnum.OTHER_ERROR, "出货单客户不存在！");
            }
            ArrearOrder arrearOrder = ArrearOrder.builder()
                    .createdAt(DateUtils.getToday())
                    .createdBy(userId)
                    .updatedAt(DateUtils.getToday())
                    .updatedBy(userId)
                    .code(documentsIdFactory.generateNextCode(DocumentsType.ARREAR_ORDER, CityEnum.of(CommonUtils.getCity())))
                    .customerId(shippingOrderDTO.getCustomerId())
                    .dueDate(DateUtils.getDueDate(customer.getPayDate()))
                    .receivableCash(shippingOrderDTO.getReceivableCash())
                    .receivedCash(shippingOrderDTO.getCash())
                    .build();
            int arrearOrderId = arrearOrderService.saveArrearOrder(arrearOrder);
            ShippingArrearRelationVO shippingArrearRelationVO = ShippingArrearRelationVO.builder()
                    .shippingOrderId(result)
                    .arrearOrderId(arrearOrderId)
                    .build();
            return ResponseResult.ok(shippingArrearRelationVO);
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }

    }

    /**
     * 查看出货单详情
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseResult<ShippingOrderVO> ShippingOrderVO(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        List<ShippingOrderProduct> shippingOrderProductList = shippingOrderService.getShippingOrderProductList(id);
        List<ProductVO> productVOList = new ArrayList<>();
        shippingOrderProductList.forEach(s -> {
            Product product = productService.findProductById(s.getProductId());
            ProductVO productVO = ProductVO.builder()
                    .productName(product.getName())
                    .type(product.getType())
                    .build();
            BeanUtils.copyProperties(s, productVO);
            productVOList.add(productVO);
        });
        ShippingOrder shippingOrder = shippingOrderService.getShippingOrder(id);
        ShippingOrderVO shippingOrderVO = ShippingOrderVO.builder()
                .city(CityEnum.of(shippingOrder.getCity()).getMessage())
                .productVOList(productVOList)
                .build();
        BeanUtils.copyProperties(shippingOrder, shippingOrderVO);

        return ResponseResult.ok(shippingOrderVO);
    }
}

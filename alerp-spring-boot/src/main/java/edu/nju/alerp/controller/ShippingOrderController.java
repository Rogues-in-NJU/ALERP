package edu.nju.alerp.controller;

import edu.nju.alerp.common.*;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.ProcessingOrderIdCodeDTO;
import edu.nju.alerp.dto.ReconciliationDTO;
import edu.nju.alerp.dto.ShippingOrderDTO;
import edu.nju.alerp.dto.ShippingProductDTO;
import edu.nju.alerp.entity.*;
import edu.nju.alerp.enums.*;
import edu.nju.alerp.service.*;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    UserService userService;
    @Resource
    private DocumentsIdFactory documentsIdFactory;

    /**
     * 删除出货单
     *
     * @param id
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET, name = "删除出货单")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<Integer> delete(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        int arrearOrderId = shippingOrderService.getShippingOrder(id).getArrearOrderId();
        ArrearOrder arrearOrder = arrearOrderService.getOne(arrearOrderId);
        if (arrearOrder.getReceivedCash() > 0) {
            //如果已收金额大于0，则不能被出货单不能被废弃。
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "收款单已收款，无法废弃")));
        }
        int res = 0;
        try {
            res = shippingOrderService.deleteShippingOrder(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //修改所有对应加工单的状态为"未完成"
        //针对损耗导致的加工单id为0的情况 去除
        List<Integer> processingIdList = shippingOrderService.getProcessingListById(id).stream().filter(p -> p != 0).collect(Collectors.toList());
//        根据id获取加工单，修改状态
        processingIdList.forEach(p -> {
            ProcessingOrder processingOrder = processOrderService.getOne(p);
            processingOrder.setShippingOrderId(0);
            processingOrder.setShippintOrderCode(null);
            processingOrder.setStatus(ProcessingOrderStatus.UNFINISHED.getCode());
            processOrderService.savaProcessingOrder(processingOrder);
        });
        arrearOrder.setStatus(ArrearOrderStatus.ABANDONED.getCode());
        arrearOrder.setDeletedAt(DateUtils.getTodayAccurateToSecond());
        arrearOrder.setDeletedBy(CommonUtils.getUserId());
        arrearOrderService.saveArrearOrder(arrearOrder);
        return ResponseResult.ok(res);
    }

    /**
     * 获取出货单列表
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET, name = "获取出货单列表")
    public ResponseResult<ListResponse> list(@RequestParam(value = "pageIndex") int pageIndex,
                                             @RequestParam(value = "pageSize") int pageSize,
                                             @RequestParam(value = "id", required = false, defaultValue = "") String code,
                                             @RequestParam(value = "customerName", required = false, defaultValue = "") String name,
                                             @RequestParam(value = "status", required = false) Integer status,
                                             @RequestParam(value = "createAtStartTime", required = false, defaultValue = "") String createAtStartTime,
                                             @RequestParam(value = "createAtEndTime", required = false, defaultValue = "") String createAtEndTime) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.ASC, "status"));
        Page<ShippingOrder> page = shippingOrderService.getShippingOrderList(pageable, code, name, status, createAtStartTime, createAtEndTime);
        return ResponseResult.ok(ListResponseUtils.generateResponse(new PageImpl<>(transfer(page), pageable, page.getTotalElements()), pageIndex, pageSize));
    }

    /**
     * 获取出货单列表
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/reconciliation", method = RequestMethod.GET, name = "获取出货单列表")
    public ResponseResult<ListResponse> reconciliation(@RequestParam(value = "pageIndex") int pageIndex,
                                                       @RequestParam(value = "pageSize") int pageSize,
                                                       @RequestParam(value = "id", required = false, defaultValue = "") String code,
                                                       @RequestParam(value = "customerName", required = false, defaultValue = "") String name,
                                                       @RequestParam(value = "status", required = false) Integer status,
                                                       @RequestParam(value = "createAtStartTime", required = false, defaultValue = "") String createAtStartTime,
                                                       @RequestParam(value = "createAtEndTime", required = false, defaultValue = "") String createAtEndTime) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.ASC, "hasReconciliationed"));
        Page<ShippingOrder> page = shippingOrderService.getReconciliationList(pageable, code, name, status, createAtStartTime, createAtEndTime);
        return ResponseResult.ok(ListResponseUtils.generateResponse(new PageImpl<>(transfer(page), pageable, page.getTotalElements()), pageIndex, pageSize));
    }

    private List<ShippingOrderBriefVO> transfer(Page<ShippingOrder> page) {
        List<ShippingOrderBriefVO> result = new ArrayList<>();
        page.getContent().forEach(s -> {
            ShippingOrderBriefVO shippingOrderBriefVO = ShippingOrderBriefVO.builder()
                    .customerName(customerService.getCustomer(s.getCustomerId()).getName())
                    .build();
            BeanUtils.copyProperties(s, shippingOrderBriefVO);
            result.add(shippingOrderBriefVO);
        });
        return result;
    }

    /**
     * 批量修改对账状态
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/print-list", method = RequestMethod.POST, name = "批量修改对账状态")
    public ResponseResult<Boolean> updateState(@RequestBody ReconciliationDTO reconciliationDTO) {
        for (Integer id : reconciliationDTO.getShippingOrderIds()) {
            ShippingOrder shippingOrder = shippingOrderService.getShippingOrder(id);
            shippingOrder.setHasReconciliationed(reconciliationDTO.getToState());
            shippingOrderService.saveShippingOrder(shippingOrder);
        }
        return ResponseResult.ok(true);
    }

    /**
     * 新增出货单
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST, name = "新增出货单")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<ShippingArrearRelationVO> saveShippingOrder(@Valid @RequestBody ShippingOrderDTO shippingOrderDTO) {
        try {
            ShippingOrder shippingOrder = shippingOrderService.addShippingOrder(shippingOrderDTO);
            List<Integer> processingOrderList = shippingOrderDTO.getProcessingOrderIdsCodes().stream().map(ProcessingOrderIdCodeDTO::getProcessingOrderId).collect(Collectors.toList());
            List<ShippingOrderProduct> shippingOrderProductList = new ArrayList<>();
            //删除加工单对应商品
            List<Integer> shippingProcessingProductIdList = shippingOrderDTO.getProducts().stream()
                    .filter(s -> s.getProcessingProductId() != null)
                    .map(ShippingProductDTO::getProcessingProductId)
                    .collect(Collectors.toList());
            List<ProcessOrderProduct> processingProductList = new ArrayList<>();
            shippingOrderDTO.getProcessingOrderIdsCodes().forEach(p -> {
                processingProductList.addAll(processOrderService.findProductByOrderId(p.getProcessingOrderId()));
            });
            List<Integer> processingProductIdList = processingProductList.stream().map(ProcessOrderProduct::getId).collect(Collectors.toList());
            try {
                for (Integer p : processingProductIdList) {
                    if (!shippingProcessingProductIdList.contains(p)) {
                        processOrderService.deleteProcessProductInShipping(p);
                    }
                }
            } catch (Exception e) {
                return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
            }

            //校验计价方式，遍历商品获取所有加工单
            try {
                shippingOrderDTO.getProducts().forEach(p -> {
                    if (PriceTypeEnum.of(p.getPriceType()) == null) {
                        throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "计价方式错误！");
                    }
                    ShippingOrderProduct shippingOrderProduct = ShippingOrderProduct.builder().build();
                    BeanUtils.copyProperties(p, shippingOrderProduct);
                    shippingOrderProductList.add(shippingOrderProduct);
                });
            } catch (Exception e) {
                return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
            }
            int userId = CommonUtils.getUserId();
            Customer customer = customerService.getCustomer(shippingOrderDTO.getCustomerId());
            if (customer == null) {
                return ResponseResult.fail(ExceptionWrapper.customExceptionWrapper(ExceptionEnum.ILLEGAL_REQUEST, "出货单客户不存在"));
            }
            ArrearOrder arrearOrder = ArrearOrder.builder()
                    // 收款单的初始状态为"未收款"
                    .status(ArrearOrderStatus.UNCOLLECTED.getCode())
                    .createdAt(DateUtils.getTodayAccurateToSecond())
                    .createdBy(userId)
                    .updatedAt(DateUtils.getTodayAccurateToSecond())
                    .updatedBy(userId)
                    .code(documentsIdFactory.generateNextCode(DocumentsType.ARREAR_ORDER, CityEnum.of(CommonUtils.getCity())))
                    .customerId(shippingOrderDTO.getCustomerId())
                    .dueDate(customer.getType() == CustomerType.MONTH.getCode() ? DateUtils.getDueDate(customer.getPayDate()) : DateUtils.getTodayAccurateToDay())
                    .receivableCash(shippingOrderDTO.getReceivableCash())
                    .receivedCash(0)
                    .city(CommonUtils.getCity())
                    .build();
            //先生成收款单，返回id 更新出货单
            int arrearOrderId = arrearOrderService.saveArrearOrder(arrearOrder);
            shippingOrder.setArrearOrderId(arrearOrderId);
            int shippingId = shippingOrderService.saveShippingOrder(shippingOrder);
            shippingOrderProductList.forEach(s -> {
                s.setShippingOrderId(shippingId);
                shippingOrderService.saveShippingOrderProduct(s);
            });
            processingOrderList.forEach(p -> {
                ProcessingOrder processingOrder = processOrderService.getOne(p);
                if (processingOrder.getStatus() != ProcessingOrderStatus.UNFINISHED.getCode()) {
                    throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "加工单状态错误！");
                }
                processingOrder.setShippingOrderId(shippingId);
                processingOrder.setStatus(ProcessingOrderStatus.FINISHED.getCode());
                processingOrder.setShippintOrderCode(shippingOrder.getCode());
                //待优化 可传list一次性更新
                processOrderService.savaProcessingOrder(processingOrder);
            });
            ShippingArrearRelationVO shippingArrearRelationVO = ShippingArrearRelationVO.builder()
                    .shippingOrderId(shippingId)
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
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, name = "查看出货单详情")
    public ResponseResult<ShippingOrderVO> ShippingOrderVO(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        ShippingOrder shippingOrder = shippingOrderService.getShippingOrder(id);
        List<ShippingOrderProduct> shippingOrderProductList = shippingOrderService.getShippingOrderProductList(id);
        List<ProductVO> productVOList = new ArrayList<>();
        try {
            shippingOrderProductList.forEach(s -> {
                Product product = productService.findProductById(s.getProductId());
                int processingId = s.getProcessingOrderId();
                ProductVO productVO = ProductVO.builder()
                        .processingOrderCode(processingId == 0 ? "" : processOrderService.getOne(processingId).getCode())
                        .productName(product.getName())
                        .type(product.getType())
                        .build();
                BeanUtils.copyProperties(s, productVO);
                productVOList.add(productVO);
            });
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
        List<ProcessingOrder> processingOrderList = processOrderService.findProcessingsByShipppingId(id);
        List<ProcessOrderIdCodeVO> processOrderIdCodeVOList = new ArrayList<>();
        processingOrderList.forEach(p -> {
            ProcessOrderIdCodeVO processOrderIdCodeVO = ProcessOrderIdCodeVO.builder()
                    .processingOrderId(p.getId())
                    .processingOrderCode(p.getCode())
                    .build();
            processOrderIdCodeVOList.add(processOrderIdCodeVO);
        });
        double totalWeight = productVOList.stream().mapToDouble(ProductVO::getWeight).sum();
        ShippingOrderVO shippingOrderVO = ShippingOrderVO.builder()
                .customerName(customerService.getCustomer(shippingOrder.getCustomerId()).getName())
                .createdByName(userService.getUser(shippingOrder.getCreatedBy()).getName())
                .arrearOrderCode(arrearOrderService.getOne(shippingOrder.getArrearOrderId()).getCode())
                .processingOrderIdsCodes(processOrderIdCodeVOList)
                .city(CityEnum.of(shippingOrder.getCity()).getMessage())
                .totalWeight(totalWeight)
                .products(productVOList)
                .build();
        BeanUtils.copyProperties(shippingOrder, shippingOrderVO);
        if (shippingOrder.getDeletedBy() != 0) {
            shippingOrderVO.setDeletedByName(userService.getUser(shippingOrder.getDeletedBy()).getName());

        }
        return ResponseResult.ok(shippingOrderVO);
    }

    /**
     * 获取商品平均单价列表（分页）
     *
     * @param pageIndex
     * @param pageSize
     * @param name
     * @return
     */
    @InvokeControl
    @RequestMapping(value = "/productAvgPriceslist", method = RequestMethod.GET, name = "获取商品平均单价列表（分页）")
    public ResponseResult<ListResponse> findProductPricesByPages(@RequestParam(value = "pageIndex") int pageIndex,
                                                                 @RequestParam(value = "pageSize") int pageSize,
                                                                 @RequestParam(value = "name", required = false) String name) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createAt"));
        Page<ProductDetailVO> page = productService.findAllByPage(pageable, name, null);
        List<ProductAvgPriceVO> result = new ArrayList<>();
        page.getContent().forEach(p -> {
            double totalWeight = shippingOrderService.getTotalWeightByProductId(p.getId());
            double cash = shippingOrderService.getTotalCashByProductId(p.getId());
            double avg = cash / totalWeight;
            BigDecimal b = new BigDecimal(avg);
            double avg_new = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            ProductAvgPriceVO productAvgPriceVO = ProductAvgPriceVO.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .totalWeight(totalWeight)
                    .averagePrice(avg_new)
                    .build();
            result.add(productAvgPriceVO);
        });

        return ResponseResult.ok(ListResponseUtils.generateResponse(new PageImpl<>(result, pageable, page.getTotalElements()), pageIndex, pageSize));
    }
}

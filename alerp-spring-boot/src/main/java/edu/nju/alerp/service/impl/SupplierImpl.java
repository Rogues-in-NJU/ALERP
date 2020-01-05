package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.cache.Cache;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.SupplierDTO;
import edu.nju.alerp.entity.Supplier;
import edu.nju.alerp.enums.CityEnum;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.enums.SupplierStatus;
import edu.nju.alerp.repo.SupplierRepository;
import edu.nju.alerp.service.SupplierService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.util.TimeUtil;
import edu.nju.alerp.vo.SupplierListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 供货商服务接口实现类
 * @Author: yangguan
 * @CreateDate: 2019-12-25 22:12
 */
@Slf4j
@Service
public class SupplierImpl implements SupplierService, InitializingBean {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserService userService;

    @Resource
    private Cache<Integer, String> supplierNameCache;

    @Override
    public void afterPropertiesSet() throws Exception {
        supplierNameCache.putAll(supplierRepository.findAll().parallelStream()
                .map(s -> MutablePair.of(s.getId(), s.getName()))
                .collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight)));
    }

    @Override
    public List<SupplierListVO> findAll() {
        return supplierRepository.findAll().parallelStream()
                            .map(s -> SupplierListVO.builder()
                                        .id(s.getId())
                                        .name(s.getName())
                                        .description(s.getDescription())
                                        .createdAt(s.getCreateAt())
                                        .createdById(s.getCreateBy())
                                        .createdByName(userService.getUser(s.getId()).getName())
                                        .updateTime(s.getUpdateAt())
                                        .build()).collect(Collectors.toList());
    }

    @Override
    public Page<SupplierListVO> findSuppliersByPage(Pageable pageable, String name) {
        String city = CityEnum.of(CommonUtils.getCity()).getMessage();
        QueryContainer<Supplier> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.like("name", name));
            sp.add(ConditionFactory.equal("city", city));
        }catch (Exception e) {
            log.error("Value is null.");
        }
        List<Supplier> suppliers = null;
        if (sp.isEmpty())
            suppliers = supplierRepository.findAll();
        else
            suppliers = supplierRepository.findAll(sp);
        List<SupplierListVO> queryed = suppliers.parallelStream()
                                                        .map(s -> SupplierListVO.builder()
                                                                .id(s.getId())
                                                                .name(s.getName())
                                                                .description(s.getDescription())
                                                                .updateTime(s.getUpdateAt())
                                                                .createdAt(s.getCreateAt())
                                                                .createdById(s.getCreateBy())
                                                                .createdByName(userService.getUser(s.getCreateBy()).getName())
                                                                .build()).collect(Collectors.toList());
        return new PageImpl<>(queryed, pageable, queryed.size());
    }

    @Override
    public int addOrUpdateSupplier(SupplierDTO supplierDTO) {
        String city = CityEnum.of(CommonUtils.getCity()).getMessage();
        Supplier supplier = Supplier.builder().name(supplierDTO.getName())
                                              .city(city)
                                              .status(SupplierStatus.NORMAL.getCode())
                                              .description(supplierDTO.getDescription())
                                              .updateAt(DateUtils.getToday())
                                              .updateBy(CommonUtils.getUserId())
                                              .build();
        if (supplierDTO.getId() != null) {
            supplier.setId(supplierDTO.getId());
            Supplier originSupplier = supplierRepository.getOne(supplierDTO.getId());
            if (!supplierDTO.getUpdateTime().equals(originSupplier.getUpdateAt())) {
                throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "供货商信息已变更，请重新更新");
            }
        }else {
            supplier.setCreateAt(DateUtils.getToday());
            supplier.setCreateBy(CommonUtils.getUserId());
        }
        int res = supplierRepository.saveAndFlush(supplier).getId();
        supplierNameCache.put(res, supplier.getName());
        return res;
    }

    @Override
    public int deleteSupplier(int id) {
        Supplier supplier = Supplier.builder().status(SupplierStatus.DELETED.getCode())
                                              .deleteAt(DateUtils.getToday())
                                              .deleteBy(CommonUtils.getUserId()).build();
        return supplierRepository.saveAndFlush(supplier).getId();
    }

    @Override
    public String getSupplierName(int id) {
        String name = supplierNameCache.get(id);
        if (name == null) {
            Supplier supplier = supplierRepository.getOne(id);
            if (supplier != null) {
                name = supplier.getName();
                supplierNameCache.put(id, name);
            }
        }
        return name;
    }
}

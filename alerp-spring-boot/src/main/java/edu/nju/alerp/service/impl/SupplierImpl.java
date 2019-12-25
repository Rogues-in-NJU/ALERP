package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.SupplierDTO;
import edu.nju.alerp.entity.Supplier;
import edu.nju.alerp.enums.SupplierStatus;
import edu.nju.alerp.repo.SupplierRepository;
import edu.nju.alerp.service.SupplierService;
import edu.nju.alerp.util.TimeUtil;
import edu.nju.alerp.vo.SupplierListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
public class SupplierImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public List<SupplierListVO> findAll() {
        return supplierRepository.findAll().parallelStream()
                            .map(s -> SupplierListVO.builder()
                                        .id(s.getId())
                                        .name(s.getName())
                                        .description(s.getDescription())
                                        .createdAt(s.getCreateAt())
                                        .createdById(s.getCreateBy())
                                        //todo .createdByName
                                        .build()).collect(Collectors.toList());
    }

    @Override
    public Page<SupplierListVO> findSuppliersByPage(Pageable pageable, String name) {
        QueryContainer<Supplier> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.like("name", name));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        List<SupplierListVO> queryed = supplierRepository.findAll(sp).parallelStream()
                                                        .map(s -> SupplierListVO.builder()
                                                                .id(s.getId())
                                                                .name(s.getName())
                                                                .description(s.getDescription())
                                                                .createdAt(s.getCreateAt())
                                                                .createdById(s.getCreateBy())
                                                                //todo .createdByName
                                                                .build()).collect(Collectors.toList());
        return new PageImpl<>(queryed, pageable, queryed.size());
    }

    @Override
    public int addOrUpdateSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = Supplier.builder().id(supplierDTO.getId())
                                                .name(supplierDTO.getName())
                                                .status(SupplierStatus.NORMAL.getCode())
                                                .description(supplierDTO.getDescription())
                                                .createAt(TimeUtil.dateFormat(new Date()))
                                                // todo .createBy()
                                                .build();
        return supplierRepository.saveAndFlush(supplier).getId();
    }

    @Override
    public int deleteSupplier(int id) {
        Supplier supplier = Supplier.builder().status(SupplierStatus.DELETED.getCode()).build();
        return supplierRepository.saveAndFlush(supplier).getId();
    }
}

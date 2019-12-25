package edu.nju.alerp.service;

import edu.nju.alerp.dto.SupplierDTO;
import edu.nju.alerp.vo.SupplierListVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description: 供货商服务接口类
 * @Author: yangguan
 * @CreateDate: 2019-12-25 22:03
 */
public interface SupplierService {

    public List<SupplierListVO> findAll();

    public Page<SupplierListVO> findSuppliersByPage(Pageable pageable, String name);

    public int addOrUpdateSupplier(SupplierDTO supplierDTO);

    public int deleteSupplier(int id);
}

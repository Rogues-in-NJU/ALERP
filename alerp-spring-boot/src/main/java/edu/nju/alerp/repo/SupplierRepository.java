package edu.nju.alerp.repo;


import edu.nju.alerp.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description: 供货商Repo
 * @Author: yangguan
 * @CreateDate: 2019-12-25 21:49
 */
public interface SupplierRepository extends JpaRepository<Supplier, Integer> , JpaSpecificationExecutor<Supplier> {
}

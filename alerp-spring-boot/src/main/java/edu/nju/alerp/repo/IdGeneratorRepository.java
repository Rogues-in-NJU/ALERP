package edu.nju.alerp.repo;

import edu.nju.alerp.entity.IdGenerator;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: Id发号器repo
 * @Author: yangguan
 * @CreateDate: 2019-12-23 17:27
 */
public interface IdGeneratorRepository extends JpaRepository<IdGenerator, String> {
}

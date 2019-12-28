package edu.nju.alerp.repo;

import edu.nju.alerp.entity.IdGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

/**
 * @Description: Id发号器repo
 * @Author: yangguan
 * @CreateDate: 2019-12-23 17:27
 */
public interface IdGeneratorRepository extends JpaRepository<IdGenerator, String> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update IdGenerator id set id.currentCount = ?1, id.updateTime = ?2 where id.documents = ?3 ")
    public int updateIdByDocumentsType(
            @Param("currentCount")int count,
            @Param("updateTime")String updateTime,
            @Param("documentsType")String documentsType);
}

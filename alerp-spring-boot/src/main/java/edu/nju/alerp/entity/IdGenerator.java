package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description: 各单据的发号表
 * @Author: yangguan
 * @CreateDate: 2019-12-23 17:36
 */
@Data
@Builder
@Entity
@Table(name = "id_generator")
public class IdGenerator {

    @Id
    @GeneratedValue(generator = "documents")
    @GenericGenerator(name = "documents", strategy = "uuid")
    private String documents;

    private int currentCount;
    private String updateTime;
}

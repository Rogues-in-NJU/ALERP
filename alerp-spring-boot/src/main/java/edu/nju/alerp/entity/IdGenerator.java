package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @Description: 各单据的发号表
 * @Author: yangguan
 * @CreateDate: 2019-12-23 17:36
 */
@Data
@Builder
@Entity
@Table(name = "id_generator")
@AllArgsConstructor
@NoArgsConstructor
public class IdGenerator {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 单据的类型
     */
    @Column(unique = true, nullable = false)
    private String documents;

    /**
     * 当天某个单据的自增数
     */
    @Column(nullable = false)
    private int currentCount;

    /**
     * 最后一次更新时间
     */
    @Column(nullable = false)
    private String updateTime;
}

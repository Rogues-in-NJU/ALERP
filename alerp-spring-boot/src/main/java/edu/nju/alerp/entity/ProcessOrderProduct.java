package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "process_order_product")
public class ProcessOrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int process_order_id;
    private int product_id;
    private String specification;
    private int quantity;
    private double expected_weight;
}

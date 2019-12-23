package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@Table(name = "processing_order")
public class ProcessingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;
    private int status;
    private int customer_id;
    private int shipping_order_id;
    private String salesman;
    private Date create_at;
    private int create_by;
    private Date delete_at;
    private int delete_by;
    private Date update_at;
    private int update_by;

}

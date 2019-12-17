package edu.nju.alerp.customer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 客户Controller层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:23
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/customer")
public class CustomerController {
}

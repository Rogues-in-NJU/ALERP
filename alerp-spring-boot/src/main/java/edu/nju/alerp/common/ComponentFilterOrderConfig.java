package edu.nju.alerp.common;

import edu.nju.alerp.common.auth.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 定义Filter之间顺序的component
 * @Author: yangguan
 * @CreateDate: 2020-01-12 14:03
 */
@Configuration
public class ComponentFilterOrderConfig {

    @Bean
    public FilterRegistrationBean buildAuthFilter(){
        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new AuthFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(3);//order的数值越小 则优先级越高
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean builderLoginFilter(){
        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new LoginFilter());
        filterRegistrationBean.addUrlPatterns("/api/*");
        filterRegistrationBean.setOrder(2);//order的数值越小 则优先级越高
        return filterRegistrationBean;
    }
}

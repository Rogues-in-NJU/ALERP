package edu.nju.alerp.common.auth;

import edu.nju.alerp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ConditionalOnProperty(name = "alerp.auth.enable", havingValue = "true")
@EnableAspectJAutoProxy
@Configuration
public class AuthAutoConfiguration {

    @Bean
    public AuthAspect authAspect(@Autowired AuthService authService) {
        return new AuthAspect(authService);
    }
}

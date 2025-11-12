package com.pma.validators;

import org.springframework.context.ApplicationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ValidatorConfig {

    @Bean
    public LocalValidatorFactoryBean validator(ApplicationContext applicationContext) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setConstraintValidatorFactory(
            new SpringConstraintValidatorFactory(applicationContext.getAutowireCapableBeanFactory())
        );
        factoryBean.setApplicationContext(applicationContext);
        return factoryBean;
    }

}




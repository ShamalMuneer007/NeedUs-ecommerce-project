package com.needus.ecommerce;

import com.needus.ecommerce.controllers.CustomErrorController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@Slf4j
public class EcommerceApplication {


	public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
//    @Bean
//    public ErrorController customErrorController(ErrorAttributes errorAttributes) {
//        return new CustomErrorController(errorAttributes);
//    }
}

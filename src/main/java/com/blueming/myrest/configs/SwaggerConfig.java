package com.blueming.myrest.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

// spring boot 2.2.0 버전 이후부터 Swagger2의 2.9.2 (메이저버전)이 동작하지 않음.
// Swagger2의 3.0.0릴리즈 버전으로 동작시켜야 동작함
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
   @Bean
   public Docket api(){
       return new Docket(DocumentationType.SWAGGER_2)
               .select()
               .apis(RequestHandlerSelectors.basePackage("org.springframework.boot"))
               .paths(PathSelectors.any())
               .build();
   }
}

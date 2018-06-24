package br.com.acmattos.bankslip.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configures Swagger UI application.
 * @author acmattos
 */
@Configuration
@EnableSwagger2
class SwaggerConfig {
   
   /**
    * Holds Swagger configuration for BankSlip API.
    * @return A builder which is intended to be the primary interface into the
    *         swagger-springmvc framework.
    */
   @Bean
   Docket api(){
      return new Docket(DocumentationType.SWAGGER_2)
         .select()
         .apis(RequestHandlerSelectors
            .basePackage("br.com.acmattos.bankslip"))
         .paths(PathSelectors.any())
         .build()
         .apiInfo(getApiInfo())
         .useDefaultResponseMessages(false);
   }
   
   /**
    * Gets Swagger API Info object.
    * @return ApiInfo
    */
   private ApiInfo getApiInfo(){
      return new ApiInfoBuilder()
         .title("BankSlip API")
         .description(
            "Endpoint's documentation of BankSlip API: a simple REST API to " +
            "deal with bank slip creation, payment, cancelation and " +
            "fine calculation.")
         .version("1.0.0")
         .contact(new Contact("André Mattos",
            "https://github.com/acmattos/bankslip",
            "acmattos@gmail.com"))
         .build();
   }
}

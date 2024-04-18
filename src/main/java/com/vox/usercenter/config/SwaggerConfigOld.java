//package com.vox.usercenter.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//
///**
// * 自定义Swagger接口文档的配置
// *
// */
//
////@Configuration //配置类
////@EnableSwagger2// 开启Swagger2的自动配置
//public class SwaggerConfig {
//    @Bean
//    public Docket docket() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                //enable设置是否启动Swagger
////                .enable(false)
//                //通过.select()方法，去配置扫描接口
//                .select()
//                //RequestHandlerSelectors配置如何扫描接口
//                .apis(RequestHandlerSelectors.basePackage("com.vox.usercenter.controller"))
//                // 配置如何通过path过滤,可以使用正则表达式
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    /**
//     * API 信息
//     * @return
//     */
//    //配置文档信息
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("vox用户中心")
//                .description("用户中心管理文档")
//                .termsOfServiceUrl("#")
//                .contact(new Contact("vox","voxcode.cn","voxcode@voxcode.cn"))
//                .version("1.0")
//                .build();
//    }
//
//}

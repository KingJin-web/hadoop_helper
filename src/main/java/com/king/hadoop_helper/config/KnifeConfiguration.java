package com.king.hadoop_helper.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.king.hadoop_helper.service.HdfsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @program: mooc
 * @description: Knife配置类
 * @author: King
 * @create: 2021-10-03 11:01
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class KnifeConfiguration  {
    @Value(value = "${config.swagger.enable}")  //通过 @Value  获取配置信息
    private Boolean swaggerEnable;
    // 复习@Environement  @Value    @ConfigurationProperties
    //日志输出级别，可选值为：trace，debug，info，warn，error，fatal
    //日志打印
    private static final Logger logger = LoggerFactory.getLogger(KnifeConfiguration.class);





    @Bean
    public Docket buildDocket() {
        logger.info("Swagger2 is enable: {}", swaggerEnable);
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                // 是否开启
                .enable(swaggerEnable)
                .select()
                // 要扫描的API(Controller)基础包
                .apis(RequestHandlerSelectors.basePackage("com.king.hadoop_helper.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo buildApiInfo() {
        Contact contact = new Contact("king","www.wuzhaoqi.top","jinpeng.qmail@qq.com");
        return new ApiInfoBuilder()
                .title("测试平台-平台管理API文档")
                .description("平台管理服务api")
                .contact(contact)
                .version("1.0.0").build();
    }


}

package de.gridsolut.test;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by ungerts on 22.04.14.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@ImportResource("classpath:META-INF/camelcontext.xml")
public class ESBPerformanceTest {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(ESBPerformanceTest.class, args);
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/camel/*");
        registrationBean.setLoadOnStartup(1);
        registrationBean.setName("CamelServlet");
        return registrationBean;
    }

}

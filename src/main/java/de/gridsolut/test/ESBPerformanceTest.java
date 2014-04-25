package de.gridsolut.test;


import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@ImportResource("classpath:META-INF/camelcontext.xml")
public class ESBPerformanceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ESBPerformanceTest.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(ESBPerformanceTest.class, args);
        Environment environment = ctx.getEnvironment();
        LOGGER.info("camel.http4.maxTotalConnections: " + environment.getProperty("camel.http4.maxTotalConnections"));
        LOGGER.info("tomcat.connector.minSpareThreads: " +environment.getProperty("tomcat.connector.minSpareThreads"));
    }


    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/camel/*");
        registrationBean.setLoadOnStartup(1);
        registrationBean.setName("CamelServlet");
        return registrationBean;
    }

}

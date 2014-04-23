package de.gridsolut.test;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

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

//    @Bean
//    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
//        TomcatEmbeddedServletContainerFactory fac = new TomcatEmbeddedServletContainerFactory();
//        fac.setPort(8080);
//        fac.addConnectorCustomizers(new TomcatConnectorCustomizer() {
//
//            @Override
//            public void customize(Connector connector) {
//                ProtocolHandler handler = connector.getProtocolHandler();
//                if (handler instanceof AbstractProtocol) {
//                    AbstractProtocol protocol = (AbstractProtocol) handler;
//                    protocol.setConnectionTimeout(20000);
//                    protocol.setMaxThreads(4);
//                    protocol.setMaxConnections(20000);
//                }
//                connector.setProperty("acceptCount", "20000");
//                connector.setProperty("maxKeepAliveRequests", "-1");
//            }
//        });
//        return fac;
//    }

}

package de.gridsolut.test;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmbeddedTomcatContainerCustomizer implements EmbeddedServletContainerCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedTomcatContainerCustomizer.class);

    @Value("${tomcat.connector.maxThreads}")
    private int maxThreads;

    @Value("${tomcat.connector.connectionTimeout}")
    private int connectionTimeout;

    @Value("${tomcat.connector.maxConnections}")
    private int maxConnections;

    @Value("${tomcat.connector.acceptCount}")
    private int acceptCount;

    @Value("${tomcat.connector.maxKeepAliveRequests}")
    private int maxKeepAliveRequests;

    @Value("${tomcat.connector.bindOnInit}")
    private boolean bindOnInit;

    @Value("${tomcat.connector.maxHttpHeaderSize}")
    private int maxHttpHeaderSize;

    @Value("${tomcat.connector.acceptorThreadCount}")
    private int acceptorThreadCount;

    @Value("${tomcat.connector.minSpareThreads}")
    private int minSpareThreads;

    @Override
    public void customize(ConfigurableEmbeddedServletContainer factory) {

        customizeTomcatConnector((TomcatEmbeddedServletContainerFactory) factory);
    }

    private void customizeTomcatConnector(TomcatEmbeddedServletContainerFactory factory) {
        LOGGER.info("Configuring Tomcat Connector");
        factory.addConnectorCustomizers(
                new TomcatConnectorCustomizer() {

                    @Override
                    public void customize(Connector connector) {
                        ProtocolHandler handler = connector.getProtocolHandler();
                        if (handler instanceof AbstractProtocol) {
                            AbstractProtocol protocol = (AbstractProtocol) handler;
                            protocol.setMaxThreads(maxThreads);
                            protocol.setConnectionTimeout(connectionTimeout);
                            protocol.setMaxConnections(maxConnections);
                            protocol.setMinSpareThreads(minSpareThreads);

                        }
                        connector.setProperty("acceptCount", acceptCount+"");
                        connector.setProperty("maxKeepAliveRequests", maxKeepAliveRequests+"");
                        connector.setProperty("bindOnInit", bindOnInit + "");
                        connector.setProperty("maxHttpHeaderSize", maxHttpHeaderSize + "");
                        connector.setProperty("acceptorThreadCount", acceptorThreadCount + "");
                    }
                }
        );
    }
}

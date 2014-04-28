package de.gridsolut.test.routes;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class XsltProxy extends SpringRouteBuilder {

    @Value("${camel.echoService.endpointProtocol}")
    private String endpointProtocol;

    @Value("${camel.echoService.endpointHost}")
    private String endpointHost;

    @Value("${camel.echoService.endpointPort}")
    private String endpointPort;

    @Value("${camel.echoService.endpointPath}")
    private String endpointPath;

    @Value("${camel.http4.maxTotalConnections}")
    private String maxTotalConnections;

    @Value("${camel.http4.connectionsPerRoute}")
    private String connectionsPerRoute;

    @Override
    public void configure() throws Exception {
        //TODO: allowStAX
        from("servlet:///xsltProxy?matchOnUriPrefix=true")
                .to("xslt:xslt/transfrom_reverse.xslt")
                .to(endpointProtocol + "4://" + endpointHost + ":" + endpointPort + endpointPath + "?bridgeEndpoint=true&maxTotalConnections=" + maxTotalConnections + "&connectionsPerRoute=" + connectionsPerRoute)
                .to("xslt:xslt/transform.xslt");
    }
}

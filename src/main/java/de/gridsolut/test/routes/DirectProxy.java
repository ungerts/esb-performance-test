package de.gridsolut.test.routes;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DirectProxy extends SpringRouteBuilder {

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
        from("servlet:///directProxy?matchOnUriPrefix=true")
                .to(endpointProtocol + "4://" + endpointHost + ":" + endpointPort + endpointPath + "?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&maxTotalConnections=" + maxTotalConnections +"&connectionsPerRoute=" + connectionsPerRoute);
    }
}

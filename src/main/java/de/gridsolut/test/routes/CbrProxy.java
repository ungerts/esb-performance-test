package de.gridsolut.test.routes;

import de.gridsolut.test.util.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CbrProxy extends SpringRouteBuilder {

    public static String FAULT_STRING = "First order in message must be for the symbol IBM";

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

    private String faultMessage;

    @PostConstruct
    private void initialize() throws Exception {
        faultMessage = MessageUtils.createSoapFaultString(FAULT_STRING);
    }

    @Override
    public void configure() throws Exception {
        final String faultMessage = this.faultMessage;
        from("servlet:///cbrProxy?matchOnUriPrefix=true")
                .routeId("CbrProxy")
                .choice()
                .when().xpath("//order[1]/symbol/text() = 'IBM'").process(new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(faultMessage);
                in.setHeader(Exchange.HTTP_RESPONSE_CODE, new Integer(500));
            }

        })
                .otherwise().to(endpointProtocol + "4://" + endpointHost + ":" + endpointPort + endpointPath + "?bridgeEndpoint=true&throwExceptionOnFailure=false&maxTotalConnections=" + maxTotalConnections + "&connectionsPerRoute=" + connectionsPerRoute);
    }


}

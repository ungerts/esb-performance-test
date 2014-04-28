package de.gridsolut.test.routes;

import de.gridsolut.test.util.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.converter.CxfPayloadConverter;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.commons.codec.net.URLCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.soap.SOAPMessage;

@Component
public class CbrProxyCxf extends SpringRouteBuilder {

    public static String FAULT_STRING = "First order in message must be for the symbol IBM";

    @Value("${camel.echoService.endpointProtocol}")
    private String endpointProtocol;

    @Value("${camel.echoService.endpointHost}")
    private String endpointHost;

    @Value("${camel.echoService.endpointPort}")
    private String endpointPort;

    @Value("${camel.echoService.endpointPath}")
    private String endpointPath;

    private SOAPMessage faultMessage;

    @PostConstruct
    private void initialize() throws Exception {
        faultMessage = MessageUtils.createSoapFault(FAULT_STRING);
    }

    @Override
    public void configure() throws Exception {
        //TODO: allowStreaming
        //TODO: activate data format 'MESSAGE'
        String address = endpointProtocol + "://" + endpointHost + ":" + endpointPort + endpointPath;
        URLCodec encoder = new URLCodec();
        from("cxf:/cbrProxy?wsdlURL=wsdl/echoService.wsdl&dataFormat=PAYLOAD&serviceName={http://services.samples/xsd}EchoService&endpointName={http://services.samples/xsd}EchoServicePort")
                .routeId("CbrProxyCxf")
                .choice()
                .when().xpath("//order[1]/symbol/text() = 'IBM'").process(new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(CxfPayloadConverter.elementToCxfPayload(faultMessage.getSOAPBody().extractContentAsDocument().getDocumentElement(), exchange));
                in.setHeader(Exchange.HTTP_RESPONSE_CODE, new Integer(500));
            }

        })
                .otherwise().to("cxf:/cbrProxyCall?wsdlURL=wsdl/echoService.wsdl&dataFormat=PAYLOAD&serviceName={http://services.samples/xsd}EchoService&endpointName={http://services.samples/xsd}EchoServicePort&address=" + encoder.encode(address));
    }
}

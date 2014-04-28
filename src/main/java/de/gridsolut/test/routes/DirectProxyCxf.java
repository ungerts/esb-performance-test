package de.gridsolut.test.routes;

import org.apache.camel.Exchange;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DirectProxyCxf extends SpringRouteBuilder {

    @Value("${camel.echoService.endpointProtocol}")
    private String endpointProtocol;

    @Value("${camel.echoService.endpointHost}")
    private String endpointHost;

    @Value("${camel.echoService.endpointPort}")
    private String endpointPort;

    @Value("${camel.echoService.endpointPath}")
    private String endpointPath;

    @Override
    public void configure() throws Exception {
        //TODO: allowStreaming
        String address = endpointProtocol + "://" + endpointHost + ":" + endpointPort + endpointPath;
        URLCodec encoder = new URLCodec();
        from("cxf:/directProxy?wsdlURL=wsdl/echoService.wsdl&dataFormat=MESSAGE&serviceName={http://services.samples/xsd}EchoService&endpointName={http://services.samples/xsd}EchoServicePort")
                .to("cxf:/directProxyCall?wsdlURL=wsdl/echoService.wsdl&dataFormat=MESSAGE&serviceName={http://services.samples/xsd}EchoService&endpointName={http://services.samples/xsd}EchoServicePort&address=" + encoder.encode(address));
    }
}

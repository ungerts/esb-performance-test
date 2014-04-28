package de.gridsolut.test.util;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;


public class MessageUtils {

    public static SOAPMessage createSoapFault(String faultString) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage message = messageFactory.createMessage();
        QName qName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
        message.getSOAPBody().addFault(qName, faultString);
        return message;
    }

    public static String createSoapFaultString(String faultString) throws Exception {
        SOAPMessage message = createSoapFault(faultString);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);
        return new String(outputStream.toByteArray());
    }

}

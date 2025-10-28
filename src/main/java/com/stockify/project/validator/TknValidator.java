package com.stockify.project.validator;

import jakarta.xml.soap.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TknValidator {

    private static final String NVI_SERVICE_URL = "https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx";

    public static boolean validateTkn(String tkn, String firstName, String lastName, int birthDate) {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("tckn", "http://tckimlik.nvi.gov.tr/WS");
            SOAPBody soapBody = envelope.getBody();
            SOAPElement soapBodyElem = soapBody.addChildElement("TCKimlikNoDogrula", "tckn");
            SOAPElement tknElem = soapBodyElem.addChildElement("TCKimlikNo");
            tknElem.addTextNode(tkn);
            SOAPElement firstNameElem = soapBodyElem.addChildElement("Ad");
            firstNameElem.addTextNode(firstName);
            SOAPElement lastNameElem = soapBodyElem.addChildElement("Soyad");
            lastNameElem.addTextNode(lastName);
            SOAPElement birthDateElem = soapBodyElem.addChildElement("DogumYili");
            birthDateElem.addTextNode(String.valueOf(birthDate));
            soapMessage.saveChanges();
            SOAPMessage soapResponse = soapConnection.call(soapMessage, NVI_SERVICE_URL);
            SOAPBody responseBody = soapResponse.getSOAPBody();
            String result = responseBody.getTextContent();
            soapConnection.close();
            return "true".equalsIgnoreCase(result.trim());
        } catch (Exception e) {
            return false;
        }
    }
}

package com.milan.iis_backend.service.implementation;

import com.milan.iis_backend.model.OktaUserXml;
import com.milan.iis_backend.service.XmlImportService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class OktaXmlImportService implements XmlImportService {
    @Override
    public OktaUserXml validateAndParse(InputStream xmlString) throws Exception {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory
                .newSchema(new StreamSource(new ClassPathResource("schema/okta-user.xsd").getInputStream()));

        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xmlString));

        throw new IllegalStateException("Use validateAndParse(byte[]) overload");
    }

    @Override
    public OktaUserXml validateAndParse(byte[] xmlBytes) throws Exception {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new StreamSource(new ClassPathResource("schema/okta-user.xsd").getInputStream()));
        var validator = schema.newValidator();
        validator.validate(new StreamSource(new java.io.ByteArrayInputStream(xmlBytes)));

        JAXBContext context = JAXBContext.newInstance(OktaUserXml.class);
        Unmarshaller um = context.createUnmarshaller();
        return (OktaUserXml) um.unmarshal(new ByteArrayInputStream(xmlBytes));
    }
}

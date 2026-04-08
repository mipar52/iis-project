package com.milan.iis_backend.module_import;

import com.milan.iis_backend.model.okta.dto.xml.OktaUserXml;
import com.milan.iis_backend.module_import.interfaces.XmlImportService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class OktaXmlImportService implements XmlImportService {
    @Override
    public OktaUserXml validateAndParse(InputStream xmlStream) throws Exception {
        byte[] xmlBytes = xmlStream.readAllBytes();
        return validateAndParse(xmlBytes);
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

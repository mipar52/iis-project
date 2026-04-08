package com.milan.iis_backend.module_soap.services;

import com.milan.iis_backend.module_soap.interfaces.XmlValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Service
public class OktaUserXmlValidator implements XmlValidator {
    @Override
    public List<String> validate(Path xmlFile, String xsdClasspathPath) throws Exception {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = schemaFactory.newSchema(new StreamSource(new ClassPathResource(xsdClasspathPath).getInputStream()));
        Validator validator = schema.newValidator();

        List<String> errors = new ArrayList<>();

        validator.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException { errors.add("WARNING: " + exception);}
            @Override
            public void error(SAXParseException exception) throws SAXException { errors.add("ERROR: " + exception); }
            @Override
            public void fatalError(SAXParseException exception) throws SAXException { errors.add("FATAL ERROR: " + exception); }
        });
        try {
            validator.validate(new StreamSource(xmlFile.toFile()));
        } catch (Exception e) {
            if (errors.isEmpty()) errors.add("Exception: " + e.getMessage());
        }
        return errors;
    }
}

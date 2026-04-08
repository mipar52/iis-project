package com.milan.iis_backend.module_soap.interfaces;

import java.nio.file.Path;
import java.util.List;

public interface XmlValidator {
    public List<String> validate(Path xmlFile, String xsdClasspathPath) throws Exception;
}

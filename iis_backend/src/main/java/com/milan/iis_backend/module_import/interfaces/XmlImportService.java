package com.milan.iis_backend.module_import.interfaces;

import com.milan.iis_backend.model.okta.dto.xml.OktaUserXml;

import java.io.InputStream;

public interface XmlImportService {
    public OktaUserXml validateAndParse(InputStream xmlString) throws Exception;
    public OktaUserXml validateAndParse(byte[] xmlBytes) throws Exception;
}

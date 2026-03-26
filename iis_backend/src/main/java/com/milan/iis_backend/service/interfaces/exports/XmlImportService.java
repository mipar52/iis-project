package com.milan.iis_backend.service.interfaces.exports;

import com.milan.iis_backend.model.okta.OktaUserXml;

import java.io.InputStream;

public interface XmlImportService {
    public OktaUserXml validateAndParse(InputStream xmlString) throws Exception;
    public OktaUserXml validateAndParse(byte[] xmlBytes) throws Exception;
}

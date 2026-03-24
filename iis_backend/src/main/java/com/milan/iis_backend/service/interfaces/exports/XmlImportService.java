package com.milan.iis_backend.service;

import com.milan.iis_backend.model.OktaUserXml;

import java.io.InputStream;

public interface XmlImportService {
    public OktaUserXml validateAndParse(InputStream xmlString) throws Exception;
    public OktaUserXml validateAndParse(byte[] xmlBytes) throws Exception;
}

package com.milan.iis_backend.service;

import com.milan.iis_backend.model.OktaUserJson;

public interface JsonImportService {
    public OktaUserJson validateAndParse(byte[] jsonBytes) throws Exception;
}

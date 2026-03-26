package com.milan.iis_backend.service.interfaces.exports;

import com.milan.iis_backend.model.okta.OktaUserJson;

public interface JsonImportService {
    public OktaUserJson validateAndParse(byte[] jsonBytes) throws Exception;
}

package com.milan.iis_backend.service.interfaces.exports;

import com.milan.iis_backend.model.okta.dto.OktaUserDto;
import com.milan.iis_backend.model.okta.dto.json.OktaUserJson;

public interface JsonImportService {
    public OktaUserDto validateAndParse(byte[] jsonBytes) throws Exception;
}

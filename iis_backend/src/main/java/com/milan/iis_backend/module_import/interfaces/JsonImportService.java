package com.milan.iis_backend.module_import.interfaces;

import com.milan.iis_backend.model.okta.dto.OktaUserDto;

public interface JsonImportService {
    public OktaUserDto validateAndParse(byte[] jsonBytes) throws Exception;
}

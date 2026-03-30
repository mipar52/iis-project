package com.milan.iis_backend.service.interfaces.imports;

import com.milan.iis_backend.model.okta.OktaUser;

import java.nio.file.Path;
import java.util.List;

public interface XmlExportService {
    public Path writeUsersXmlToDisk(List<OktaUser> users, Path outputFile) throws Exception;
    public List<OktaUser> filterUsersFromXmlByXPath(Path xmlFile, String searchTerm, Boolean isExact) throws Exception;
    public org.w3c.dom.Document toDocument(List<OktaUser> users) throws Exception;
}

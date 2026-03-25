package com.milan.iis_backend.soap;

import com.milan.iis_backend.model.OktaUser;
import com.milan.iis_backend.service.implementation.OktaUserService;
import com.milan.iis_backend.service.interfaces.imports.XmlExportService;
import com.milan.iis_backend.soap.gen.SearchOktaUsersRequest;
import com.milan.iis_backend.soap.gen.SearchOktaUsersResponse;
import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.nio.file.Path;
import java.util.List;

@Endpoint
@RequiredArgsConstructor
public class OktaUserEndpoint {
    private static final String NAMESPACE_URI = "http://milan.com/iis/oktauser/soap";

    private final OktaUserService oktaUserService;
    private final XmlExportService xmlExportService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchOktaUsersRequest")
    @ResponsePayload
    public SearchOktaUsersResponse search(@RequestPayload JAXBElement<SearchOktaUsersRequest> requestEl) throws Exception {
        SearchOktaUsersRequest request = requestEl.getValue();

        String term = request.getTerm();
        if (term == null) term = "";
        term = term.trim();

        List<OktaUser> allUsers = oktaUserService.findAll();

        Path xmlPath = Path.of("./generated/okta-users.xml");
        xmlExportService.writeUsersXmlToDisk(allUsers, xmlPath);

        List<OktaUser> filtered = xmlExportService.filterUsersFromXmlByXPath(xmlPath, term);

        SearchOktaUsersResponse response = new SearchOktaUsersResponse();
        SearchOktaUsersResponse.Users usersWrapper = new SearchOktaUsersResponse.Users();

        for (OktaUser u : filtered) {
            SearchOktaUsersResponse.Users.User soapUser = new SearchOktaUsersResponse.Users.User();
            if (u.getId() != null) soapUser.setId(u.getId());
            soapUser.setFirstName(u.getFirstName());
            soapUser.setLastName(u.getLastName());
            soapUser.setEmail(u.getEmail());
            soapUser.setLogin(u.getLogin());
            soapUser.setMobilePhone(u.getMobilePhone());
            soapUser.setSourceType(u.getSourceType());
            usersWrapper.getUser().add(soapUser);
        }

        response.setUsers(usersWrapper);
        return response;
    }
}
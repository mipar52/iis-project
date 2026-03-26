package com.milan.iis_backend.soap;

import com.milan.iis_backend.model.okta.OktaUser;
import com.milan.iis_backend.service.implementation.OktaUserService;
import com.milan.iis_backend.service.interfaces.imports.XmlExportService;
import com.milan.iis_backend.soap.gen.SearchOktaUsersRequest;
import com.milan.iis_backend.soap.gen.SearchOktaUsersResponse;
import com.milan.iis_backend.validation.XmlValidator;
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
    private final XmlValidator xmlValidator;

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

        // tocka 3 - xml validacija
        List<String> errors = xmlValidator.validate(xmlPath, "schema/okta-users-export.xsd");
        if (!errors.isEmpty()) { throw new IllegalArgumentException("Generated XML is not valid: " + String.join(" | ", errors)); }

        List<OktaUser> filtered = xmlExportService.filterUsersFromXmlByXPath(xmlPath, term);

        SearchOktaUsersResponse response = new SearchOktaUsersResponse();
        SearchOktaUsersResponse.Users usersWrapper = new SearchOktaUsersResponse.Users();

        for (OktaUser u : filtered) {
            SearchOktaUsersResponse.Users.User soapUser = new SearchOktaUsersResponse.Users.User();
            if (u.getId() != null) soapUser.setId(u.getId());
            soapUser.setFirstName(u.getProfile().getFirstName());
            soapUser.setLastName(u.getProfile().getLastName());
            soapUser.setEmail(u.getProfile().getEmail());
            soapUser.setLogin(u.getProfile().getLogin());
            soapUser.setMobilePhone(u.getProfile().getMobilePhone());
           // soapUser.setSourceType(u.getProfile().getSourceType());
            usersWrapper.getUser().add(soapUser);
        }

        response.setUsers(usersWrapper);
        return response;
    }
}
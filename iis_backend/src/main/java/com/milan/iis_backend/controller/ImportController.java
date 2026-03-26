package com.milan.iis_backend.controller;

import com.milan.iis_backend.model.okta.OktaUser;
import com.milan.iis_backend.model.okta.OktaUserJson;
import com.milan.iis_backend.model.okta.OktaUserProfile;
import com.milan.iis_backend.model.okta.OktaUserXml;
import com.milan.iis_backend.repository.UserRepository;
import com.milan.iis_backend.service.interfaces.exports.JsonImportService;
import com.milan.iis_backend.service.interfaces.exports.XmlImportService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/import")
@AllArgsConstructor
public class ImportController {
    private final XmlImportService xmlImportService;
    private final JsonImportService jsonImportService;
    private final UserRepository userRepository;

    @PostMapping(value = "/okta-user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importOktaUser(
            @RequestPart(value = "xmlFile", required = false) MultipartFile xmlFile,
            @RequestPart(value = "jsonFile", required = false) MultipartFile jsonValue) {
        if ((xmlFile == null || xmlFile.isEmpty()) && (jsonValue == null || jsonValue.isEmpty())) {
            return ResponseEntity.badRequest().body("Need to send either an XML file or a JSON file!!!!!!!!");
        }

        List<String> savedIds = new ArrayList<>();
        List<ValidationError> validationErrors = new ArrayList<>();

        if (xmlFile != null && !xmlFile.isEmpty()) {
            try {
                byte[] xmlBytes = xmlFile.getBytes();
                OktaUserXml dto = xmlImportService.validateAndParse(xmlBytes);

                OktaUser user = new OktaUser();
                OktaUserProfile profile = new OktaUserProfile();
                profile.setFirstName(dto.firstName);
                profile.setLastName(dto.lastName);
                profile.setMobilePhone(dto.mobilePhone);
                profile.setEmail(dto.email);
                profile.setLogin(dto.login);
                user.setProfile(profile);
              //  profile.setSourceType("xml");

                savedIds.add(userRepository.save(user).getId());

            } catch (Exception ex) {
                validationErrors.add(new ValidationError("xmlFile", ex.getMessage()));
            }
        }

        if (jsonValue != null && !jsonValue.isEmpty()) {
            try {
                byte[] jsonBytes = jsonValue.getBytes();
                OktaUserJson dto = jsonImportService.validateAndParse(jsonBytes);

                OktaUser user = new OktaUser();
                OktaUserProfile profile = new OktaUserProfile();

                profile.setFirstName(dto.firstName);
                profile.setLastName(dto.lastName);
                profile.setMobilePhone(dto.mobilePhone);
                profile.setEmail(dto.email);
                profile.setLogin(dto.login);
                user.setProfile(profile);
            } catch (Exception ex) {
                validationErrors.add(new ValidationError("jsonFile", ex.getMessage()));
            }
        }
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImportResult(savedIds, validationErrors));
        }
        return ResponseEntity.ok(new ImportResult(savedIds, List.of()));
    }

    @GetMapping("/users")
    public List<OktaUser> getUsers() {
        return userRepository.findAll();
    }
    public record ValidationError(String field, String message) {}
    public record ImportResult(List<String> savedIds, List<ValidationError> errors) {}
}

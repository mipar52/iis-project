package com.milan.iis_backend.service.implementation;

import com.milan.iis_backend.exceptions.JsonSchemaValidationException;
import com.milan.iis_backend.model.okta.dto.OktaUserDto;
import com.milan.iis_backend.service.interfaces.exports.JsonImportService;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Set;
import com.networknt.schema.*;
import java.util.stream.Collectors;

@Service
public class OktaJsonImportService implements JsonImportService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSchema jsonSchema;

    public OktaJsonImportService() throws Exception {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        try (InputStream inputStream = new ClassPathResource("schema/okta-user.schema.json").getInputStream()) {
            this.jsonSchema = factory.getSchema(inputStream);
        }
    }

    @Override
    public OktaUserDto validateAndParse(byte[] jsonBytes) throws Exception {
        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(jsonBytes);
        Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);

        if (!errors.isEmpty()) {
            String errorMessage = errors.stream().map(ValidationMessage::getMessage).collect(Collectors.joining("; "));
            throw new JsonSchemaValidationException(errors, errorMessage);
        }
        return objectMapper.treeToValue(jsonNode, OktaUserDto.class);
    }
}

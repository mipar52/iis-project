package com.milan.iis_backend.model.exceptions;

import com.networknt.schema.ValidationMessage;

import java.util.Set;

public class JsonSchemaValidationException extends  RuntimeException {
    private final Set<ValidationMessage> errors;

    public JsonSchemaValidationException(Set<ValidationMessage> errors, String message) {
        super(message);
        this.errors = errors;
    }

    public Set<ValidationMessage> getErrors() { return errors; }
}

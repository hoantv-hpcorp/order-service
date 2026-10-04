package com.polarbookshop.orderservice.book;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BookErrorResponse {

    private final Map<String, String> errors = new HashMap<>();

    @JsonAnySetter
    public void add(String name, String value) {
        this.errors.put(name, value);
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    @JsonIgnore
    public String getFirstErrorMessage() {
        return errors.values().stream().findFirst().orElse(null);
    }

}

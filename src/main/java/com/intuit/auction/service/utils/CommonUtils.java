package com.intuit.auction.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

@Slf4j
public class CommonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String toJson(Object body, Boolean prettyPrint) {
        try {
            if (ObjectUtils.isNotEmpty(body) && body instanceof String) {
                return (String) body;
            }
            if (Boolean.TRUE.equals(prettyPrint)) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
            } else {
                return objectMapper.writeValueAsString(body);
            }
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

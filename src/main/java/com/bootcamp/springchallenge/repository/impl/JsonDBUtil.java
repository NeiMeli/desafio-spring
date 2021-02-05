package com.bootcamp.springchallenge.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;

import java.io.File;

public class JsonDBUtil {
    public static JsonNode parseDatabase(String path) throws Exception {
        File file = ResourceUtils.getFile(path);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(file);
    }
}

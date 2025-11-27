package com.example.inventory_service.util;

import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ProtoMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T convertStruct(Struct struct, Class<T> clazz) {
        try {
            String json = JsonFormat.printer()
                    .omittingInsignificantWhitespace()
                    .print(struct);

            return objectMapper.readValue(json, clazz);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Struct to DTO", e);
        }
    }

    public static <T> List<T> convertStructList(List<Struct> structs, Class<T> clazz) {
        List<T> result = new ArrayList<>();

        for (Struct struct : structs) {
            result.add(convertStruct(struct, clazz));
        }

        return result;
    }

}

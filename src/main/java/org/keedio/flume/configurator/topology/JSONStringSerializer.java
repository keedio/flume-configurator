package org.keedio.flume.configurator.topology;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONStringSerializer {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    private JSONStringSerializer() {
        // nothing to do, really
    }

    public static String toJSONString(Object object) throws IOException {
        return mapper. writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    public static <T> T fromJSONString(String string, Class<T> clazz) throws IOException {
        return mapper.readValue(string, clazz);
    }

    public static <T> T fromJSONString(String string, JavaType javaType) throws IOException {
        return mapper.readValue(string, javaType);
    }

    public static byte[] toBytes(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) throws IOException {
        return mapper.readValue(bytes, clazz);
    }

    public static <T> T fromBytes(byte[] bytes, JavaType javaType) throws IOException {
        return mapper.readValue(bytes, javaType);
    }

    public static JavaType getJavaType(Class clazz, Class clazz2) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(clazz, clazz2);
        return javaType;
    }

}

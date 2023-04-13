package ru.digitalhabits.homework3;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestUtils {

    private static final String EXAMPLE_FILES_PATH_TEMPLATE = "src/test/resources/examples/%s/%s";
    public static final String API_PATH_TEMPLATE = "/api/v1/%ss";

    public static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, File> retrieveExampleFiles(String entityName, String entityType) {
        File examplesDirectory = new File(String.format(EXAMPLE_FILES_PATH_TEMPLATE, entityName, entityType));
        File[] exampleFiles = examplesDirectory.listFiles();
        return Arrays.stream(Objects.requireNonNull(exampleFiles))
                .collect(Collectors.toMap(File::getName, Function.identity()));
    }
}

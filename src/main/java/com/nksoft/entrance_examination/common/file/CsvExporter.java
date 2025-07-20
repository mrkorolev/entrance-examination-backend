package com.nksoft.entrance_examination.common.file;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Function;

@Component
public class CsvExporter {
    public <T> ByteArrayResource exportToCsv(String headerRow, List<T> data, Function<T, String> mapper) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println(headerRow);
            for (T item : data) {
                writer.println(mapper.apply(item));
            }
        }
        return new ByteArrayResource(out.toByteArray());
    }
}

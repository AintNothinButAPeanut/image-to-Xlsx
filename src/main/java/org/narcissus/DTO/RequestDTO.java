package org.narcissus.DTO;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.stream.Stream;

public record RequestDTO(String uuid, Stream<MultipartFile> files, int size) {
    public RequestDTO {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(files);
    }

}

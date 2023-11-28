package org.narcissus.DTO;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Objects;

public record RequestDTO(String id, Collection<MultipartFile> files, short size) {
    public RequestDTO {
        Objects.requireNonNull(id);
        Objects.requireNonNull(files);
    }

}

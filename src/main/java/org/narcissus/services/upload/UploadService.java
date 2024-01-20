package org.narcissus.services.upload;

import org.narcissus.DTO.RequestDTO;

import java.util.Optional;

@FunctionalInterface
public interface UploadService {

    Optional<String> uploadFiles(RequestDTO files);

}

package com.spring.beatmarket.infrastructure.upload;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Component
public class UploadFileValidator {
    private static final String EMPTY_FILE_MESSAGE = "Uploaded file must not be empty.";

    public void validate(final MultipartFile file, final UploadFilePolicy policy) {
        if (file.isEmpty()) {
            throw new InvalidUploadFileException(EMPTY_FILE_MESSAGE);
        }

        String filename = file.getOriginalFilename();
        String lowercaseFilename = filename == null ? null : filename.toLowerCase(Locale.ROOT);
        boolean hasAllowedExtension = lowercaseFilename != null
                && policy.allowedExtensions().stream().anyMatch(lowercaseFilename::endsWith);
        if (!hasAllowedExtension) {
            throw new InvalidUploadFileException(policy.invalidFormatMessage());
        }
        if (file.getSize() > policy.maxSizeBytes()) {
            throw new UploadFileTooBigException(file.getSize(), policy.maxSizeBytes());
        }
    }
}

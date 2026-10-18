package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.shared.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class FileKeyGenerator {

    String generate(final String directory, final String name, final String extension) {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        return directory + "/" + StringUtils.createSlug(name)
                + "-" + uniqueSuffix + "." + extension;
    }
}

package es.dawgrupo2.zendashop.dto;

import java.util.List;

public record UserBasicDTO(
        Long id,
        String username,
        String email,
        List<String> roles,
        ImageDTO avatar
    ) {
}

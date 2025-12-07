package com.bracepl.dbp_onboarding_service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class ValidationUtils {

    public static boolean hasNullOrEmptyField(Object obj) {
        if (obj == null) {
            log.error("PASSED NULL OBJECT...");
            return true;
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            // Skip the fieldsToUpdate field
            if ("fieldsToUpdate".equals(field.getName())) {
                continue;
            }

            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                if (value == null) {
                    log.error("{} IS NULL", field.getName());
                    return true;
                }

                if (value instanceof String && !StringUtils.hasText((String) value)) {
                    log.error("{} IS EMPTY", field.getName());
                    return true;
                }

                if (value instanceof MultipartFile && ((MultipartFile) value).isEmpty()) {
                    log.error("{} HAS NO FILE", field.getName());
                    return true;
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean hasEmptyFile(List<MultipartFile> files){
        for(MultipartFile file: files){
            if (file != null && file.isEmpty()) {
                log.error("{} HAS NO FILE", file.getOriginalFilename());
                return true;
            }
        }
        return false;
    }


}

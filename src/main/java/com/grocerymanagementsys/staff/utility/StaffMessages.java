package com.grocerymanagementsys.staff.utility;

import com.grocerymanagementsys.common.exceptions.EmailAlreadyUsedException;
import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StaffMessages {

    public static String friendly(Exception e, String fallback) {
        if (e instanceof ValidationException || e instanceof ResourceNotFoundException || e instanceof EmailAlreadyUsedException || e instanceof IllegalArgumentException) {
            return e.getMessage();
        }
        log.error("Staff action failed", e);
        return fallback;
    }
}

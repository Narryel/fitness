package com.narryel.fitness.bot.handlers.input.validation;


import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

@Data
@Builder
public class ValidationResult {

    public static final ValidationResult VALID_INPUT_MESSAGE = ValidationResult.builder()
            .isMessageValid(true)
            .message(Strings.EMPTY)
            .build();

    public static ValidationResult getInvalidResult(String message) {
        return ValidationResult.builder()
                .isMessageValid(false)
                .message(message)
                .build();
    }

    private final boolean isMessageValid;

    private final String message;
}

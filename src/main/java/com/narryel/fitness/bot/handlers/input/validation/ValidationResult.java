package com.narryel.fitness.bot.handlers.input.validation;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;

@Data
@Builder
@RequiredArgsConstructor( access = AccessLevel.PRIVATE)
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

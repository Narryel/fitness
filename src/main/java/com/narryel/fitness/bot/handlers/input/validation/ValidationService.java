package com.narryel.fitness.bot.handlers.input.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ValidationService {

    public ValidationResult checkIfPositiveBigDecimal(Update update, String message) {
        try {
            final var replaced = update.getMessage().getText().replace(",", ".");
            var number = new BigDecimal(replaced);
            if (number.compareTo(BigDecimal.ZERO) < 0) {
                return ValidationResult.getInvalidResult(message);
            }
        } catch (Exception e) {
            return ValidationResult.getInvalidResult(message);
        }

        return ValidationResult.VALID_INPUT_MESSAGE;
    }

    public ValidationResult checkIfPositiveInteger(Update update, String message) {
        try {
            final var text = update.getMessage().getText();
            int integer = Integer.parseInt(text);
            if (integer <= 0) {
                return ValidationResult.getInvalidResult(message);
            }
        } catch (Exception e) {
            return ValidationResult.getInvalidResult(message);
        }
        return ValidationResult.VALID_INPUT_MESSAGE;
    }
}

package ru.errorfreetext.service;

import org.junit.jupiter.api.Test;
import ru.errorfreetext.exception.InvalidTextException;
import static org.junit.jupiter.api.Assertions.*;

class TextValidationServiceTest {
    private final TextValidationService service = new TextValidationService();

    @Test void rejectsShortText() { assertThrows(InvalidTextException.class, () -> service.validate("ab")); }
    @Test void rejectsOnlyDigitsAndSymbols() { assertThrows(InvalidTextException.class, () -> service.validate("123!@#")); }
    @Test void acceptsNormalText() { assertDoesNotThrow(() -> service.validate("Привет!")); }
}

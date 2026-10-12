package ru.errorfreetext.service;

import org.springframework.stereotype.Service;
import ru.errorfreetext.exception.InvalidTextException;

@Service
public class TextValidationService {
    public void validate(String text) {
        if (text == null || text.length() < 3) throw new InvalidTextException("Text must contain at least 3 characters");
        if (text.codePoints().allMatch(cp -> !Character.isLetter(cp))) {
            throw new InvalidTextException("Text cannot contain only special characters and digits");
        }
    }
}

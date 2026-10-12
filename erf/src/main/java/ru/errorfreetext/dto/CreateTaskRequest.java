package ru.errorfreetext.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "text must not be blank")
        @Size(min = 3, message = "text must contain at least 3 characters")
        String text,
        @NotBlank(message = "language must not be blank")
        @Pattern(regexp = "(?i)RU|EN", message = "language must be RU or EN")
        String language
) {}

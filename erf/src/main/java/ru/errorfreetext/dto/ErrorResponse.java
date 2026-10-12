package ru.errorfreetext.dto;

import java.time.OffsetDateTime;
public record ErrorResponse(String errorMessage, int errorCode, OffsetDateTime timestamp, String path) {}

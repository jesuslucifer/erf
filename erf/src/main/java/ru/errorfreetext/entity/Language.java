package ru.errorfreetext.entity;

public enum Language {
    RU("ru"), EN("en");

    private final String apiValue;
    Language(String apiValue) { this.apiValue = apiValue; }
    public String apiValue() { return apiValue; }
}

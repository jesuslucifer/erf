package ru.errorfreetext.service;

import org.springframework.stereotype.Service;
import ru.errorfreetext.client.YandexSpellerClient;
import ru.errorfreetext.client.YandexSpellerClient.SpellError;
import ru.errorfreetext.entity.Language;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class TextCorrectionService {
    private static final int MAX_CHUNK_SIZE = 10_000;
    private static final int IGNORE_DIGITS = 2;
    private static final int IGNORE_URLS = 4;
    private static final Pattern URL = Pattern.compile("(?i)\\b(?:https?://|www\\.)\\S+");
    private final YandexSpellerClient client;

    public TextCorrectionService(YandexSpellerClient client) { this.client = client; }

    public String correct(String text, Language language) {
        List<String> chunks = split(text);
        int options = (containsDigit(text) ? IGNORE_DIGITS : 0) | (URL.matcher(text).find() ? IGNORE_URLS : 0);
        List<List<SpellError>> results = client.checkTexts(chunks, language, options);
        if (results == null || results.size() != chunks.size()) throw new IllegalStateException("Unexpected response from Yandex Speller");
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < chunks.size(); i++) result.append(applyCorrections(chunks.get(i), results.get(i)));
        return result.toString();
    }

    List<String> split(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + MAX_CHUNK_SIZE, text.length());
            if (end < text.length()) {
                int split = text.lastIndexOf(' ', end);
                if (split <= start) split = end;
                else split++;
                end = split;
            }
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }

    private String applyCorrections(String text, List<SpellError> errors) {
        if (errors == null || errors.isEmpty()) return text;
        List<SpellError> sorted = errors.stream().filter(e -> e.s() != null && !e.s().isEmpty())
                .sorted(Comparator.comparingInt(SpellError::pos).reversed()).toList();
        StringBuilder result = new StringBuilder(text);
        for (SpellError error : sorted) {
            int start = error.pos(), end = start + error.len();
            if (start >= 0 && end <= result.length()) result.replace(start, end, error.s().get(0));
        }
        return result.toString();
    }

    private boolean containsDigit(String text) { return text.codePoints().anyMatch(Character::isDigit); }
}

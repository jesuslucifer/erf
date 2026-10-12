package ru.errorfreetext.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.errorfreetext.client.YandexSpellerClient;
import ru.errorfreetext.entity.Language;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextCorrectionServiceTest {
    @Mock YandexSpellerClient client;

    @Test void setsBothOptionsWhenNeeded() {
        TextCorrectionService service = new TextCorrectionService(client);
        when(client.checkTexts(anyList(), eq(Language.RU), eq(6))).thenReturn(List.of(List.of()));
        assertEquals("Текст 123 https://example.com", service.correct("Текст 123 https://example.com", Language.RU));
        verify(client).checkTexts(anyList(), eq(Language.RU), eq(6));
    }

    @Test void appliesSuggestion() {
        TextCorrectionService service = new TextCorrectionService(client);
        when(client.checkTexts(anyList(), eq(Language.RU), eq(0)))
                .thenReturn(List.of(List.of(new YandexSpellerClient.SpellError(1, 0, 0, 0, 6, "карова", List.of("корова")))));
        assertEquals("корова бежит", service.correct("карова бежит", Language.RU));
    }

    @Test void splitsLongText() {
        TextCorrectionService service = new TextCorrectionService(client);
        String text = "а".repeat(9999) + " " + "б".repeat(20);
        when(client.checkTexts(anyList(), eq(Language.EN), eq(0))).thenReturn(List.of(List.of(), List.of()));
        assertEquals(text, service.correct(text, Language.EN));
        verify(client).checkTexts(argThat(list -> list.size() == 2), eq(Language.EN), eq(0));
    }
    @Test void emailDoesNotEnableIgnoreUrls() {
        TextCorrectionService service = new TextCorrectionService(client);
        when(client.checkTexts(anyList(), eq(Language.EN), eq(0))).thenReturn(List.of(List.of()));

        service.correct("Напишите test@example.com", Language.EN);

        verify(client).checkTexts(anyList(), eq(Language.EN), eq(0));
    }

    @Test void digitsOnlyOptionIsEnabledWithoutUrls() {
        TextCorrectionService service = new TextCorrectionService(client);
        when(client.checkTexts(anyList(), eq(Language.RU), eq(2))).thenReturn(List.of(List.of()));

        service.correct("Текст 123", Language.RU);

        verify(client).checkTexts(anyList(), eq(Language.RU), eq(2));
    }

}


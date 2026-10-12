package ru.errorfreetext.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import ru.errorfreetext.entity.Language;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Component
public class YandexSpellerClient {
    private final RestClient restClient;

    public YandexSpellerClient(
            @Value("${yandex.speller.base-url:https://speller.yandex.net/services/spellservice.json}") String baseUrl,
            @Value("${yandex.speller.connect-timeout-ms:2000}") long connectTimeoutMs,
            @Value("${yandex.speller.read-timeout-ms:5000}") long readTimeoutMs) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public List<List<SpellError>> checkTexts(List<String> texts, Language language, int options) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        texts.forEach(text -> form.add("text", text));
        form.add("lang", language.apiValue());
        form.add("options", String.valueOf(options));
        form.add("format", "plain");

        return restClient.post()
                .uri("/checkTexts")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public record SpellError(int code, int pos, int row, int col, int len, String word, List<String> s) {}
}

package ru.errorfreetext.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.errorfreetext.entity.CorrectionTask;
import ru.errorfreetext.entity.Language;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskProcessingServiceTest {
    @Mock
    TextCorrectionService correctionService;
    @Mock
    TaskResultService resultService;
    @InjectMocks
    TaskProcessingService processingService;

    @Test
    void completesTaskAfterSuccessfulCorrection() {
        CorrectionTask task = new CorrectionTask(UUID.randomUUID(), "карова", Language.RU);
        task.markInProgress();
        when(correctionService.correct("карова", Language.RU)).thenReturn("корова");

        processingService.process(task);

        verify(resultService).complete(task.getId(), "корова");
        verify(resultService, never()).fail(any(), anyString());
    }

    @Test
    void marksTaskFailedWhenYandexThrows() {
        CorrectionTask task = new CorrectionTask(UUID.randomUUID(), "карова", Language.RU);
        task.markInProgress();
        when(correctionService.correct("карова", Language.RU))
                .thenThrow(new IllegalStateException("Yandex unavailable"));

        processingService.process(task);

        verify(resultService).fail(task.getId(), "Yandex unavailable");
        verify(resultService, never()).complete(any(), anyString());
    }
}

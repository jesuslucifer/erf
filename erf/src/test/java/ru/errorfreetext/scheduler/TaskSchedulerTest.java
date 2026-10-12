package ru.errorfreetext.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.errorfreetext.entity.CorrectionTask;
import ru.errorfreetext.entity.Language;
import ru.errorfreetext.service.TaskClaimService;
import ru.errorfreetext.service.TaskProcessingService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskSchedulerTest {
    @Mock
    TaskClaimService claimService;
    @Mock
    TaskProcessingService processingService;
    @InjectMocks
    AppTaskScheduler scheduler;

    @Test
    void claimsAndProcessesAllClaimedTasks() {
        CorrectionTask first = new CorrectionTask(UUID.randomUUID(), "Первый", Language.RU);
        CorrectionTask second = new CorrectionTask(UUID.randomUUID(), "Второй", Language.RU);
        when(claimService.claimNewTasks()).thenReturn(List.of(first, second));

        scheduler.processTasks();

        verify(processingService).process(first);
        verify(processingService).process(second);
    }

    @Test
    void doesNothingWhenThereAreNoNewTasks() {
        when(claimService.claimNewTasks()).thenReturn(List.of());

        scheduler.processTasks();

        verifyNoInteractions(processingService);
    }
}

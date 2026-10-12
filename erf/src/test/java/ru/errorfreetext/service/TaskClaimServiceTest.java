package ru.errorfreetext.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.errorfreetext.entity.CorrectionTask;
import ru.errorfreetext.entity.Language;
import ru.errorfreetext.entity.TaskStatus;
import ru.errorfreetext.repository.CorrectionTaskRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskClaimServiceTest {
    @Mock
    CorrectionTaskRepository repository;
    @InjectMocks
    TaskClaimService service;

    @Test
    void claimsTasksAndMovesThemToInProgress() {
        CorrectionTask task = new CorrectionTask(UUID.randomUUID(), "Привет", Language.RU);
        when(repository.findAndLockNewTasks()).thenReturn(List.of(task));

        List<CorrectionTask> result = service.claimNewTasks();

        assertEquals(1, result.size());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        verify(repository).saveAll(List.of(task));
    }
}

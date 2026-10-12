package ru.errorfreetext.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.errorfreetext.entity.CorrectionTask;
import ru.errorfreetext.repository.CorrectionTaskRepository;

import java.util.UUID;

@Service
public class TaskResultService {
    private final CorrectionTaskRepository repository;

    public TaskResultService(CorrectionTaskRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void complete(UUID taskId, String correctedText) {
        CorrectionTask task = find(taskId);
        task.complete(correctedText);
        repository.save(task);
    }

    @Transactional
    public void fail(UUID taskId, String errorMessage) {
        CorrectionTask task = find(taskId);
        task.fail(errorMessage);
        repository.save(task);
    }

    private CorrectionTask find(UUID taskId) {
        return repository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task disappeared while being processed: " + taskId));
    }
}

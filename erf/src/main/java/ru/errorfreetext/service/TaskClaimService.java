package ru.errorfreetext.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.errorfreetext.entity.CorrectionTask;
import ru.errorfreetext.repository.CorrectionTaskRepository;

import java.util.List;

@Service
public class TaskClaimService {
    private final CorrectionTaskRepository repository;

    public TaskClaimService(CorrectionTaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Atomically claims up to 20 NEW tasks. The transaction ends before any external API call.
     */
    @Transactional
    public List<CorrectionTask> claimNewTasks() {
        List<CorrectionTask> tasks = repository.findAndLockNewTasks();
        tasks.forEach(CorrectionTask::markInProgress);
        repository.saveAll(tasks);
        return tasks;
    }
}

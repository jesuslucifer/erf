package ru.errorfreetext.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.errorfreetext.dto.CreateTaskRequest;
import ru.errorfreetext.entity.CorrectionTask;
import ru.errorfreetext.entity.Language;
import ru.errorfreetext.exception.TaskNotFoundException;
import ru.errorfreetext.repository.CorrectionTaskRepository;
import java.util.UUID;

@Service
public class TaskService {
    private final CorrectionTaskRepository repository;
    private final TextValidationService validationService;

    public TaskService(CorrectionTaskRepository repository, TextValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @Transactional
    public CorrectionTask create(CreateTaskRequest request) {
        validationService.validate(request.text());
        Language language = Language.valueOf(request.language().toUpperCase());
        return repository.save(new CorrectionTask(UUID.randomUUID(), request.text(), language));
    }

    @Transactional(readOnly = true)
    public CorrectionTask get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}

package ru.errorfreetext.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.errorfreetext.entity.CorrectionTask;

@Service
public class TaskProcessingService {
    private static final Logger log = LoggerFactory.getLogger(TaskProcessingService.class);

    private final TextCorrectionService correctionService;
    private final TaskResultService resultService;

    public TaskProcessingService(TextCorrectionService correctionService, TaskResultService resultService) {
        this.correctionService = correctionService;
        this.resultService = resultService;
    }

    public void process(CorrectionTask task) {
        try {
            String corrected = correctionService.correct(task.getSourceText(), task.getLanguage());
            resultService.complete(task.getId(), corrected);
            log.info("Task {} completed", task.getId());
        } catch (Exception e) {
            String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            try {
                resultService.fail(task.getId(), message);
            } catch (Exception persistenceException) {
                log.error("Could not mark task {} as FAILED", task.getId(), persistenceException);
                return;
            }
            log.error("Task {} failed: {}", task.getId(), message, e);
        }
    }
}

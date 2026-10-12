package ru.errorfreetext.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.errorfreetext.entity.CorrectionTask;
import ru.errorfreetext.service.TaskClaimService;
import ru.errorfreetext.service.TaskProcessingService;

import java.util.List;

@Component
public class AppTaskScheduler {
    private static final Logger log = LoggerFactory.getLogger(AppTaskScheduler.class);

    private final TaskClaimService claimService;
    private final TaskProcessingService processingService;

    public AppTaskScheduler(TaskClaimService claimService, TaskProcessingService processingService) {
        this.claimService = claimService;
        this.processingService = processingService;
    }

    @Scheduled(fixedDelayString = "${scheduler.fixed-delay-ms:5000}")
    public void processTasks() {
        List<CorrectionTask> tasks = claimService.claimNewTasks();
        if (tasks.isEmpty()) {
            return;
        }

        log.info("Claimed {} task(s) for processing", tasks.size());
        tasks.forEach(processingService::process);
    }
}

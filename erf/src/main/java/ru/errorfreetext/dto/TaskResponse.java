package ru.errorfreetext.dto;

import ru.errorfreetext.entity.CorrectionTask;
import ru.errorfreetext.entity.TaskStatus;

public record TaskResponse(TaskStatus status, String correctedText, String errorMessage) {
    public static TaskResponse from(CorrectionTask task) {
        return switch (task.getStatus()) {
            case NEW, IN_PROGRESS -> new TaskResponse(task.getStatus(), null, null);
            case COMPLETED -> new TaskResponse(task.getStatus(), task.getCorrectedText(), null);
            case FAILED -> new TaskResponse(task.getStatus(), null, task.getErrorMessage());
        };
    }
}

package ru.errorfreetext.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "correction_tasks", indexes = @Index(name = "idx_tasks_status", columnList = "status"))
public class CorrectionTask {
    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sourceText;

    @Column(nullable = false, length = 2)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(columnDefinition = "TEXT")
    private String correctedText;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    protected CorrectionTask() {}

    public CorrectionTask(UUID id, String sourceText, Language language) {
        this.id = id;
        this.sourceText = sourceText;
        this.language = language;
        this.status = TaskStatus.NEW;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() { updatedAt = OffsetDateTime.now(); }

    public UUID getId() { return id; }
    public String getSourceText() { return sourceText; }
    public Language getLanguage() { return language; }
    public TaskStatus getStatus() { return status; }
    public String getCorrectedText() { return correctedText; }
    public String getErrorMessage() { return errorMessage; }

    public void markInProgress() { status = TaskStatus.IN_PROGRESS; }
    public void complete(String correctedText) { this.correctedText = correctedText; this.errorMessage = null; status = TaskStatus.COMPLETED; }
    public void fail(String errorMessage) { this.errorMessage = errorMessage; status = TaskStatus.FAILED; }
}

package ru.errorfreetext.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.errorfreetext.entity.CorrectionTask;

import java.util.List;
import java.util.UUID;

public interface CorrectionTaskRepository extends JpaRepository<CorrectionTask, UUID> {

    /**
     * Locks a batch of NEW tasks and skips rows locked by another application instance.
     * The lock is held only for the surrounding claim transaction.
     */
    @Query(value = """
            SELECT *
            FROM correction_tasks
            WHERE status = 'NEW'
            ORDER BY created_at
            FOR UPDATE SKIP LOCKED
            LIMIT 20
            """, nativeQuery = true)
    List<CorrectionTask> findAndLockNewTasks();
}

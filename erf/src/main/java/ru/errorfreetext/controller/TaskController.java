package ru.errorfreetext.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.errorfreetext.dto.*;
import ru.errorfreetext.service.TaskService;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) { this.taskService = taskService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return new CreateTaskResponse(taskService.create(request).getId());
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable UUID id) { return TaskResponse.from(taskService.get(id)); }
}

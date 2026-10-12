package ru.errorfreetext.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.errorfreetext.entity.CorrectionTask;
import ru.errorfreetext.entity.Language;
import ru.errorfreetext.entity.TaskStatus;
import ru.errorfreetext.service.TaskService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaskService taskService;

    @Test
    void createsTask() throws Exception {
        UUID id = UUID.randomUUID();
        when(taskService.create(any())).thenReturn(new CorrectionTask(id, "Привет", Language.RU));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Привет\",\"language\":\"RU\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").value(id.toString()));
    }

    @Test
    void rejectsInvalidLanguage() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Привет\",\"language\":\"DE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(40001))
                .andExpect(jsonPath("$.path").value("/tasks"));
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{broken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(40001));
    }

    @Test
    void rejectsInvalidUuid() throws Exception {
        mockMvc.perform(get("/tasks/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(40001));
    }

    @Test
    void returnsOnlyStatusForInProgressTask() throws Exception {
        CorrectionTask task = new CorrectionTask(UUID.randomUUID(), "Привет", Language.RU);
        task.markInProgress();
        when(taskService.get(task.getId())).thenReturn(task);

        mockMvc.perform(get("/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.correctedText").doesNotExist())
                .andExpect(jsonPath("$.errorMessage").doesNotExist());
    }
}

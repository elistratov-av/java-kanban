package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import services.Managers;
import services.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {
    @Test
    void shouldBeEquals() {
        Epic epic1 = new Epic("Epic1");
        Epic epic2 = new Epic(epic1);
        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    void allSubtasksNew() {
        TaskManager tm = Managers.getDefault();
        Epic epic = tm.create(new Epic("Epic"));
        LocalDateTime today = LocalDateTime.now();
        tm.create(new Subtask("Subtask1", TaskStatus.NEW, today, Duration.ofMinutes(1), epic));
        tm.create(new Subtask("Subtask2", TaskStatus.NEW, today, Duration.ofMinutes(1), epic));

        epic = tm.findEpic(epic.getId()).orElseThrow();
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void allSubtasksDone() {
        TaskManager tm = Managers.getDefault();
        Epic epic = tm.create(new Epic("Epic"));
        LocalDateTime today = LocalDateTime.now();
        tm.create(new Subtask("Subtask1", TaskStatus.DONE, today, Duration.ofMinutes(1), epic));
        tm.create(new Subtask("Subtask2", TaskStatus.DONE, today, Duration.ofMinutes(1), epic));

        epic = tm.findEpic(epic.getId()).orElseThrow();
        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void allSubtasksNewOrDone() {
        TaskManager tm = Managers.getDefault();
        Epic epic = tm.create(new Epic("Epic"));
        LocalDateTime today = LocalDateTime.now();
        tm.create(new Subtask("Subtask1", TaskStatus.NEW, today, Duration.ofMinutes(1), epic));
        tm.create(new Subtask("Subtask2", TaskStatus.DONE, today, Duration.ofMinutes(1), epic));

        epic = tm.findEpic(epic.getId()).orElseThrow();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void someSubtasksInProgress() {
        TaskManager tm = Managers.getDefault();
        Epic epic = tm.create(new Epic("Epic"));
        LocalDateTime today = LocalDateTime.now();
        tm.create(new Subtask("Subtask1", TaskStatus.NEW, today, Duration.ofMinutes(1), epic));
        tm.create(new Subtask("Subtask2", TaskStatus.IN_PROGRESS, today, Duration.ofMinutes(1), epic));

        epic = tm.findEpic(epic.getId()).orElseThrow();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}
package services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void fetchTasks() {
        taskManager.create(new Task());
        taskManager.create(new Task());
        Collection<Task> tasks = taskManager.fetchTasks();

        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(2, tasks.size());
    }

    @Test
    void clearTasks() {
        taskManager.create(new Task());
        taskManager.create(new Task());
        Collection<Task> tasks = taskManager.fetchTasks();

        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(2, tasks.size());

        taskManager.clearTasks();
        tasks = taskManager.fetchTasks();

        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(0, tasks.size());
    }

    @Test
    void findTask() {
        Task task = taskManager.create(new Task());

        Assertions.assertNotNull(task);

        int id = task.getId();
        Task foundTask = taskManager.findTask(id).orElseThrow();

        Assertions.assertEquals(task, foundTask);
    }

    @Test
    void create() {
        Task task = taskManager.create(new Task());

        Assertions.assertNotNull(task);
    }

    @Test
    void shouldBeEquals() {
        Task task = new Task("Task1", LocalDateTime.now(), Duration.ofMinutes(1));
        task.setDescription("Description1");
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.create(task);
        Task foundTask = taskManager.findTask(task.getId()).orElseThrow();

        Assertions.assertEquals(task.getId(), foundTask.getId());
        Assertions.assertEquals(task.getName(), foundTask.getName());
        Assertions.assertEquals(task.getDescription(), foundTask.getDescription());
        Assertions.assertEquals(task.getStatus(), foundTask.getStatus());
    }

    @Test
    void testOverlap() {
        LocalDateTime today = LocalDateTime.now();
        taskManager.create(new Task("Task1", today, Duration.ofMinutes(2)));
        taskManager.create(new Task("Task2", today.plusMinutes(1), Duration.ofMinutes(2)));

        Assertions.assertEquals(1, taskManager.getPrioritizedTasks().size());
    }
}

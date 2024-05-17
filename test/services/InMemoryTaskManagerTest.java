package services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.util.Collection;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeAll() {
        taskManager = Managers.getDefault();
    }

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
        Task foundTask = taskManager.findTask(id);
        Assertions.assertNotNull(foundTask);
        Assertions.assertEquals(task, foundTask);
    }

    @Test
    void create() {
        Task task = taskManager.create(new Task());
        Assertions.assertNotNull(task);
    }

    @Test
    void shouldBeEquals() {
        Task task = new Task("Task1");
        task.setDescription("Description1");
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.create(task);
        Task foundTask = taskManager.findTask(task.getId());
        Assertions.assertEquals(task.getId(), foundTask.getId());
        Assertions.assertEquals(task.getName(), foundTask.getName());
        Assertions.assertEquals(task.getDescription(), foundTask.getDescription());
        Assertions.assertEquals(task.getStatus(), foundTask.getStatus());
    }
}
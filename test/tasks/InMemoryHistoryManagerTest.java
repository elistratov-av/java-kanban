package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Managers;
import services.TaskManager;

import java.util.List;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void testHistory() {
        Task task = taskManager.create(new Task("Task1"));
        Task foundTask = taskManager.findTask(task.getId());
        foundTask.setName("Task2");
        taskManager.update(foundTask);
        taskManager.findTask(task.getId());
        List<Task> history = taskManager.getHistory();

        Assertions.assertNotNull(history);
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals("Task2", history.getFirst().getName());
    }

    @Test
    void shouldRemoveOldTask() {
        Task task1 = taskManager.create(new Task("Task1"));
        Task task2 = taskManager.create(new Task("Task2"));
        taskManager.findTask(task1.getId());
        taskManager.findTask(task2.getId());
        List<Task> history = taskManager.getHistory();

        Assertions.assertNotNull(history);
        Assertions.assertEquals(2, history.size());
        taskManager.removeTask(task2.getId());
        history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals("Task1", history.getFirst().getName());
    }

    @Test
    void shouldBeEmpty() {
        Epic epic = taskManager.create(new Epic("Epic1"));
        Subtask subtask1 = taskManager.create(new Subtask("Subtask1", epic));
        Subtask subtask2 = taskManager.create(new Subtask("Subtask2", epic));
        taskManager.findEpic(epic.getId());
        taskManager.findSubtask(subtask1.getId());
        taskManager.findSubtask(subtask2.getId());
        List<Task> history = taskManager.getHistory();

        Assertions.assertNotNull(history);
        Assertions.assertEquals(3, history.size());
        taskManager.removeEpic(epic.getId());
        history = taskManager.getHistory();
        Assertions.assertTrue(history.isEmpty());
    }
}
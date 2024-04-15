package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals("Task1", history.get(0).getName());
        Assertions.assertEquals("Task2", history.get(1).getName());
    }
}
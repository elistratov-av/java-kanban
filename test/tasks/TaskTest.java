package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void shouldBeEquals() {
        Task task1 = new Task("Task1");
        Task task2 = new Task(task1);
        task2.setName("Task2");
        Assertions.assertEquals(task1, task2);
    }
}
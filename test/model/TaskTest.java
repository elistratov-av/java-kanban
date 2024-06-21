package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {
    @Test
    void shouldBeEquals() {
        Task task1 = new Task("Task1", LocalDateTime.now(), Duration.ofMinutes(1));
        Task task2 = new Task(task1);
        Assertions.assertEquals(task1, task2);
    }
}
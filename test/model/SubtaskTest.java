package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class SubtaskTest {
    @Test
    void shouldBeEquals() {
        Subtask subtask1 = new Subtask("Subtask1", LocalDateTime.now(), Duration.ofMinutes(1), new Epic("Epic1"));
        Subtask subtask2 = new Subtask(subtask1);
        Assertions.assertEquals(subtask1, subtask2);
    }
}
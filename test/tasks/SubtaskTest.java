package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    @Test
    void shouldBeEquals() {
        Subtask subtask1 = new Subtask("Subtask1", new Epic("Epic1"));
        Subtask subtask2 = new Subtask(subtask1);
        Assertions.assertEquals(subtask1, subtask2);
    }
}
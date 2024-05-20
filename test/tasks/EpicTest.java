package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {
    @Test
    void shouldBeEquals() {
        Epic epic1 = new Epic("Epic1");
        Epic epic2 = new Epic(epic1);
        Assertions.assertEquals(epic1, epic2);
    }
}
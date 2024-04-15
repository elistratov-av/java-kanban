package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import services.Managers;

class ManagersTest {

    @Test
    void shouldNotBeNull() {
        Assertions.assertNotNull(Managers.getDefault());
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}
package services;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeAll() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
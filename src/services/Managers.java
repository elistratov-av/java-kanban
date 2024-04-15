package services;

import tasks.InMemoryHistoryManager;
import tasks.InMemoryTaskManager;

public final class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

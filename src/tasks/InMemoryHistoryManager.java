package tasks;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final LinkedList<Task> list = new LinkedList<>();

    @Override
    public void add(Task task) {
        list.add(task);
        if (list.size() > MAX_HISTORY_SIZE)
            list.removeFirst();
    }

    @Override
    public List<Task> getHistory() {
        return list;
    }
}

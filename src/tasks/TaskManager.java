package tasks;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    Collection<Task> fetchTasks();

    void clearTasks();

    Task findTask(int id);

    Task create(Task task);

    void update(Task task);

    Task removeTask(int id);

    Collection<Subtask> fetchSubtasks();

    void clearSubtasks();

    Subtask findSubtask(int id);

    Subtask create(Subtask task);

    void update(Subtask task);

    Subtask removeSubtask(int id);

    Collection<Epic> fetchEpics();

    Collection<Subtask> fetchEpicSubtasks(int id);

    void clearEpics();

    Epic findEpic(int id);

    Epic create(Epic task);

    void update(Epic task);

    Epic removeEpic(int id);
}

package services;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TaskManager {
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    Collection<Task> fetchTasks();

    void clearTasks();

    Optional<Task> findTask(int id);

    Task create(Task task);

    void update(Task task);

    Task removeTask(int id);

    Collection<Subtask> fetchSubtasks();

    void clearSubtasks();

    Optional<Subtask> findSubtask(int id);

    Subtask create(Subtask task);

    void update(Subtask task);

    Subtask removeSubtask(int id);

    Collection<Epic> fetchEpics();

    Collection<Subtask> fetchEpicSubtasks(int id);

    void clearEpics();

    Optional<Epic> findEpic(int id);

    Epic create(Epic task);

    void update(Epic task);

    Epic removeEpic(int id);
}

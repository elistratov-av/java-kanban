package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    List<Task> fetchTasks();

    void clearTasks();

    Optional<Task> findTask(int id);

    Task create(Task task);

    void update(Task task);

    Task removeTask(int id);

    List<Subtask> fetchSubtasks();

    void clearSubtasks();

    Optional<Subtask> findSubtask(int id);

    Subtask create(Subtask task);

    void update(Subtask task);

    Subtask removeSubtask(int id);

    List<Epic> fetchEpics();

    List<Subtask> fetchEpicSubtasks(int id);

    void clearEpics();

    Optional<Epic> findEpic(int id);

    Epic create(Epic task);

    void update(Epic task);

    Epic removeEpic(int id);
}

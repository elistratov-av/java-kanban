package services;

import org.jetbrains.annotations.NotNull;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int taskCounter = 0;

    protected int nextId() {
        return ++taskCounter;
    }

    protected void resetIdIfGreater(int id) {
        if (id > taskCounter)
            taskCounter = id;
    }

    private final HistoryManager history;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();

    private void removeTasksFromHistory(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            history.remove(task.getId());
        }
    }

    public InMemoryTaskManager(HistoryManager history) {
        this.history = history;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    //region Tasks

    @Override
    public Collection<Task> fetchTasks() {
        return tasks.values();
    }

    @Override
    public void clearTasks() {
        removeTasksFromHistory(tasks.values());
        tasks.clear();
    }

    @Override
    public Task findTask(int id) {
        Task task = tasks.get(id);
        if (task != null)
            history.add(new Task(task));
        return task;
    }

    @Override
    public Task create(@NotNull Task task) {
        task.setId(nextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void update(@NotNull Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public Task removeTask(int id) {
        history.remove(id);
        return tasks.remove(id);
    }

    //endregion

    //region Helpers

    protected @NotNull TaskStatus calcEpicStatus(int id) {
        TaskStatus allStatus = null;
        for (Subtask st : subtasks.values()) {
            if (st == null || st.getEpicId() != id) continue;
            TaskStatus status = st.getStatus();
            if (allStatus == null) {
                if (status == TaskStatus.IN_PROGRESS) return TaskStatus.IN_PROGRESS;
                allStatus = status;
            } else if (allStatus != status)
                return TaskStatus.IN_PROGRESS;
        }

        return allStatus == null ? TaskStatus.NEW : allStatus;
    }

    //endregion

    //region Subtasks

    @Override
    public Collection<Subtask> fetchSubtasks() {
        // Реализация уже использует интерфейс Collection
        // чтобы не создавать обертку в виде new ArrayList оставляю этот интерфейс
        // на самом деле его функционал уже чем у List, но вполне достаточно для пользователей
        // поскольку требуется только перебор полученной коллекции
        return subtasks.values();
    }

    @Override
    public void clearSubtasks() {
        removeTasksFromHistory(subtasks.values());
        subtasks.clear();
        epics.forEach((k, e) -> e.setStatus(TaskStatus.NEW));
    }

    @Override
    public Subtask findSubtask(int id) {
        Subtask task = subtasks.get(id);
        if (task != null)
            history.add(new Subtask(task));
        return task;
    }

    @Override
    public Subtask create(@NotNull Subtask task) {
        Integer epicId = task.getEpicId();
        Epic epic = epicId != null ? findEpic(epicId) : null;
        if (epic == null) return null;
        task.setId(nextId());
        subtasks.put(task.getId(), task);
        epic.setStatus(calcEpicStatus(epic.getId()));
        return task;
    }

    @Override
    public void update(@NotNull Subtask task) {
        Integer epicId = task.getEpicId();
        Epic epic = epicId != null ? findEpic(epicId) : null;
        if (epic == null) throw new IllegalStateException("Refers to on-existent epic: " + epicId);
        subtasks.put(task.getId(), task);
        epic.setStatus(calcEpicStatus(epic.getId()));
    }

    @Override
    public Subtask removeSubtask(int id) {
        history.remove(id);
        Subtask task = subtasks.remove(id);
        if (task != null) {
            Integer epicId = task.getEpicId();
            Epic epic = epicId != null ? findEpic(epicId) : null;
            if (epic != null) {
                epic.setStatus(calcEpicStatus(epic.getId()));
            }
        }
        return task;
    }

    //endregion

    //region Epics

    @Override
    public Collection<Epic> fetchEpics() {
        return epics.values();
    }

    @Override
    public Collection<Subtask> fetchEpicSubtasks(int epicId) {
        List<Subtask> epicTasks = new ArrayList<>();
        for (Subtask task : subtasks.values()) {
            if (task.getEpicId() == epicId)
                epicTasks.add(task);
        }

        return epicTasks;
    }

    @Override
    public void clearEpics() {
        removeTasksFromHistory(epics.values());
        epics.clear();
        removeTasksFromHistory(subtasks.values());
        subtasks.clear();
    }

    @Override
    public Epic findEpic(int id) {
        Epic task = epics.get(id);
        if (task != null)
            history.add(new Epic(task));
        return task;
    }

    @Override
    public Epic create(@NotNull Epic task) {
        task.setId(nextId());
        task.setStatus(TaskStatus.NEW);
        epics.put(task.getId(), task);
        return task;
    }

    @Override
    public void update(@NotNull Epic task) {
        epics.put(task.getId(), task);
    }

    @Override
    public Epic removeEpic(int id) {
        history.remove(id);
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask task : fetchEpicSubtasks(id)) {
                history.remove(task.getId());
                subtasks.remove(task.getId());
            }
        }
        return epic;
    }

    //endregion
}

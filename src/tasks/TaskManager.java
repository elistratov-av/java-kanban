package tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private static int taskCounter = 0;

    private static int nextId() {
        return ++taskCounter;
    }

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    //region Tasks

    public Collection<Task> fetchTasks() {
        return tasks.values();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public Task findTask(int id) {
        return tasks.get(id);
    }

    public Task create(Task task) {
        task.id = nextId();
        tasks.put(task.id, task);
        return task;
    }

    public void update(Task task) {
        tasks.put(task.id, task);
    }

    public Task removeTask(int id) {
        return tasks.remove(id);
    }

    //endregion

    //region Helpers

    private TaskStatus calcEpicStatus(int id) {
        TaskStatus allStatus = null;
        for (Subtask st : subtasks.values()) {
            if (st == null || st.epicId != id) continue;
            TaskStatus status = st.getStatus();
            if (allStatus == null) {
                if (status == TaskStatus.IN_PROGRESS) return TaskStatus.IN_PROGRESS;
                allStatus = status;
            } else if (allStatus != status)
                return TaskStatus.IN_PROGRESS;
        }

        return allStatus == null ? TaskStatus.NEW : allStatus;
    }

    private Collection<Subtask> fetchEpicSubtasks(int epicId)
    {
        ArrayList<Subtask> tasks = new ArrayList<>();
        for (Subtask t : subtasks.values())
        {
            if (t.epicId == epicId)
                tasks.add(t);
        }

        return tasks;
    }

    //endregion

    //region Subtasks

    public Collection<Subtask> fetchSubtasks() {
        return subtasks.values();
    }

    public void clearSubtasks() {
        subtasks.clear();
        epics.forEach((k, e) -> e.status = TaskStatus.NEW);
    }

    public Subtask findSubtask(int id) {
        return subtasks.get(id);
    }

    public Subtask create(Subtask task) {
        Epic epic = findEpic(task.epicId);
        if (epic == null) return null;
        task.id = nextId();
        subtasks.put(task.id, task);
        epic.status = calcEpicStatus(epic.id);
        return task;
    }

    public void update(Subtask task) {
        Epic epic = findEpic(task.epicId);
        if (epic == null) return;
        subtasks.put(task.id, task);
        epic.status = calcEpicStatus(epic.id);
    }

    public Subtask removeSubtask(int id) {
        Subtask t = subtasks.remove(id);
        if (t != null) {
            Epic epic = findEpic(t.epicId);
            if (epic != null)
                epic.status = calcEpicStatus(epic.id);
        }
        return t;
    }

    //endregion

    //region Epics

    public Collection<Epic> fetchEpics() {
        return epics.values();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic findEpic(int id) {
        return epics.get(id);
    }

    public Epic create(Epic task) {
        task.id = nextId();
        task.status = TaskStatus.NEW;
        epics.put(task.id, task);
        return task;
    }

    public void update(Epic task) {
        epics.put(task.id, task);
    }

    public Epic removeEpic(int id) {
        Epic ep = epics.remove(id);
        if (ep != null) {
            for (Subtask t : fetchEpicSubtasks(id)) {
                subtasks.remove(t.id);
            }
        }
        return ep;
    }

    //endregion
}

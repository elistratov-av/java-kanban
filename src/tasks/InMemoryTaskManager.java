package tasks;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int taskCounter = 0;

    private static int nextId() {
        return ++taskCounter;
    }

    private final HistoryManager history = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

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
        tasks.clear();
    }

    @Override
    public Task findTask(int id) {
        Task t = tasks.get(id);
        if (t != null)
            history.add(new Task(t));
        return t;
    }

    @Override
    public Task create(Task task) {
        task.setId(nextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void update(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public Task removeTask(int id) {
        return tasks.remove(id);
    }

    //endregion

    //region Helpers

    private TaskStatus calcEpicStatus(int id) {
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
        return subtasks.values();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        epics.forEach((k, e) -> e.status = TaskStatus.NEW);
    }

    @Override
    public Subtask findSubtask(int id) {
        Subtask t = subtasks.get(id);
        if (t != null)
            history.add(new Subtask(t));
        return t;
    }

    @Override
    public Subtask create(Subtask task) {
        Epic epic = task.getEpic();
        if (epic == null) return null;
        task.setId(nextId());
        subtasks.put(task.getId(), task);
        epic.status = calcEpicStatus(epic.getId());
        return task;
    }

    @Override
    public void update(Subtask task) {
        Epic epic = task.getEpic();
        if (epic == null) return;
        subtasks.put(task.getId(), task);
        epic.status = calcEpicStatus(epic.getId());
    }

    @Override
    public Subtask removeSubtask(int id) {
        Subtask t = subtasks.remove(id);
        if (t != null) {
            Epic epic = t.getEpic();
            if (epic != null) {
                epic.status = calcEpicStatus(epic.getId());
            }
        }
        return t;
    }

    //endregion

    //region Epics

    @Override
    public Collection<Epic> fetchEpics() {
        return epics.values();
    }

    @Override
    public Collection<Subtask> fetchEpicSubtasks(int epicId) {
        ArrayList<Subtask> tasks = new ArrayList<>();
        for (Subtask t : subtasks.values()) {
            if (t.getEpicId() == epicId)
                tasks.add(t);
        }

        return tasks;
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic findEpic(int id) {
        Epic t = epics.get(id);
        if (t != null)
            history.add(new Epic(t));
        return t;
    }

    @Override
    public Epic create(Epic task) {
        task.setId(nextId());
        task.status = TaskStatus.NEW;
        epics.put(task.getId(), task);
        return task;
    }

    @Override
    public void update(Epic task) {
        epics.put(task.getId(), task);
    }

    @Override
    public Epic removeEpic(int id) {
        Epic ep = epics.remove(id);
        if (ep != null) {
            for (Subtask t : fetchEpicSubtasks(id)) {
                subtasks.remove(t.getId());
            }
        }
        return ep;
    }

    //endregion
}

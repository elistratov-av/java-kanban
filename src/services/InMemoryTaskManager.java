package services;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

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
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    //region Helpers

    private void removeTasksFromHistory(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            history.remove(task.getId());
        }
    }

    private boolean isOverlap(Task task1, Task task2) {
        if (task1.getStartTime().isBefore(task2.getStartTime()))
            return task2.getStartTime().isBefore(task1.getEndTime());
        return task1.getStartTime().isBefore(task2.getEndTime());
    }

    private boolean isValidPriorityTask(Task task) {
        if (task != null && task.getStartTime() != null && task.getDuration() != null)
            return prioritizedTasks.stream().noneMatch(t -> isOverlap(t, task));
        return false;
    }

    private boolean addPriorityTask(Task task) {
        if (isValidPriorityTask(task))
            return prioritizedTasks.add(task);
        return false;
    }

    private void removePriorityTask(Task task) {
        if (task != null && task.getStartTime() != null)
            prioritizedTasks.remove(task);
    }

    //endregion

    public InMemoryTaskManager(HistoryManager history) {
        this.history = history;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    //region Tasks

    @Override
    public Collection<Task> fetchTasks() {
        return tasks.values();
    }

    @Override
    public void clearTasks() {
        Collection<Task> tasks = this.tasks.values();
        removeTasksFromHistory(tasks);
        prioritizedTasks.removeAll(tasks);
        this.tasks.clear();
    }

    @Override
    public Optional<Task> findTask(int id) {
        Task task = tasks.get(id);
        if (task != null)
            history.add(task.clone());
        return Optional.ofNullable(task);
    }

    @Override
    public Task create(Task task) {
        task.setId(nextId());
        tasks.put(task.getId(), task);
        addPriorityTask(task);
        return task;
    }

    @Override
    public void update(Task task) {
        removePriorityTask(tasks.put(task.getId(), task));
        addPriorityTask(task);
    }

    @Override
    public Task removeTask(int id) {
        history.remove(id);
        removePriorityTask(tasks.get(id));
        return tasks.remove(id);
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
        Collection<Subtask> subtasks = this.subtasks.values();
        removeTasksFromHistory(subtasks);
        prioritizedTasks.removeAll(subtasks);
        this.subtasks.clear();
        epics.forEach((k, e) -> e.updateState(Collections.emptyList()));
    }

    @Override
    public Optional<Subtask> findSubtask(int id) {
        Subtask task = subtasks.get(id);
        if (task != null)
            history.add(task.clone());
        return Optional.ofNullable(task);
    }

    @Override
    public Subtask create(Subtask task) {
        Integer epicId = task.getEpicId();
        Optional<Epic> epic = Optional.empty();
        if (epicId != null)
            epic = findEpic(epicId);
        if (epic.isEmpty()) return null;

        Epic e = epic.get();
        task.setId(nextId());
        subtasks.put(task.getId(), task);
        addPriorityTask(task);
        e.updateState(fetchEpicSubtasks(e.getId()));
        return task;
    }

    @Override
    public void update(Subtask task) {
        Integer epicId = task.getEpicId();
        Optional<Epic> epic = Optional.empty();
        if (epicId != null)
            epic = findEpic(epicId);
        Epic e = epic.orElseThrow(() -> new IllegalStateException("Эпик не существует: " + epicId));

        removePriorityTask(subtasks.put(task.getId(), task));
        addPriorityTask(task);
        e.updateState(fetchEpicSubtasks(e.getId()));
    }

    @Override
    public Subtask removeSubtask(int id) {
        history.remove(id);
        removePriorityTask(subtasks.get(id));
        Subtask task = subtasks.remove(id);
        if (task != null) {
            Integer epicId = task.getEpicId();
            Optional<Epic> epic = Optional.empty();
            if (epicId != null)
                epic = findEpic(epicId);
            epic.ifPresent(e -> e.updateState(fetchEpicSubtasks(e.getId())));
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
        return subtasks.values().stream()
                .filter(t -> t.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    @Override
    public void clearEpics() {
        removeTasksFromHistory(epics.values());
        epics.clear();
        clearSubtasks();
    }

    @Override
    public Optional<Epic> findEpic(int id) {
        Epic task = epics.get(id);
        if (task != null)
            history.add(task.clone());
        return Optional.ofNullable(task);
    }

    @Override
    public Epic create(Epic task) {
        task.setId(nextId());
        task.setStatus(TaskStatus.NEW);
        epics.put(task.getId(), task);
        return task;
    }

    @Override
    public void update(Epic task) {
        epics.put(task.getId(), task);
    }

    @Override
    public Epic removeEpic(int id) {
        history.remove(id);
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask task : fetchEpicSubtasks(id)) {
                history.remove(task.getId());
                removePriorityTask(task);
                subtasks.remove(task.getId());
            }
        }
        return epic;
    }

    //endregion
}

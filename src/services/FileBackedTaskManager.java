package services;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utils.ManagerSaveException;
import utils.TaskCsvConverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager history, File file) {
        super(history);
        if (file == null)
            throw new IllegalArgumentException("Required parameter file is not specified");
        this.file = file;
    }

    //region Save & Load

    private void save() {
        try (BufferedWriter wr = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            wr.write(TaskCsvConverter.CSV_HEADER);
            wr.newLine();

            for (Task task : fetchTasks()) {
                writeTask(wr, task);
            }

            for (Epic epic : fetchEpics()) {
                writeTask(wr, epic);
            }

            for (Subtask subtask : fetchSubtasks()) {
                writeTask(wr, subtask);
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private static void writeTask(BufferedWriter wr, Task task) throws IOException {
        String csv = TaskCsvConverter.toString(task);
        if (csv != null) {
            wr.write(csv);
            wr.newLine();
        }
    }

    public static TaskManager loadFromFile(File file) {
        FileBackedTaskManager mgr = new FileBackedTaskManager(Managers.getDefaultHistory(), file);

        try (BufferedReader rd = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.isBlank()) continue;

                Task task = TaskCsvConverter.fromString(line);
                if (task == null) continue;

                mgr.loadTask(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }

        return mgr;
    }

    private void loadTask(Task task) {
        if (task == null) return;

        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                break;
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                subtasks.put(task.getId(), (Subtask) task);
                break;
            default: throw new IllegalStateException("Unexpected value: " + task.getType());
        }
        resetIdIfGreater(task.getId());
    }

    //endregion

    //region Tasks

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public Task create(Task task) {
        task = super.create(task);
        save();
        return task;
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public Task removeTask(int id) {
        Task task = super.removeTask(id);
        save();
        return task;
    }

    //endregion

    //region Epics

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public Epic create(Epic epic) {
        epic = super.create(epic);
        save();
        return epic;
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        save();
    }

    @Override
    public Epic removeEpic(int id) {
        Epic epic = super.removeEpic(id);
        save();
        return epic;
    }

    //endregion

    //region Subtasks

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public Subtask create(Subtask subtask) {
        subtask = super.create(subtask);
        save();
        return subtask;
    }

    @Override
    public void update(Subtask subtask) {
        super.update(subtask);
        save();
    }

    @Override
    public Subtask removeSubtask(int id) {
        Subtask subtask = super.removeSubtask(id);
        save();
        return subtask;
    }

    //endregion
}

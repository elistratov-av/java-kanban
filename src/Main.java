import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;
import util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws IOException {
        File trackerFile = Files.createTempFile("tracker", ".csv").toFile();
        System.out.println("trackerFile: " + trackerFile.getPath());
        TaskManager taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), trackerFile);

        LocalDateTime today = LocalDateTime.now();
        Task task1 = taskManager.create(new Task("Task1", today, Duration.ofMinutes(1)));
        Task task2 = taskManager.create(new Task("Task2", today, Duration.ofMinutes(1)));
        task2.setStatus(TaskStatus.IN_PROGRESS);
        Epic epic1 = taskManager.create(new Epic("Epic1"));
        Subtask subtask1 = taskManager.create(new Subtask("Subtask1", today, Duration.ofMinutes(1), epic1));
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        Subtask subtask2 = taskManager.create(new Subtask("Subtask2", today, Duration.ofMinutes(1), epic1));
        Subtask subtask3 = taskManager.create(new Subtask("Subtask3", today, Duration.ofMinutes(1), epic1));
        subtask3.setStatus(TaskStatus.DONE);
        Epic epic2 = taskManager.create(new Epic("Epic2"));

        System.out.println("Список задач первого трекера задач:");
        List<Task> allTasks1 = getAllTasks(taskManager);
        printTasks(allTasks1);
        System.out.println();
        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(trackerFile);
        System.out.println("Список задач второго трекера задач:");
        List<Task> allTasks2 = getAllTasks(taskManager2);
        printTasks(allTasks2);
        System.out.println();

        System.out.println("Списки задач обоих трекеров совпадают: " + listEquals(allTasks1, allTasks2));
    }

    private static List<Task> getAllTasks(TaskManager taskManager) {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(taskManager.fetchTasks());
        tasks.addAll(taskManager.fetchEpics());
        tasks.addAll(taskManager.fetchSubtasks());

        return tasks;
    }

    private static boolean listEquals(List<Task> tasks1, List<Task> tasks2) {
        if (tasks1 == tasks2)
            return true;
        if (tasks1 == null || tasks2 == null)
            return false;

        int count = tasks1.size();
        if (tasks2.size() != count)
            return false;

        for (int i = 0; i < count; i++) {
            if (!taskEquals(tasks1.get(i), tasks2.get(i)))
                return false;
        }

        return true;
    }

    private static boolean taskEquals(Task task1, Task task2) {
        if (task1 == task2)
            return true;
        else if (task1 == null || task2 == null)
            return false;

        if (task1.getClass() != task2.getClass()) return false;

        return Task.idEquals(task1.getId(), task2.getId()) && StringUtils.equals(task1.getName(), task2.getName(), true) &&
                StringUtils.equals(task1.getDescription(), task2.getDescription(), true) && task1.getStatus() == task2.getStatus() &&
                Objects.equals(task1.getStartTime(), task2.getStartTime()) && Objects.equals(task1.getDuration(), task2.getDuration()) &&
                Objects.equals(task1.getEpicId(), task2.getEpicId());
    }

    private static void printTasks(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История:");
        printTasks(manager.getHistory());
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        printTasks(manager.fetchTasks());
        System.out.println("Эпики:");
        for (Task epic : manager.fetchEpics()) {
            System.out.println(epic);

            for (Task task : manager.fetchEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        printTasks(manager.fetchSubtasks());

        printHistory(manager);
    }
}

import services.FileBackedTaskManager;
import services.Managers;
import services.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import utils.TaskComparator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        File trackerFile = Files.createTempFile("tracker", ".csv").toFile();
        System.out.println("trackerFile: " + trackerFile.getPath());
        TaskManager taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), trackerFile);

        Task task1 = taskManager.create(new Task("Task1"));
        Task task2 = taskManager.create(new Task("Task2"));
        task2.setStatus(TaskStatus.IN_PROGRESS);
        Epic epic1 = taskManager.create(new Epic("Epic1"));
        Subtask subtask1 = taskManager.create(new Subtask("Subtask1", epic1));
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        Subtask subtask2 = taskManager.create(new Subtask("Subtask2", epic1));
        Subtask subtask3 = taskManager.create(new Subtask("Subtask3", epic1));
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

        Task[] tasks1 = allTasks1.toArray(new Task[0]);
        Task[] tasks2 = allTasks2.toArray(new Task[0]);
        System.out.println("Списки задач обоих трекеров совпадают: " + Arrays.equals(tasks1, tasks2, new TaskComparator()));
    }

    private static List<Task> getAllTasks(TaskManager taskManager) {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(taskManager.fetchTasks());
        tasks.addAll(taskManager.fetchEpics());
        tasks.addAll(taskManager.fetchSubtasks());

        return tasks;
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

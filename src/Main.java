import services.Managers;
import services.TaskManager;
import tasks.*;

import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        int task1Id = taskManager.create(new Task("Task1")).getId();
        int task2Id = taskManager.create(new Task("Task2")).getId();
        Epic epic = taskManager.create(new Epic("Epic1"));
        int epic1Id = epic.getId();
        int subtask1Id = taskManager.create(new Subtask("Subtask1", epic)).getId();
        int subtask2Id = taskManager.create(new Subtask("Subtask2", epic)).getId();
        int subtask3Id = taskManager.create(new Subtask("Subtask3", epic)).getId();
        epic = taskManager.create(new Epic("Epic2"));
        int epic2Id = epic.getId();

        printAllTasks(taskManager);
        System.out.println();
        System.out.println("Выполняем пользовательский сценарий");

        checkTask(taskManager, task1Id);
        checkTask(taskManager, task2Id);
        checkEpic(taskManager, epic1Id);
        checkSubtask(taskManager, subtask1Id);
        checkTask(taskManager, task1Id);
        checkSubtask(taskManager, subtask2Id);
        checkEpic(taskManager, epic2Id);
        checkSubtask(taskManager, subtask3Id);
        checkEpic(taskManager, epic1Id);
        checkTask(taskManager, task1Id);

        taskManager.removeTask(task1Id);
        System.out.println("Удалили задачу по id: " + task1Id);
        printHistory(taskManager);

        taskManager.removeEpic(epic1Id);
        System.out.println("Удалили эпик по id: " + epic1Id);
        printHistory(taskManager);

        System.out.println("Завершили пользовательский сценарий");
        System.out.println();
        printAllTasks(taskManager);
    }

    private static void checkTask(TaskManager taskManager, int id) {
        taskManager.findTask(id);
        System.out.println("Обращение к задаче по id: " + id);
        printHistory(taskManager);
    }

    private static void checkSubtask(TaskManager taskManager, int id) {
        taskManager.findSubtask(id);
        System.out.println("Обращение к подзадаче по id: " + id);
        printHistory(taskManager);
    }

    private static void checkEpic(TaskManager taskManager, int id) {
        taskManager.findEpic(id);
        System.out.println("Обращение к эпику по id: " + id);
        printHistory(taskManager);
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

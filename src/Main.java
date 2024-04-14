import tasks.*;

import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        int task1Id = taskManager.create(new Task("Task1", "tasks.Task 1 description")).getId();
        int task2Id = taskManager.create(new Task("Task2", "tasks.Task 2 description")).getId();
        Epic epic = taskManager.create(new Epic("Epic1", "tasks.Epic 1 description"));
        int epic1Id = epic.getId();
        int subtask1Id = taskManager.create(new Subtask("Subtask1", "tasks.Subtask 1 description", epic)).getId();
        int subtask2Id = taskManager.create(new Subtask("Subtask2", "tasks.Subtask 2 description", epic)).getId();
        epic = taskManager.create(new Epic("Epic2", "tasks.Epic 2 description"));
        int epic2Id = epic.getId();
        int subtask3Id = taskManager.create(new Subtask("Subtask3", "tasks.Subtask 3 description", epic)).getId();

        System.out.println("Старт тестов!!!");
        printAllTasks(taskManager);
        System.out.println();

        Task task1 = taskManager.findTask(task1Id);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setName("Task1 +");
        taskManager.update(task1);
        Task task2 = taskManager.findTask(task2Id);
        task2.setStatus(TaskStatus.DONE);
        task2.setName("Task2 +");
        taskManager.update(task2);

        Epic epic1 = taskManager.findEpic(epic1Id);
        epic1.setStatus(TaskStatus.DONE);
        epic1.setName("Epic1 +");
        taskManager.update(epic1);
        Subtask subtask1 = taskManager.findSubtask(subtask1Id);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setName("Subtask1 +");
        taskManager.update(subtask1);
        Subtask subtask2 = taskManager.findSubtask(subtask2Id);
        subtask2.setStatus(TaskStatus.DONE);
        subtask2.setName("Subtask2 +");
        taskManager.update(subtask2);

        Epic epic2 = taskManager.findEpic(epic2Id);
        epic2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.update(epic2);
        Subtask subtask3 = taskManager.findSubtask(subtask3Id);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.update(subtask3);

        System.out.println("Поменяли статусы");
        printAllTasks(taskManager);
        System.out.println();

        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(epic2.getId());

        System.out.println("Удалили задачи");
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("Тесты завершены");
    }

    private static void printTasks(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
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

        System.out.println("История:");
        printTasks(manager.getHistory());
    }
}

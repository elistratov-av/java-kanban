import tasks.*;

import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = taskManager.create(new Task("Task1", "tasks.Task 1 description"));
        Task task2 = taskManager.create(new Task("Task2", "tasks.Task 2 description"));
        Epic epic1 = taskManager.create(new Epic("Epic1", "tasks.Epic 1 description"));
        Subtask subtask1 = taskManager.create(new Subtask("Subtask1", "tasks.Subtask 1 description", epic1.getId()));
        Subtask subtask2 = taskManager.create(new Subtask("Subtask2", "tasks.Subtask 2 description", epic1.getId()));
        Epic epic2 = taskManager.create(new Epic("Epic2", "tasks.Epic 2 description"));
        Subtask subtask3 = taskManager.create(new Subtask("Subtask3", "tasks.Subtask 3 description", epic2.getId()));

        System.out.println("Старт тестов!!!");
        System.out.println("Списки задач:");
        printTasks(taskManager.fetchTasks());
        System.out.println("Списки эпиков:");
        printTasks(taskManager.fetchEpics());
        System.out.println("Списки подзадач:");
        printTasks(taskManager.fetchSubtasks());
        System.out.println();

        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.update(task1);
        task2.setStatus(TaskStatus.DONE);
        taskManager.update(task2);

        epic1.setStatus(TaskStatus.DONE);
        taskManager.update(epic1);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.update(subtask1);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.update(subtask2);

        epic2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.update(epic2);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.update(subtask3);

        System.out.println("Поменяли статусы");
        System.out.println("Списки задач:");
        printTasks(taskManager.fetchTasks());
        System.out.println("Списки эпиков:");
        printTasks(taskManager.fetchEpics());
        System.out.println("Списки подзадач:");
        printTasks(taskManager.fetchSubtasks());
        System.out.println();

        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(epic2.getId());

        System.out.println("Удалили задачи");
        System.out.println("Списки задач:");
        printTasks(taskManager.fetchTasks());
        System.out.println("Списки эпиков:");
        printTasks(taskManager.fetchEpics());
        System.out.println("Списки подзадач:");
        printTasks(taskManager.fetchSubtasks());
        System.out.println("Тесты завершены");
    }

    private static void printTasks(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}

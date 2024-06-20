package service;

import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskCsvConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File trackerFile;

    @BeforeEach
    public void beforeEach() throws IOException {
        trackerFile = Files.createTempFile("test", ".csv").toFile();
        taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), trackerFile);
    }

    @Test
    void loadFromEmptyFile() {
        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(trackerFile);

        Assertions.assertTrue(taskManager1.fetchTasks().isEmpty(), "Кол-во задач ожидалось равным 1");
        Assertions.assertTrue(taskManager1.fetchEpics().isEmpty(), "Кол-во эпиков ожидалось равным 1");
        Assertions.assertTrue(taskManager1.fetchSubtasks().isEmpty(), "Кол-во подзадач ожидалось равным 1");
    }

    @Test
    void loadSavedFromFile() {
        taskManager.create(new Task("Task1", LocalDateTime.now(), Duration.ofMinutes(1)));
        Epic epic = taskManager.create(new Epic("Epic1"));
        taskManager.create(new Subtask("Subtask1", LocalDateTime.now().plusHours(1), Duration.ofMinutes(1), epic));
        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(trackerFile);

        Assertions.assertEquals(1, taskManager1.fetchTasks().size(), "Кол-во задач ожидалось равным 1");
        Assertions.assertEquals(1, taskManager1.fetchEpics().size(), "Кол-во эпиков ожидалось равным 1");
        Assertions.assertEquals(1, taskManager1.fetchSubtasks().size(), "Кол-во подзадач ожидалось равным 1");
    }

    @Test
    void loadFromFile() throws IOException {
        LocalDateTime today = LocalDateTime.now();
        try (BufferedWriter wr = Files.newBufferedWriter(trackerFile.toPath(), StandardCharsets.UTF_8)) {
            wr.write(TaskCsvConverter.CSV_HEADER);
            wr.newLine();
            wr.write("1,TASK,Task1,NEW,,," + today + ",1");
            wr.newLine();
            wr.write("2,EPIC,Epic1,NEW,,," + today + ",1");
            wr.newLine();
            wr.write("3,SUBTASK,Subtask1,NEW,,2," + today + ",1");
            wr.newLine();
        }
        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(trackerFile);

        Assertions.assertEquals(1, taskManager1.fetchTasks().size(), "Кол-во задач ожидалось равным 1");
        Assertions.assertEquals(1, taskManager1.fetchEpics().size(), "Кол-во эпиков ожидалось равным 1");
        Assertions.assertEquals(1, taskManager1.fetchSubtasks().size(), "Кол-во подзадач ожидалось равным 1");
    }

    @Test
    void testNotFoundFile() {
        Assertions.assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(new File("NotFoundFile.csv")));
    }
}
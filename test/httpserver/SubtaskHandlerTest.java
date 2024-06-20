package httpserver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtaskHandlerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        taskServer = new HttpTaskServer(manager, gson);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
        taskServer = null;
    }

    @Test
    public void testFetchSubtasks() throws IOException, InterruptedException {
        Epic epic = manager.create(new Epic("Epic"));
        Subtask subtask = manager.create(new Subtask("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Subtask> actualSubtasks = gson.fromJson(response.body(),
                    new TypeToken<List<Subtask>>() {
                    }.getType());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            assertNotNull(actualSubtasks, "Подзадачи не возвращаются");
            assertEquals(1, actualSubtasks.size(), "Некорректное количество подзадач");
        }
    }

    @Test
    public void testFindSubtask() throws IOException, InterruptedException {
        Epic epic = manager.create(new Epic("Epic"));
        Subtask expectedSubtask = manager.create(new Subtask("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + expectedSubtask.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Subtask actualSubtask = gson.fromJson(response.body(), Subtask.class);
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            assertEquals(expectedSubtask, actualSubtask, "Подзадачи не совпадают");
            assertEquals(expectedSubtask.getName(), actualSubtask.getName(), "Некорректное имя подзадачи");
        }
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = manager.create(new Epic("Epic"));
        Task subtask = new Subtask("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic);
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            // проверяем, что создалась одна задача с корректным именем
            List<Subtask> expectedSubtasks = manager.fetchSubtasks();
            assertNotNull(expectedSubtasks, "Подзадачи не возвращаются");
            assertEquals(1, expectedSubtasks.size(), "Некорректное количество подзадач");
            assertEquals(subtask.getName(), expectedSubtasks.getFirst().getName(), "Некорректное имя подзадачи");
        }
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = manager.create(new Epic("Epic"));
        Task expectedSubtask = manager.create(new Subtask("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic));
        expectedSubtask.setName("New name");
        String subtaskJson = gson.toJson(expectedSubtask);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task actualSubtask = gson.fromJson(response.body(), Task.class);
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            assertNotNull(actualSubtask, "Подзадача не возвращается");
            assertEquals(expectedSubtask.getName(), actualSubtask.getName(), "Некорректное имя подзадачи");
        }
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = manager.create(new Epic("Epic"));
        Subtask task = manager.create(new Subtask("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(204, response.statusCode());

            List<Task> expectedSubtasks = manager.fetchTasks();
            assertEquals(0, expectedSubtasks.size(), "Подзадача не удалилась");
        }
    }
}
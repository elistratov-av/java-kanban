package httpserver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

class TaskHandlerTest {
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
    public void testFetchTasks() throws IOException, InterruptedException {
        Task task = manager.create(new Task("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5)));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> actualTasks = gson.fromJson(response.body(),
                    new TypeToken<List<Task>>() {
                    }.getType());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            assertNotNull(actualTasks, "Задачи не возвращаются");
            assertEquals(1, actualTasks.size(), "Некорректное количество задач");
        }
    }

    @Test
    public void testFindTask() throws IOException, InterruptedException {
        Task expectedTask = manager.create(new Task("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5)));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + expectedTask.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task actualTask = gson.fromJson(response.body(), Task.class);
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            assertEquals(expectedTask, actualTask, "Задачи не совпадают");
            assertEquals(expectedTask.getName(), actualTask.getName(), "Некорректное имя задачи");
        }
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            // проверяем, что создалась одна задача с корректным именем
            List<Task> expectedTasks = manager.fetchTasks();
            assertNotNull(expectedTasks, "Задачи не возвращаются");
            assertEquals(1, expectedTasks.size(), "Некорректное количество задач");
            assertEquals(task.getName(), expectedTasks.getFirst().getName(), "Некорректное имя задачи");
        }
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task expectedTask = manager.create(new Task("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5)));
        expectedTask.setName("New name");
        String taskJson = gson.toJson(expectedTask);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task actualTask = gson.fromJson(response.body(), Task.class);
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            assertNotNull(actualTask, "Задача не возвращается");
            assertEquals(expectedTask.getName(), actualTask.getName(), "Некорректное имя задачи");
        }
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = manager.create(new Task("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5)));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(204, response.statusCode());

            List<Task> expectedTasks = manager.fetchTasks();
            assertEquals(0, expectedTasks.size(), "Задача не удалилась");
        }
    }
}
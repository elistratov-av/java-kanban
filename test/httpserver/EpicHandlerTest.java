package httpserver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
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

class EpicHandlerTest {
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
    public void testGetEpics() throws IOException, InterruptedException {
        manager.create(new Epic("Test"));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Epic> actualEpics = gson.fromJson(response.body(),
                    new TypeToken<List<Epic>>() {
                    }.getType());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            assertNotNull(actualEpics, "Эпики не возвращаются");
            assertEquals(1, actualEpics.size(), "Некорректное количество эпиков");
        }
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = manager.create(new Epic("Test"));
        manager.create(new Subtask("Test",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
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
    public void testFindEpic() throws IOException, InterruptedException {
        Epic expectedEpic = manager.create(new Epic("Test"));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + expectedEpic.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Epic actualEpic = gson.fromJson(response.body(), Epic.class);
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            assertEquals(expectedEpic, actualEpic, "Эпики не совпадают");
            assertEquals(expectedEpic.getName(), actualEpic.getName(), "Некорректное имя эпика");
        }
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test");
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            // проверяем, что создалась одна задача с корректным именем
            List<Epic> expectedEpics = manager.getEpics();
            assertNotNull(expectedEpics, "Эпики не возвращаются");
            assertEquals(1, expectedEpics.size(), "Некорректное количество эпиков");
            assertEquals(epic.getName(), expectedEpics.getFirst().getName(), "Некорректное имя эпика");
        }
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic expectedEpic = manager.create(new Epic("Test"));
        expectedEpic.setName("New name");
        String epicJson = gson.toJson(expectedEpic);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Epic actualEpic = gson.fromJson(response.body(), Epic.class);
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            assertNotNull(actualEpic, "Эпик не возвращается");
            assertEquals(expectedEpic.getName(), actualEpic.getName(), "Некорректное имя эпика");
        }
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = manager.create(new Epic("Test"));

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(204, response.statusCode());

            List<Epic> expectedEpics = manager.getEpics();
            assertEquals(0, expectedEpics.size(), "Эпик не удалился");
        }
    }
}
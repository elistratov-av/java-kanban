package httpserver;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.InputParsingException;
import exception.NotFoundException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static final Pattern TASKS_PATTERN = Pattern.compile("^/tasks$");
    private static final Pattern TASKS_ID_PATTERN = Pattern.compile("^/tasks/\\d+$");

    @Override
    public boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException {
        switch (requestMethod) {
            case "GET":
                if (TASKS_PATTERN.matcher(path).matches()) {
                    getTasks(httpExchange);
                    return true;
                } else if (TASKS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/tasks/".length());
                    findTask(httpExchange, parseInt(pathId));
                    return true;
                }
                break;

            case "POST":
                if (TASKS_PATTERN.matcher(path).matches()) {
                    Task task = parseTask(getRequestBodyText(httpExchange));
                    editTask(httpExchange, task);
                    return true;
                }
                break;

            case "DELETE":
                if (TASKS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/tasks/".length());
                    deleteTask(httpExchange, parseInt(pathId));
                    return true;
                }
                break;
        }

        return false;
    }

    private Task parseTask(String json) {
        try {
            return gson.fromJson(json, Task.class);
        } catch (JsonSyntaxException ex) {
            throw new InputParsingException("Ошибка преобразования к типу Task: " + json, ex);
        }
    }

    private void getTasks(HttpExchange httpExchange) throws IOException {
        Collection<Task> tasks = taskManager.getTasks();
        sendJson(httpExchange, 200, gson.toJson(tasks));
    }

    private void findTask(HttpExchange httpExchange, int id) throws IOException {
        Optional<Task> taskOpt = taskManager.findTask(id);
        Task task = taskOpt.orElseThrow(() -> new NotFoundException("Задача #" + id + " не найдена"));
        sendJson(httpExchange, 200, gson.toJson(task));
    }

    private void editTask(HttpExchange httpExchange, Task task) throws IOException {
        if (task.getId() == null) {
            task = taskManager.create(task);
        } else taskManager.update(task);
        sendJson(httpExchange, 201, gson.toJson(task));
    }

    private void deleteTask(HttpExchange httpExchange, int id) throws IOException {
        taskManager.removeTask(id);
        httpExchange.sendResponseHeaders(204, -1);
    }
}

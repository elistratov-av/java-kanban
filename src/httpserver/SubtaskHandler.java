package httpserver;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.InputParsingException;
import exception.NotFoundException;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static final Pattern SUBTASKS_PATTERN = Pattern.compile("^/subtasks$");
    private static final Pattern SUBTASKS_ID_PATTERN = Pattern.compile("^/subtasks/\\d+$");

    @Override
    public boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException {
        switch (requestMethod) {
            case "GET":
                if (SUBTASKS_PATTERN.matcher(path).matches()) {
                    fetchSubtasks(httpExchange);
                    return true;
                } else if (SUBTASKS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/subtasks/".length());
                    findSubtask(httpExchange, parseInt(pathId));
                    return true;
                }
                break;

            case "POST":
                if (SUBTASKS_PATTERN.matcher(path).matches()) {
                    Subtask subtask = parseSubtask(getRequestBodyText(httpExchange));
                    editSubtask(httpExchange, subtask);
                    return true;
                }
                break;

            case "DELETE":
                if (SUBTASKS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/subtasks/".length());
                    deleteSubtask(httpExchange, parseInt(pathId));
                    return true;
                }
                break;
        }

        return false;
    }

    private Subtask parseSubtask(String json) {
        try {
            return gson.fromJson(json, Subtask.class);
        } catch (JsonSyntaxException ex) {
            throw new InputParsingException("Ошибка преобразования к типу Subtask: " + json, ex);
        }
    }

    private void fetchSubtasks(HttpExchange httpExchange) throws IOException {
        Collection<Subtask> subtasks = taskManager.fetchSubtasks();
        sendJson(httpExchange, 200, gson.toJson(subtasks));
    }

    private void findSubtask(HttpExchange httpExchange, int id) throws IOException {
        Optional<Subtask> subtaskOpt = taskManager.findSubtask(id);
        Subtask subtask = subtaskOpt.orElseThrow(() -> new NotFoundException("Подзадача #" + id + " не найдена"));
        sendJson(httpExchange, 200, gson.toJson(subtask));
    }

    private void editSubtask(HttpExchange httpExchange, Subtask subtask) throws IOException {
        if (subtask.getId() == null) {
            subtask = taskManager.create(subtask);
        } else taskManager.update(subtask);
        sendJson(httpExchange, 201, gson.toJson(subtask));
    }

    private void deleteSubtask(HttpExchange httpExchange, int id) throws IOException {
        taskManager.removeSubtask(id);
        httpExchange.sendResponseHeaders(204, -1);
    }
}

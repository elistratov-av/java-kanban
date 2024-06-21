package httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static final Pattern PRIORITIZED_PATTERN = Pattern.compile("^/prioritized$");

    @Override
    public boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException {
        switch (requestMethod) {
            case "GET":
                if (PRIORITIZED_PATTERN.matcher(path).matches()) {
                    getPrioritizedTasks(httpExchange);
                    return true;
                }
                break;
        }

        return false;
    }

    private void getPrioritizedTasks(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = taskManager.getPrioritizedTasks();
        sendJson(httpExchange, 200, gson.toJson(tasks));
    }
}

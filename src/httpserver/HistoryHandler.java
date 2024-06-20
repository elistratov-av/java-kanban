package httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static final Pattern HISTORY_PATTERN = Pattern.compile("^/history$");

    @Override
    public boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException {
        switch (requestMethod) {
            case "GET":
                if (HISTORY_PATTERN.matcher(path).matches()) {
                    getHistory(httpExchange);
                    return true;
                }
                break;
        }

        return false;
    }

    private void getHistory(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = taskManager.getHistory();
        sendJson(httpExchange, 200, gson.toJson(tasks));
    }
}

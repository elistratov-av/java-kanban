package httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.InputParsingException;
import exception.NotFoundException;
import exception.OverlapException;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try (httpExchange) {
            try {
                String path = httpExchange.getRequestURI().getPath();
                String requestMethod = httpExchange.getRequestMethod();

                if (!processRequest(httpExchange, path, requestMethod)) {
                    handleNotAllowed(httpExchange, requestMethod + " " + path + ": недопустимое обращение");
                }
            } catch (NotFoundException ex) {
                handleNotFound(httpExchange, ex);
            } catch (OverlapException ex) {
                handleHasInteractions(httpExchange, ex);
            } catch (InputParsingException ex) {
                handleInputParsing(httpExchange, ex);
            } catch (Exception ex) {
                handleError(httpExchange, ex);
            }
        }
    }

    protected abstract boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException;

    protected static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new InputParsingException("Ошибка преобразования к типу int: " + value, ex);
        }
    }

    protected static String getRequestBodyText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected static void sendJson(HttpExchange h, int rCode, String json) throws IOException {
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(rCode, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    protected static void sendText(HttpExchange h, int rCode, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        h.sendResponseHeaders(rCode, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    protected static void printError(Throwable ex) {
        for (; ex != null; ex = ex.getCause()) {
            System.out.println(ex.getMessage());
            if (ex.getCause() == null)
                ex.printStackTrace();
        }
    }

    private static void handleNotFound(HttpExchange h, NotFoundException ex) {
        try {
            sendText(h, 404, ex.getMessage());
        } catch (Exception ignore) {
            printError(ignore);
        }
    }

    private static void handleHasInteractions(HttpExchange h, OverlapException ex) {
        try {
            sendText(h, 406, ex.getMessage());
        } catch (Exception ignore) {
            printError(ignore);
        }
    }

    private static void handleInputParsing(HttpExchange h, InputParsingException ex) {
        try {
            sendText(h, 400, ex.getMessage());
        } catch (Exception ignore) {
            printError(ignore);
        }
    }

    private static void handleNotAllowed(HttpExchange h, String message) {
        try {
            sendText(h, 405, message);
        } catch (Exception ignore) {
            printError(ignore);
        }
    }

    private static void handleError(HttpExchange h, Exception ex) {
        try {
            printError(ex);
            sendText(h, 500, ex.getMessage());
        } catch (Exception ignore) {
            printError(ignore);
        }
    }
}

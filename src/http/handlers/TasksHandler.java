package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskIntersectionException;
import exception.TaskNotFoundException;
import task.Task;
import taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String TASKS_PATH = "^/api/v1/tasks$";
    private static final String TASKS_ID_PATH = "^/api/v1/tasks/\\d+$";
    private final
    TaskManager taskManager;
    Gson gson;

    public TasksHandler(final TaskManager taskManager, final Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        String requestMethod = httpExchange.getRequestMethod();
        try (httpExchange) {
            switch (requestMethod) {
                case "GET" -> handleGetTask(httpExchange);
                case "POST" -> handlePostTask(httpExchange, path);
                case "DELETE" -> handleDeleteTask(httpExchange);
                default -> sendNotAllowed405(httpExchange,
                        "Ждем GET, POST или DELETE запрос, а получили - " + requestMethod,
                        405);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleGetTask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches(TASKS_PATH, path)) {
            String response = gson.toJson(taskManager.getAllTask());
            sendText200(httpExchange, response, 200);
        }
        if (Pattern.matches(TASKS_ID_PATH, path)) {
            String pathId = path.replaceFirst("/api/v1/tasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(taskManager.getTaskById(id));
                sendText200(httpExchange, response, 200);
            } else {
                System.out.println("Получен некорректный идентификатор задачи = " + id);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void handlePostTask(HttpExchange exchange, String path) throws IOException, TaskNotFoundException {
        if (Pattern.matches(TASKS_PATH, path)) {
            handleAddTask(exchange);
        } else if (Pattern.matches(TASKS_ID_PATH, path)) {
            handleUpdateTask(exchange, path);
        } else {
            sendBadRequest400(exchange, "Неверный путь запроса", 400);
        }
    }

    private void handleAddTask(HttpExchange exchange) throws IOException {
        final String requestBody = readText(exchange);
        final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
        if (!isValidJsonTask(jsonBody)) {
            sendBadRequest400(exchange, "Неправильный набор полей в теле запроса", 400);
            return;
        }
        Task task = gson.fromJson(requestBody, Task.class);
        try {
            String response = gson.toJson(taskManager.createTask(task));
            sendSuccessButNoNeedToReturn201(exchange, "Задача добавлена в TaskManager", 201);
        } catch (TaskIntersectionException e) {
            sendHasInteractions406(exchange, "Задача пересекается по времени с существующей задачей",
                    406);
        }
    }

    //обновление задачи
    private void handleUpdateTask(HttpExchange httpExchange, String path) throws IOException, TaskNotFoundException {
        final String pathId = path.replaceFirst("/api/v1/tasks/", "");
        final int id = parsePathId(pathId);
        if (id <= 0) {
            sendNotAllowed405(httpExchange, "Неправильный формат id", 405);
            return;
        }

        final String requestBody = readText(httpExchange);
        final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
        if (!isValidJsonTask(jsonBody)) {
            sendNotAllowed405(httpExchange, "Неправильный набор полей в теле запроса", 405);
            return;
        }
        Task task = gson.fromJson(requestBody, Task.class);
        if (task.getId() != id) {
            sendBadRequest400(httpExchange, "Id в path и теле запроса не равны", 400);
            return;
        }
        try {
            taskManager.updateTask(task);
            sendSuccessButNoNeedToReturn201(httpExchange, "Задача обновлена - 201, id = " + id,
                    201);
        } catch (TaskIntersectionException e) {
            sendHasInteractions406(httpExchange, "Задача пересекается по времени с существующей задачей",
                    406);
        }
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //все задачи
        if (Pattern.matches(TASKS_PATH, path)) {
            taskManager.deleteAllTasks();
            sendSuccessButNoNeedToReturn201(httpExchange, "Задачи удалены", 201);
        }
        //задачи по айди
        if (Pattern.matches(TASKS_ID_PATH, path)) {
            String requestMethod = httpExchange.getRequestMethod();
            String pathId = path.replaceFirst("/api/v1/tasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                taskManager.deleteTaskById(id);
                sendText200(httpExchange, "Удалена задача с айди = " + id, 200);
            } else {
                System.out.println("Получен некорректный идентификатор задачи = " + id);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private boolean isValidJsonTask(JsonObject jsonObject) {
        return jsonObject.has("name") &&
                jsonObject.has("description") &&
                jsonObject.has("id") &&
                jsonObject.has("status") &&
                jsonObject.has("type") &&
                jsonObject.has("start_time") &&
                jsonObject.has("duration");
    }
}
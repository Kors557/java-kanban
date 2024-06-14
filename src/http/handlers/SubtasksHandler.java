package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskIntersectionException;
import exception.TaskNotFoundException;
import task.SubTask;
import taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final String subtasksPath = "^/api/v1/subtasks$";
    private final String subtasksIdPath = "^/api/v1/subtasks/\\d+$";
    TaskManager taskManager;
    Gson gson;

    public SubtasksHandler(final TaskManager taskManager, final Gson gson) {
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
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET":
                    handleGetSubtask(httpExchange);
                    break;
                case "POST":
                    handlePostSubtask(httpExchange, path);
                    break;
                case "DELETE":
                    handleDeleteSubtask(httpExchange);
                    break;
                default:
                    sendNotAllowed405(httpExchange, "Ждем GET, POST или DELETE запрос, а получили - "
                            + requestMethod, 405);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    private void handleGetSubtask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //get
        if (Pattern.matches(subtasksPath, path)) {
            String response = gson.toJson(taskManager.getAllSubTasks());
            sendText200(httpExchange, response, 200);
        }
        //задача по айди
        if (Pattern.matches(subtasksIdPath, path)) {
            String pathId = path.replaceFirst("/api/v1/subtasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(taskManager.getSubTaskById(id));
                sendText200(httpExchange, response, 200);
            } else {
                System.out.println("Получен некорректный идентификатор задачи = " + id);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void handlePostSubtask(HttpExchange exchange, String path) throws IOException, TaskNotFoundException {
        if (Pattern.matches(subtasksPath, path)) {
            handleAddSubtask(exchange);
        } else if (Pattern.matches(subtasksIdPath, path)) {
            handleUpdateSubtask(exchange, path);
        } else {
            sendBadRequest400(exchange, "Неверный путь запроса", 400);
        }
    }

    private void handleAddSubtask(HttpExchange exchange) throws IOException {
        final String requestBody = readText(exchange);
        final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
        if (!isValidJsonSubtask(jsonBody)) {
            sendNotAllowed405(exchange, "Неправильный набор полей в теле запроса", 405);
            return;
        }
        SubTask subtask = gson.fromJson(requestBody, SubTask.class);
        try {
            String response = gson.toJson(taskManager.createSubTask(subtask));
            sendSuccessButNoNeedToReturn201(exchange, "Задача добавлена в TaskManager", 201);
        } catch (Exception e) {
            sendHasInteractions406(exchange, "Задача пересекается по времени с существующей задачей",
                    406);
        }
    }

    //обновление задачи
    private void handleUpdateSubtask(HttpExchange httpExchange, String path) throws IOException, TaskNotFoundException {
        final String pathId = path.replaceFirst("/api/v1/subtasks/", "");
        final int id = parsePathId(pathId);
        if (id <= 0) {
            sendNotAllowed405(httpExchange, "Неправильный формат id", 405);
            return;
        }

        final String requestBody = readText(httpExchange);
        final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
        if (!isValidJsonSubtask(jsonBody)) {
            sendNotAllowed405(httpExchange, "Неправильный набор полей в теле запроса", 405);
            return;
        }
        SubTask subtask = gson.fromJson(requestBody, SubTask.class);
        if (subtask.getId() != id) {
            sendBadRequest400(httpExchange, "Id в path и теле запроса не равны", 400);
            return;
        }
        try {
            taskManager.updateSubTask(subtask);
            sendSuccessButNoNeedToReturn201(httpExchange, "Подзадача обновлена - 201, id = " + id,
                    201);
        } catch (TaskIntersectionException e) {
            sendHasInteractions406(httpExchange, "Подзадача пересекается по времени с существующей задачей",
                    406);
        }
    }

    private void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //все задачи
        if (Pattern.matches(subtasksPath, path)) {
            taskManager.deleteAllSubTasks();
            sendSuccessButNoNeedToReturn201(httpExchange, "Подзадачи удалены", 201);
        }
        //задачи по айди
        if (Pattern.matches(subtasksIdPath, path)) {
            String requestMethod = httpExchange.getRequestMethod();
            String pathId = path.replaceFirst("/api/v1/subtasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                taskManager.deleteSubTaskById(id);
                sendText200(httpExchange, "Удалена подзадача с айди = " + id, 200);
            } else {
                System.out.println("Получен некорректный идентификатор задачи = " + id);
                httpExchange.sendResponseHeaders(405, 0);
            }
        } else {
            httpExchange.sendResponseHeaders(405, 0);
        }
    }

    private boolean isValidJsonSubtask(JsonObject jsonObject) {
        return jsonObject.has("name") &&
                jsonObject.has("description") &&
                jsonObject.has("id") &&
                jsonObject.has("status") &&
                jsonObject.has("type") &&
                jsonObject.has("start_time") &&
                jsonObject.has("duration");
    }
}
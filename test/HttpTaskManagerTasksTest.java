package test;

import com.google.gson.Gson;
import http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import taskmanager.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    private static final int TEST_PORT = 8081;

    // создаём экземпляр InMemoryTaskManager
    HistoryManager historyManager;
    TaskManager manager;
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);
        taskServer = new HttpTaskServer(manager, TEST_PORT); // Используем тестовый порт
        gson = Managers.getGson();

        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();

        taskServer.start();
    }

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 0,
                Status.NEW, Instant.now(), 5);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + TEST_PORT + "/api/v1/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении задачи");

        List<Task> tasksFromManager = manager.getAllTask();
        assertNotNull(tasksFromManager, "Список задач не должен быть null");
        Assertions.assertEquals(1, tasksFromManager.size(), "Неверное количество задач после добавления");

        Task addedTask = tasksFromManager.getFirst();
        Assertions.assertEquals(task.getName(), addedTask.getName(), "Неверное имя добавленной задачи");
        Assertions.assertEquals(task.getDescription(), addedTask.getDescription(), "Неверное описание добавленной задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2", 1, Instant.now(), 5);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + TEST_PORT + "/api/v1/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());

        // Проверяем код ответа
        Assertions.assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении эпика");

        List<Epic> epicFromManager = manager.getAllEpicsList();
        assertNotNull(epicFromManager, "Список задач не должен быть null");
        Assertions.assertEquals(1, epicFromManager.size(), "Неверное количество упиков после добавления");

        Epic addedTask = epicFromManager.get(0);
        Assertions.assertEquals(epic.getName(), addedTask.getName(), "Неверное имя добавленного эпика");
        Assertions.assertEquals(epic.getDescription(), addedTask.getDescription(), "Неверное описание добавленного эпика");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2", 1, Instant.now(), 5);
        manager.createEpic(epic);
        SubTask subTask = new SubTask("Test 3", "Testing task 3", 1, Status.NEW, epic.getId(), Instant.now(), 5);
        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + TEST_PORT + "/api/v1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении SubTask");

        ArrayList<SubTask> subTaskFromManager = manager.getAllSubTasks();
        assertNotNull(subTaskFromManager, "Список SubTask не должен быть null");
        System.out.println(subTaskFromManager);
        Assertions.assertEquals(1, subTaskFromManager.size(), "Неверное количество SubTask после добавления");

        SubTask addedSubTask = subTaskFromManager.get(0);
        Assertions.assertEquals(subTask.getName(), addedSubTask.getName(), "Неверное имя добавленного SubTask");
        Assertions.assertEquals(subTask.getDescription(), addedSubTask.getDescription(), "Неверное описание добавленного SubTask");
    }
}
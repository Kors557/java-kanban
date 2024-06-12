package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import taskmanager.Managers;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final int port;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager, int port) throws IOException {
        this.port = port;
        Gson gson = Managers.getGson();
        this.server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/api/v1/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/api/v1/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/api/v1/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/api/v1/history", new HistoryHandler(taskManager, gson));
        server.createContext("/api/v1/prioritized", new PrioritizedHandler(taskManager, gson));
        System.out.println("HTTP-сервер запущен на " + port + " порту!");
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault(), 8080);
    }

    public void start() {
        System.out.println("Started TaskServer on port " + port);
        System.out.println("http://localhost:" + port + "/api/v1/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + port);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
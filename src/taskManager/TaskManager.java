package taskManager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    int generateId();

    Task createTask(String name, String description, Status status);

    ArrayList<Task> getAllTask();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void deleteAllTasks();

    Task updateTask(String name, String description, Status status, int id);

    SubTask createSubTask(String name, String description, Status status, int idEpic);

    ArrayList<SubTask> getAllSubTasks();

    SubTask getSubTaskById(int id);

    void deleteAllSubTasks();

    void deleteById(int id);

    void updateSubTask(int id, String name, String description, Status status);

    Epic createEpic(String name, String description);

    HashMap<Epic, ArrayList<SubTask>> getAllEpics();

    Epic getEpicById(int id);

    void deleteAllEpics();

    void deleteEpicsById(int id);

    Epic updateEpic(int id, String name, String description);

    ArrayList<SubTask> getSubTasksByIdEpic(int id);

    void checkStatusEpic();

    List<? extends Task> getHistory();
}

package taskmanager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TaskManager {
    int generateId();

    Task createTask(String name, String description, Status status);

    Task createTask(Task task);

    ArrayList<Task> getAllTask();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void deleteAllTasks();

    Task updateTask(String name, String description, Status status, int id);

    Task updateTask(Task task);

    SubTask createSubTask(String name, String description, Status status, int idEpic);

    SubTask createSubTask(SubTask subTask);

    ArrayList<SubTask> getAllSubTasks();

    SubTask getSubTaskById(int id);

    void deleteAllSubTasks();

    void deleteSubTaskById(int id);

    SubTask updateSubTask(int id, String name, String description, Status status);

    void updateSubTask(SubTask subtask);

    Epic createEpic(String name, String description, Instant startTime, long duration);

    Epic createEpic(Epic epic);

    Map<Epic, ArrayList<SubTask>> getAllEpics();

    List<Epic> getAllEpicsList();

    Epic getEpicById(int id);

    void deleteAllEpics();

    void deleteEpicsById(int id);

    Epic updateEpic(int id, String name, String description);

    void updateEpic(Epic epic);

    ArrayList<SubTask> getSubTasksByIdEpic(int id);

    void checkStatusEpic();

    List<? extends Task> getHistory();

}

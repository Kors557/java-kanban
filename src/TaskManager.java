import java.util.ArrayList;

public class TaskManager {
    private int nextId = 1;
    ArrayList<Task> allTask = new ArrayList<>();

    public int generateId() {
        return nextId ++;
    }

    public Task createTask(String name, String description, Status status) {
        Task task = new Task(name, description, generateId(), status);
        allTask.add(task);
        return task;
    }

    public ArrayList<Task> getAllTask() {
        return allTask;
    }

    public void deleteTaskById(int id) {

    }





}

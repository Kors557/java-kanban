package task;
import task.Task;
import task.Status;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> idSubtask;
    public Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }

    public ArrayList<Integer> getIdSubtask() {
        return idSubtask;
    }
}
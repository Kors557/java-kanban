package epic;
import task.Task;
import status.Status;

import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<Integer> idSubtask;
    public Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
    }

    @Override
    public String toString() {
        return "epic.Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }
}
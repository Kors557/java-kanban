package task;

import java.util.List;

public class Epic extends Task {
    private List<Integer> idSubtask;

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

    public List<Integer> getIdSubtask() {
        return idSubtask;
    }
}
package task;

import java.util.List;

public class Epic extends Task {
    private List<Integer> idSubtask;

    private TaskType type = TaskType.EPIC;

    public Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
    }

    public Epic() {

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

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public void setType(TaskType type) {
        this.type = type;
    }
}
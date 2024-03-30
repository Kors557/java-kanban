package task;

import task.Status;
import task.Task;

public class SubTask extends Task {
    private int idEpic;

    public SubTask(String name, String description, int id, Status status, int idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return "task.SubTask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }

    public int getIdEpic() {
        return idEpic;
    }
}

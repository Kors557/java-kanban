package task;

public class SubTask extends Task {
    private int idEpic;

    public SubTask(String name, String description, int id, Status status, int idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
    }

    public SubTask() {

    }


    public void setEpicId(int id) {
        this.idEpic = id;
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

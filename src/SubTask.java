public class SubTask extends Task {

    public SubTask(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }
}

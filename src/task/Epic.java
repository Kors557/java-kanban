package task;

import java.time.Instant;
import java.util.List;

public class Epic extends Task {
    private List<Integer> idSubtask;

    private Instant endTime;

    private TaskType epicType = TaskType.EPIC;

    public Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
    }

    public Epic(String name, String description, int id, Instant startTime, long duration) {
        super(name, description, id, Status.NEW, startTime, duration);
        this.endTime = super.getEndTime();
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
                ", startTime='" + getStartTime().toEpochMilli() +
                ", endTime='"
                + getEndTime().toEpochMilli() +
                '\'' + ", duration='" + getDuration() +
                '}';
    }

    public List<Integer> getIdSubtask() {
        return idSubtask;
    }

    @Override
    public TaskType getSubTaskType() {
        return epicType;
    }

    @Override
    public void setSubTaskType(TaskType subTaskType) {
        this.epicType = subTaskType;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
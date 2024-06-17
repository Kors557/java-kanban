package task;

import com.google.gson.annotations.Expose;

import java.time.Instant;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    @Expose(serialize = false, deserialize = false)
    private TaskType type = TaskType.TASK;
    private Instant startTime;
    private long duration;


    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, int id, Status status, Instant startTime, long duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Instant getEndTime() {
        long secondsInMinute = 60L;
        return startTime.plusSeconds(duration * secondsInMinute);
    }

    @Override
    public String toString() {
        return "task.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime='" + startTime.toEpochMilli() +
                ", endTime='"
                + getEndTime().toEpochMilli() +
                '\'' + ", duration='" + duration +
                '}';
    }

    public TaskType getSubTaskType() {
        return type;
    }

    public String getTaskName() {
        return name;
    }

    public void setTaskName(String taskName) {
        this.name = taskName;
    }

    public void setSubTaskType(TaskType subTaskType) {
        this.type = subTaskType;
    }
}

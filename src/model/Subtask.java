package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    protected Integer epicId;

    public Subtask() {
        super();
    }

    public Subtask(String name, LocalDateTime startTime, Duration duration, Epic epic) {
        super(name, startTime, duration);
        if (epic != null)
            epicId = epic.getId();
    }

    public Subtask(String name, TaskStatus status, LocalDateTime startTime, Duration duration, Epic epic) {
        super(name, status, startTime, duration);
        if (epic != null)
            epicId = epic.getId();
    }

    public Subtask(Subtask task) {
        super(task);
        epicId = task.epicId;
    }

    @Override
    public Subtask clone() {
        return new Subtask(this);
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String description = getDescription();
        return "tasks.Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + status +
                ", epicId=" + getEpicId() +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", description.length='" + (description == null ? 0 : description.length()) + '\'' +
                '}';
    }
}

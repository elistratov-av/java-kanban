package tasks;

import java.util.Objects;

public class Subtask extends Task {
    protected Integer epicId;

    public Subtask() {
        super();
    }

    public Subtask(String name, Epic epic) {
        super(name);
        if (epic != null)
            epicId = epic.getId();
    }

    public Subtask(Subtask task) {
        super(task);
        epicId = task.epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask subtask)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        String description = getDescription();
        return "tasks.Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + status +
                ", epicId=" + getEpicId() +
                ", description.length='" + (description == null ? 0 : description.length()) + '\'' +
                '}';
    }
}

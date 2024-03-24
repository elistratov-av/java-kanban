package tasks;

public class Subtask extends Task {
    protected int epicId;

    public Subtask() {
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(Subtask task) {
        super(task);
        this.epicId = task.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String description = getDescription();
        return "tasks.Subtask{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", description.length='" + (description == null ? 0 : description.length()) + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}

package tasks;

public class Epic extends Task {
    public Epic() {
        super();
    }

    public Epic(String name) {
        super(name);
    }

    public Epic(Epic task) {
        super(task);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        String description = getDescription();
        return "tasks.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + status +
                ", description.length='" + (description == null ? 0 : description.length()) + '\'' +
                '}';
    }
}

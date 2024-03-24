package tasks;

public class Epic extends Task {
    public Epic() {
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Epic task) {
        super(task);
    }

    @Override
    public void setStatus(TaskStatus status) {
    }

    @Override
    public String toString() {
        String description = getDescription();
        return "tasks.Epic{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", description.length='" + (description == null ? 0 : description.length()) + '\'' +
                ", status=" + status +
                '}';
    }
}

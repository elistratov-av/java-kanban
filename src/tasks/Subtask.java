package tasks;

public class Subtask extends Task {
    protected Epic epic;

    public Subtask() {
    }

    public Subtask(String name, Epic epic) {
        super(name);
        this.epic = epic;
    }

    public Subtask(Subtask task) {
        super(task);
        this.epic = task.epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public Integer getEpicId() {
        return epic != null ? epic.getId() : null;
    }

    @Override
    public String toString() {
        String description = getDescription();
        return "tasks.Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description.length='" + (description == null ? 0 : description.length()) + '\'' +
                ", status=" + status +
                ", epicId=" + getEpicId() +
                '}';
    }
}

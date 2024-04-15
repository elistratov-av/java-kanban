package tasks;

public class Epic extends Task {
    public Epic(String name) {
        super(name);
    }

    public Epic(Epic task) {
        super(task);
    }

    @Override
    public void setStatus(TaskStatus status) {
        // Статус эпика расчетная величина, поэтому блокирую его смену через открытый метод
    }

    @Override
    public String toString() {
        String description = getDescription();
        return "tasks.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description.length='" + (description == null ? 0 : description.length()) + '\'' +
                ", status=" + status +
                '}';
    }
}

package tasks;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    protected final HashSet<Integer> subtaskIds = new HashSet<>();

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

    public Set<Integer> getSubtaskIds() {
        return Collections.unmodifiableSet(subtaskIds);
    }

    @Override
    public String toString() {
        String description = getDescription();
        return "tasks.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description.length='" + (description == null ? 0 : description.length()) + '\'' +
                ", status=" + status +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}

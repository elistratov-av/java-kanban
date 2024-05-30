package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

public class Epic extends Task {
    public Epic() {
        super();
    }

    public Epic(String name) {
        super(name, null, null);
    }

    public Epic(Epic task) {
        super(task);
    }

    @Override
    public Epic clone() {
        return new Epic(this);
    }

    //region Helpers

    private TaskStatus calcEpicStatus(Collection<Subtask> subtasks) {
        TaskStatus allStatus = null;
        for (Subtask st : subtasks) {
            TaskStatus status = st.getStatus();
            if (allStatus == null) {
                if (status == TaskStatus.IN_PROGRESS) return TaskStatus.IN_PROGRESS;
                allStatus = status;
            } else if (allStatus != status)
                return TaskStatus.IN_PROGRESS;
        }

        return allStatus == null ? TaskStatus.NEW : allStatus;
    }

    private void recalcDuration(Collection<Subtask> subtasks) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        for (Subtask st : subtasks) {
            LocalDateTime d = st.getStartTime();
            if (d != null) {
                if (startTime == null || startTime.isAfter(d))
                    startTime = d;
            }
            d = st.getEndTime();
            if (d != null) {
                if (endTime == null || endTime.isBefore(d))
                    endTime = d;
            }
        }

        this.startTime = startTime;
        this.duration = startTime != null && endTime != null ? Duration.between(startTime, endTime) : null;
    }

    //endregion

    public void updateState(Collection<Subtask> subtasks) {
        status = calcEpicStatus(subtasks);
        recalcDuration(subtasks);
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", description.length='" + (description == null ? 0 : description.length()) + '\'' +
                '}';
    }
}

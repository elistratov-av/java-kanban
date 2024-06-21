package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

public class Epic extends Task {
    protected LocalDateTime endTime;

    public Epic() {
        super();
    }

    public Epic(String name) {
        super(name, null, null);
    }

    public Epic(Epic task) {
        super(task);
        endTime = task.endTime;
    }

    @Override
    public Epic clone() {
        return new Epic(this);
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
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

    private LocalDateTime calcEndTime(Collection<Subtask> subtasks) {
        LocalDateTime endTime = null;
        for (Subtask st : subtasks) {
            LocalDateTime end = st.getEndTime();
            if (end != null) {
                if (endTime == null || endTime.isBefore(end))
                    endTime = end;
            }
        }
        return endTime;
    }

    private void recalcDuration(Collection<Subtask> subtasks) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration duration = null;
        for (Subtask st : subtasks) {
            LocalDateTime start = st.getStartTime();
            if (start != null) {
                if (startTime == null || startTime.isAfter(start))
                    startTime = start;
            }
            LocalDateTime end = st.getEndTime();
            if (end != null) {
                if (endTime == null || endTime.isBefore(end))
                    endTime = end;
            }
            if (start != null && end != null) {
                Duration d = Duration.between(startTime, endTime);
                if (duration != null)
                    duration = duration.plus(d);
                else duration = d;
            }
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    //endregion

    public void updateState(Collection<Subtask> subtasks) {
        status = calcEpicStatus(subtasks);
        recalcDuration(subtasks);
    }

    public void updateEndTime(Collection<Subtask> subtasks) {
        endTime = calcEndTime(subtasks);
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

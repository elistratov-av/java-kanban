package utils;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import tasks.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskCsvConverter {
    public static final String CSV_HEADER = "id,type,name,status,description,epic,startTime,duration";
    public static final int FIELDS_COUNT = 8;

    public static String toString(Task task) {
        return task != null ? toCsv(task).toString() : null;
    }

    private static StringBuilder toCsv(Task task) {
        Long duration = task.getDuration() != null ? task.getDuration().toMinutes() : null;
        return new StringBuilder(task.getId() + ",")
                .append(task.getType()).append(",")
                .append(StringUtils.emptyIfNull(task.getName())).append(",")
                .append(task.getStatus()).append(",")
                .append(StringUtils.emptyIfNull(task.getDescription())).append(",")
                .append(StringUtils.emptyIfNull(task.getEpicId())).append(",")
                .append(StringUtils.emptyIfNull(task.getStartTime())).append(",")
                .append(StringUtils.emptyIfNull(duration));
    }

    public static Task fromString(String value) {
        if (value == null || value.isBlank()) return null;

        String[] tokens = value.split(",", FIELDS_COUNT + 1);
        if (tokens.length < FIELDS_COUNT || "id".equals(tokens[0])) return null;

        TaskType taskType = TaskType.valueOf(tokens[1]);
        switch (taskType) {
            case TASK: return fromCsv(new Task(), tokens);
            case EPIC: return fromCsv(new Epic(), tokens);
            case SUBTASK: return fromCsv(new Subtask(), tokens);
            default: throw new IllegalStateException("Unexpected value: " + taskType);
        }
    }

    private static Task fromCsv(Task task, String[] tokens) {
        task.setId(Integer.parseInt(tokens[0]));
        task.setName(tokens[2]);
        task.setStatus(TaskStatus.valueOf(tokens[3]));
        task.setDescription(tokens[4]);
        task.setStartTime(!StringUtils.nullOrBlank(tokens[6]) ? LocalDateTime.parse(tokens[6]) : null);
        Long minutes = !StringUtils.nullOrBlank(tokens[7]) ? Long.parseLong(tokens[7]) : null;
        task.setDuration(minutes != null ? Duration.ofMinutes(minutes) : null);
        return task;
    }

    private static Task fromCsv(Subtask subtask, String[] tokens) {
        fromCsv((Task)subtask, tokens);
        subtask.setEpicId(Integer.parseInt(tokens[5]));
        return subtask;
    }
}

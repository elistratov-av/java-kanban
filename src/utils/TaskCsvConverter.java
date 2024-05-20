package utils;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import tasks.TaskType;

public class TaskCsvConverter {
    public static final String CSV_HEADER = "id,type,name,status,description,epic";

    public static String toString(Task task) {
        if (task == null) return null;

        if (task.getType() == TaskType.SUBTASK) {
            return toCsv((Subtask)task).toString();
        }
        return toCsv(task).toString();
    }

    private static StringBuilder toCsv(Task task) {
        return new StringBuilder(task.getId() + ",")
                .append(task.getType()).append(",")
                .append(StringUtils.emptyIfNull(task.getName())).append(",")
                .append(task.getStatus()).append(",")
                .append(StringUtils.emptyIfNull(task.getDescription())).append(",");
    }

    private static StringBuilder toCsv(Subtask subtask) {
        return toCsv((Task) subtask)
                .append(StringUtils.emptyIfNull(subtask.getEpicId()));
    }

    public static Task fromString(String value) {
        if (value == null || value.isBlank()) return null;

        String[] tokens = value.split(",", 7);
        if (tokens.length < 5 || "id".equals(tokens[0])) return null;

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
        return task;
    }

    private static Task fromCsv(Subtask subtask, String[] tokens) {
        fromCsv((Task)subtask, tokens);
        if (tokens.length > 5)
            subtask.setEpicId(Integer.parseInt(tokens[5]));
        return subtask;
    }
}

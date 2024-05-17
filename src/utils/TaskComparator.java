package utils;

import tasks.Subtask;
import tasks.Task;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task t1, Task t2) {
        if (t1 == t2) return 0;
        if (t1 == null) return -1;
        if (t2 == null) return 1;

        Class<? extends Task> cls1 = t1.getClass();
        Class<? extends Task> cls2 = t2.getClass();
        if (cls1 != cls2) {
            if (cls1 == Task.class) return -1;
            if (cls1 == Subtask.class) return 1;
            return cls2 == Task.class ? 1 : -1;
        }

        int cmp = Integer.compare(t1.getId(), t2.getId());
        if (cmp != 0) return cmp;
        cmp = StringUtils.compare(t1.getName(), t2.getName(), true);
        if (cmp != 0) return cmp;
        cmp = Integer.compare(t1.getStatus().ordinal(), t2.getStatus().ordinal());
        if (cmp != 0) return cmp;
        cmp = StringUtils.compare(t1.getDescription(), t2.getDescription(), true);
        if (cmp != 0) return cmp;

        if (cls1 == Subtask.class)
            return Integer.compare(((Subtask) t1).getEpicId(), ((Subtask) t2).getEpicId());
        return 0;
    }
}

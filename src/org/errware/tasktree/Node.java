package org.errware.tasktree;

public interface Node {
    int execute(TaskTree t);
    boolean validate(TaskTree t);
    boolean invalidate(TaskTree t);
}

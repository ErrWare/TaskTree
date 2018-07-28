package org.errware.tasktree;

public interface Node {
    int execute(TaskTree t);
    boolean isValid(TaskTree t);
    boolean isInvalid(TaskTree t);
}

package org.errware.tasktree.combat;

import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.TaskTree;

public class CombatCleanup extends AbstractNode {
    @Override
    public int execute( TaskTree taskTree) {
        return 0;
    }

    public CombatCleanup(){super(mc -> true, mc -> true);}
}

package org.errware.tasktree.combat;

import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.TaskTree;

public class FightSequence extends AbstractNode {
    @Override
    public int execute(TaskTree t) {
        return 1000;
    }

    @Override
    public boolean isValid(TaskTree t) {
        return true;
    }

    @Override
    public boolean isInvalid(TaskTree t) {
        //isInvalid if target dead or out of combat
        return !((CombatTree)t).getTargetNPC().exists() || !c.getLocalPlayer().isInCombat();
    }
}

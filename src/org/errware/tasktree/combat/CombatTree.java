package org.errware.tasktree.combat;

import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.methods.filter.Filter;
import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.LooperNode;
import org.errware.tasktree.Node;
import org.errware.tasktree.TaskTree;

import java.util.NoSuchElementException;
import java.util.Stack;

import static org.errware.tasktree.LooperNode.ExecutionType.INSISTENT;


public class CombatTree extends TaskTree {
    private NPC targetNPC;
    public void updateTargetNPC(int index){targetNPC = c.getNpcs().getLocalNPC(index);}
    public void updateTargetNPC(NPC npc){targetNPC=npc;}
    public NPC getTargetNPC(){return targetNPC;}

    public CombatTree(){ root = new LooperNode(INSISTENT); }

    public CombatTree( Filter<NPC> f){
        root.setExecutionType(INSISTENT);
        root.add(new TargetSelection(f));
        root.add(new FightSequence());
        root.add(new CombatCleanup());
    }
    public CombatTree( TargetSelection ts, FightSequence fs, CombatCleanup cc){
        root.setExecutionType(INSISTENT);
        root.add(ts);
        root.add(fs);
        root.add(cc);
    }
    @Override
    public boolean isValid(TaskTree t){
        //foughtSinceEntrance = false;
        c.log("Combat Tree Validated~~~~~~~~~~~~~");

        return true;
    }
    @Override
    public boolean isInvalid(TaskTree t){
        //return phase == Phase.PREFIGHT && foughtSinceEntrance;
        return false;
    }
}

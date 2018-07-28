package org.errware.tasktree.combat;

import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.wrappers.interactive.NPC;
import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.TaskTree;

//exists is false when npc dead
//realID is 0 when npc dead

public class TargetSelection extends AbstractNode {
    private Filter<NPC> npcFilter;
    private int targetIndex=-1;
    public int getTargetIndex(){return targetIndex;}
    public TargetSelection(Filter<NPC> f){
        npcFilter = f;
    }

    @Override
    public int execute(TaskTree t) {
        c.log("Combat Prefight if executed: " + !c.getLocalPlayer().isMoving());
        if(!c.getLocalPlayer().isMoving()) {
            NPC monster = c.getNpcs().closest(npcFilter);
            c.log("Monster null?: " + (monster==null ? "True" : "False"));
            c.log("Monster closest: " + monster.toString());
            if (monster != null) {
                monster.interact("Attack");
                targetIndex = monster.getIndex();
            } else {
                targetIndex = -1;
            }
        }
        //return positive number if some npc targetted
        return targetIndex;
    }
    @Override
    public boolean isValid(TaskTree t){
        return true;
    }

    @Override
    public boolean isInvalid(TaskTree t) {

        //Exit target selection when in combat or target is dead, return true
        c.log("Invalidate pre 1: " + (c.getLocalPlayer().isInCombat()  ? "True" : "False"));
        c.log("Invalidate pre 2: " + (targetIndex!=-1 ? "True" : "False") + "Target Index: " + targetIndex);
        if(targetIndex != -1) {
            NPC n = c.getNpcs().getLocalNPC(targetIndex);
            c.log("Invalidate npc == null? " + (n == null ? "True" : "False"));
            c.log("Invalidate pre 3: " + (!c.getNpcs().getLocalNPC(targetIndex).exists() ? "True" : "False"));
        }
        return c.getLocalPlayer().isInCombat() || (targetIndex!=-1 && !c.getNpcs().getLocalNPC(targetIndex).exists());
    }
}

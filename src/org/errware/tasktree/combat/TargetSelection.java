package org.errware.tasktree.combat;

import org.dreambot.api.methods.MethodContext;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.wrappers.interactive.NPC;
import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.TaskTree;

import java.util.function.Predicate;

//exists is false when npc dead
//realID is 0 when npc dead

public class TargetSelection extends AbstractNode {
    private Filter<NPC> npcFilter;
    private int targetIndex=-1;
    private int myIndex;
    public int getTargetIndex(){return targetIndex;}
    public TargetSelection(Filter<NPC> f){
        npcFilter = f;
    }
    private Map map;
    public final Predicate<NPC> available = n -> n != null && (n.getInteractingIndex() == myIndex || n.getInteractingIndex() == -1) && map.canReach(n);
    @Override
    public int execute(TaskTree t) {
        c.log("Prefight check");
        NPC targetNpc = targetIndex==-1 ? null : c.getNpcs().getLocalNPC(targetIndex);
        // If I'm not moving or my target is interacting with someone else
        if(!c.getLocalPlayer().isMoving() || (targetNpc != null && targetNpc.getInteractingIndex() != myIndex && targetNpc.getInteractingIndex() != -1 )) {
            // first check for NPC interacting with me
            NPCs npcs = c.getNpcs();
            NPC monster = npcs.closest(npc -> npc != null && npc.isInteracting(c.getLocalPlayer()));
            // if none found get closest one by filter
            if(monster == null) {
                c.log("no monster interacting with me");
                // get closest available monster matching filter
                // :: is a way to reference a specific method, sort of like obj.method without the calling ()
                monster = npcs.closest(available.and(npcFilter::match)::test);
            }
            //c.log("Monster null?: " + (monster==null ? "True" : "False"));
            //c.log("Monster closest: " + monster.toString());
            if (monster != null) {
                c.log("Attacking");
                monster.interact("Attack");
                targetIndex = monster.getIndex();

            }
        }
        return 400;
    }
    @Override
    public boolean isValid(TaskTree t){
        myIndex = c.getLocalPlayer().getIndex();
        map = new Map(c.getClient());
        return true;
    }

    @Override
    public boolean isInvalid(TaskTree t) {

        //Exit target selection when in combat or target is dead, return true
        //c.log("TargetSelection invalidation: ");
        //c.log("player in combat: " + (c.getLocalPlayer().isInCombat()  ? "True" : "False"));
        //c.log("targetIndex != -1: " + (targetIndex!=-1 ? "True" : "False") + "Target Index: " + targetIndex);
        if(targetIndex != -1) {
            NPC n = c.getNpcs().getLocalNPC(targetIndex);
            ((CombatTree)t).updateTargetNPC(n);
            //c.log("Invalidate npc == null? " + (n == null ? "True" : "False"));
            //c.log("Invalidate pre 3: " + (!c.getNpcs().getLocalNPC(targetIndex).exists() ? "True" : "False"));
        }
        NPC n = ((CombatTree)t).getTargetNPC();
        //assumes single combat for now
        return n != null && n.isInteracting(c.getLocalPlayer());//c.getLocalPlayer().isInCombat() || (targetIndex!=-1 && !c.getNpcs().getLocalNPC(targetIndex).exists());
    }
}

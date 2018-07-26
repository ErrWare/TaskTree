package org.errware.tasktree.combat;

import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.methods.filter.Filter;
import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.Node;
import org.errware.tasktree.TaskTree;


public class CombatTree extends TaskTree {
    protected Node enterCombat;
    protected Node doCombat;
    protected Node exitCombat;
    private NPC targetNPC;
    public void updateTargetNPC(int index){targetNPC = c.getNpcs().getLocalNPC(index);}
    public void updateTargetNPC(NPC npc){targetNPC=npc;}
    public NPC getTargetNPC(){return targetNPC;}
    public CombatTree(){ }
    protected enum Phase{PREFIGHT, FIGHT, POSTFIGHT}
    Phase phase = Phase.PREFIGHT;
    boolean phaseValid = false; //keeps track of whether branches have been valid in the past or not
                                //if so we'll want to keep executing them even if the valid precondition
                                //no longer holds - until invalid postcondition is true
                                //just like in the ordinary execution of tasktrees
    protected Node[] phases = new Node[3];
    protected boolean foughtSinceEntrance;

    public CombatTree( Filter<NPC> f){
        enterCombat = new TargetSelection(f);
        doCombat = new FightSequence();
        exitCombat = new CombatCleanup();
    }
    public CombatTree( Node n0, Node n1, Node n2){
        enterCombat = n0;
        doCombat = n1;
        exitCombat = n2;
    }
    @Override
    public String toString(){
        return "Combat Tree: " + super.toString();
    }
    @Override
    public boolean validate( TaskTree t){
        t.traceNode(this);
        foughtSinceEntrance = false;
        c.log("Combat Tree Validated~~~~~~~~~~~~~");
        return true;
    }
    @Override
    public boolean invalidate( TaskTree t){
        if(phase == Phase.PREFIGHT && foughtSinceEntrance){
            t.untraceNode();
            return true;
        }
        return false;
    }
    @Override
    public int execute(TaskTree t){
        //try each stage at least once
        c.log("Combat Node Executing");
        c.log("Phase val: " + phase);
        switch (phase) {
            case PREFIGHT:
                c.log("Prefight! - phase valid? " + (phaseValid?"true":"false"));
                if(phaseValid && enterCombat.invalidate(this)){
                    c.log("Setting phase to FIGHT");
                    phase=Phase.FIGHT;
                    foughtSinceEntrance = true;
                    phaseValid = false;
                    return 10;
                }
                else if(phaseValid || enterCombat.validate(this)){
                    phaseValid = true;
                    int targetIndex = enterCombat.execute(this);
                    c.log("Target Index: " + targetIndex);
                    if(targetIndex!=-1){
                        //npc targeted
                        //here is where we start combat scribe
                        updateTargetNPC(targetIndex);
                        //return 1000;
                    }
                    return 250;
                }
            case FIGHT:
                if(phaseValid && doCombat.invalidate(this)){
                    phase=Phase.POSTFIGHT;
                    phaseValid = false;
                    //here we stop combat scribe
                    return 0;
                }
                //maybe don't even need doCombat.validate?
                else if(phaseValid || doCombat.validate(this)){
                    phaseValid = true;
                    //doCombat returns wait ms time - might be dependent on location in sequence
                    return doCombat.execute(this);
                }
            case POSTFIGHT:
                if(phaseValid && exitCombat.invalidate(this)){
                    phaseValid = false;
                    phase=Phase.PREFIGHT;
                }
                else if(phaseValid || exitCombat.validate(this)){
                    phaseValid = true;
                    return exitCombat.execute(this);
                }
        }
        return 800;
    }

    //<editor-fold desc="static vs non-static nested class discussion">
    /*
    For reference: https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html
    static -> others can make w/o making a CombatNode,
        but since protected can they?
        all CombatNodes share same definition
        objx of class can still differ in attributes
        subclasses of CombatNode can still extend
            "a static nested class cannot refer directly to instance
            variables or methods defined in its enclosing class: it
            can use them only through an object reference."
    nonstatic -> must make through a CombatNode
    */
    //</editor-fold>

    //exists is false when npc dead
    //realID is 0 when npc dead
    protected class TargetSelection extends AbstractNode {
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
        public boolean validate(TaskTree t){
            return true;
        }

        @Override
        public boolean invalidate(TaskTree t) {

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

    //if overriden with a fightsequence tree it is recommended
    //that the invalidate be dependent on the emptiness of the trace
    protected class FightSequence extends AbstractNode{
        @Override
        public int execute(TaskTree t) {
            return 1000;
        }

        @Override
        public boolean validate( TaskTree t) {
            return true;
        }

        @Override
        public boolean invalidate(TaskTree t) {
            //invalidate if target dead or out of combat
            return !((CombatTree)t).getTargetNPC().exists() || !c.getLocalPlayer().isInCombat();
        }
    }

    protected class CombatCleanup extends AbstractNode{
        boolean flag;
        @Override
        public int execute( TaskTree taskTree) {
            return 0;
        }

        @Override
        public boolean validate(TaskTree taskTree) {
            flag=true;
            return true;
        }

        @Override
        public boolean invalidate( TaskTree taskTree) {
            if(flag){
                flag = false;
                return true;
            }
            return false;
        }
    }
}

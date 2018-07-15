package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.methods.filter.Filter;


public class CombatTree extends TaskTree {
    protected Node enterCombat;
    protected Node doCombat;
    protected Node exitCombat;
    private NPC targetNPC;
    public void updateTargetNPC(int index){targetNPC = c.getNpcs().getLocalNPC(index);}
    public void updateTargetNPC(NPC npc){targetNPC=npc;}
    public NPC getTargetNPC(){return targetNPC;}
    public CombatTree(MethodContext c){
        super(c);
    }
    protected enum Phase{PREFIGHT, FIGHT, POSTFIGHT}
    Phase phase = Phase.PREFIGHT;
    boolean phaseValid = false; //keeps track of whether branches have been valid in the past or not
                                //if so we'll want to keep executing them even if the valid precondition
                                //no longer holds - until invalid postcondition is true
                                //just like in the ordinary execution of tasktrees
    protected Node[] phases = new Node[3];
    protected boolean foughtSinceEntrance;

    public CombatTree(MethodContext c, Filter<NPC> f){
        super(c);
        enterCombat = new TargetSelection(f);
        doCombat = new FightSequence();
        exitCombat = new CombatCleanup();
    }
    public CombatTree(MethodContext c, Node n0, Node n1, Node n2){
        super(c);
        enterCombat = n0;
        doCombat = n1;
        exitCombat = n2;
    }
    @Override
    public String toString(){
        return "Combat Tree: " + super.toString();
    }
    @Override
    public boolean validate(MethodContext c, TaskTree t){
        t.traceNode(this);
        foughtSinceEntrance = false;
        c.log("Combat Tree Validated~~~~~~~~~~~~~");
        return true;
    }
    @Override
    public boolean invalidate(MethodContext c, TaskTree t){
        if(phase == Phase.PREFIGHT && foughtSinceEntrance){
            t.untraceNode();
            return true;
        }
        return false;
    }
    @Override
    public int execute(MethodContext c, TaskTree t){
        //try each stage at least once
        c.log("Combat Node Executing");
        c.log("Phase val: " + phase);
        switch (phase) {
            case PREFIGHT:
                c.log("Prefight! - phase valid? " + (phaseValid?"true":"false"));
                if(phaseValid && enterCombat.invalidate(c,this)){
                    c.log("Setting phase to FIGHT");
                    phase=Phase.FIGHT;
                    foughtSinceEntrance = true;
                    phaseValid = false;
                    return 10;
                }
                else if(phaseValid || enterCombat.validate(c, this)){
                    phaseValid = true;
                    int targetIndex = enterCombat.execute(c,this);
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
                if(phaseValid && doCombat.invalidate(c,this)){
                    phase=Phase.POSTFIGHT;
                    phaseValid = false;
                    //here we stop combat scribe
                    return 0;
                }
                //maybe don't even need doCombat.validate?
                else if(phaseValid || doCombat.validate(c,this)){
                    phaseValid = true;
                    //doCombat returns wait ms time - might be dependent on location in sequence
                    return doCombat.execute(c,this);
                }
            case POSTFIGHT:
                if(phaseValid && exitCombat.invalidate(c,this)){
                    phaseValid = false;
                    phase=Phase.PREFIGHT;
                }
                else if(phaseValid || exitCombat.validate(c,this)){
                    phaseValid = true;
                    return exitCombat.execute(c,this);
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
    protected class TargetSelection extends Node{
        private Filter<NPC> npcFilter;
        private int targetIndex=-1;
        public int getTargetIndex(){return targetIndex;}
        public TargetSelection(Filter<NPC> f){
            npcFilter = f;
        }

        @Override
        public int execute(MethodContext c, TaskTree t) {
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
        public boolean validate(MethodContext c, TaskTree t){
            return true;
        }

        @Override
        public boolean invalidate(MethodContext c, TaskTree t) {

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
    protected class FightSequence extends Node{
        @Override
        public int execute(MethodContext c, TaskTree t) {
            return 1000;
        }

        @Override
        public boolean validate(MethodContext c, TaskTree t) {
            return true;
        }

        @Override
        public boolean invalidate(MethodContext c, TaskTree t) {
            //invalidate if target dead or out of combat
            return !((CombatTree)t).getTargetNPC().exists() || !c.getLocalPlayer().isInCombat();
        }
    }

    protected class CombatCleanup extends ENode{

        @Override
        public int execute(MethodContext methodContext, TaskTree taskTree) {
            return 0;
        }

        @Override
        public boolean validate(MethodContext methodContext, TaskTree taskTree) {
            flag=true;
            return true;
        }

        @Override
        public boolean invalidate(MethodContext methodContext, TaskTree taskTree) {
            if(flag){
                flag = false;
                return true;
            }
            return false;
        }
    }
}

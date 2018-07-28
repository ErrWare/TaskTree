package org.errware.tasktree.combat;

import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.methods.filter.Filter;
import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.LooperNode;
import org.errware.tasktree.Node;
import org.errware.tasktree.TaskTree;

import java.util.Stack;

import static org.errware.tasktree.LooperNode.ExecutionType.INSISTENT;


public class CombatTree extends TaskTree {
    /*
    protected Node enterCombat;
    protected Node doCombat;
    protected Node exitCombat;
    */
    private NPC targetNPC;
    public void updateTargetNPC(int index){targetNPC = c.getNpcs().getLocalNPC(index);}
    public void updateTargetNPC(NPC npc){targetNPC=npc;}
    public NPC getTargetNPC(){return targetNPC;}

    public CombatTree(){ root = new LooperNode(INSISTENT); }
    //protected enum Phase{PREFIGHT, FIGHT, POSTFIGHT}
    //Phase phase = Phase.PREFIGHT;
    //boolean phaseValid = false; //keeps track of whether branches have been valid in the past or not
                                //if so we'll want to keep executing them even if the valid precondition
                                //no longer holds - until invalid postcondition is true
                                //just like in the ordinary execution of tasktrees
    //protected Node[] phases = new Node[3];
    //protected boolean foughtSinceEntrance;

    public CombatTree( Filter<NPC> f){
        /*
        enterCombat = new TargetSelection(f);
        doCombat = new FightSequence();
        exitCombat = new CombatCleanup();
        */
        //I don't like having to specify the execution type last
        //fix it
        root = new LooperNode();
        root.add(new TargetSelection(f));
        root.add(new FightSequence());
        root.add(new CombatCleanup());
        root.setExecutionType(INSISTENT);
        trace = new Stack();
        trace.push(root);
    }
    public CombatTree( TargetSelection ts, FightSequence fs, CombatCleanup cc){
        root = new LooperNode();
        root.add(ts);
        root.add(fs);
        root.add(cc);
        root.setExecutionType(INSISTENT);
        trace = new Stack();
        trace.push(root);
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
    /*
    //old logic, let's see if can replace w/ insistent nodes
    @Override
    public int execute(TaskTree t){
        //try each stage at least once
        c.log("Combat Node Executing");
        c.log("Phase val: " + phase);
        switch (phase) {
            case PREFIGHT:
                c.log("Prefight! - phase valid? " + (phaseValid?"true":"false"));
                if(phaseValid && enterCombat.isInvalid(this)){
                    c.log("Setting phase to FIGHT");
                    phase=Phase.FIGHT;
                    foughtSinceEntrance = true;
                    phaseValid = false;
                    return 10;
                }
                else if(phaseValid || enterCombat.isValid(this)){
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
                if(phaseValid && doCombat.isInvalid(this)){
                    phase=Phase.POSTFIGHT;
                    phaseValid = false;
                    //here we stop combat scribe
                    return 0;
                }
                //maybe don't even need doCombat.validate?
                else if(phaseValid || doCombat.isValid(this)){
                    phaseValid = true;
                    //doCombat returns wait ms time - might be dependent on location in sequence
                    return doCombat.execute(this);
                }
            case POSTFIGHT:
                if(phaseValid && exitCombat.isInvalid(this)){
                    phaseValid = false;
                    phase=Phase.PREFIGHT;
                }
                else if(phaseValid || exitCombat.isValid(this)){
                    phaseValid = true;
                    return exitCombat.execute(this);
                }
        }
        return 800;
    }
    */

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
}

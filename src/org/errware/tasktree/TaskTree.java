package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

import java.util.ArrayList;
import java.util.Stack;

public class TaskTree extends AbstractNode {
    protected LooperNode root;
    protected Stack<Node> trace;

    public TaskTree(){}

    public boolean isValid( TaskTree y){return true;}
    //<editor-fold desc="isInvalid discussion">
    /* isInvalid is probably the most important method to override, wondering what to make the default
     * Option 1: false
     *      Never unravel a TaskTree once it's been added to the trace
     * Option 2: trace.peek() == root
     *      Unravel a TaskTree if it is idle at the start of a single execution.
     * Option 3?: trace.empty()
     *      Unravel if the root node has been invalidated
     *      makes root node's invalidity the invalidation condition
     *      would mean isValid should push the root onto the trace before it returns true
     * Option 4?: track how many times consecutively trace.peek() == root, invalidate after certain threshold
     *      would be an ad hoc fix to option 2's impatience
     */
    //</editor-fold>
    public boolean isInvalid( TaskTree y){return trace.peek() == root;}
    public final int execute(){return this.execute(null);}

    public int execute(TaskTree t){
        invalidate();
        return trace.peek().execute(this);  // pass this to claim ownership of called nodes
    }

    protected ArrayList<Node> interruptHandler;
    protected void interrupt(int interruptCode){
        interruptHandler.get(interruptCode).execute(this);
    }


    //Pass reference of tree down execution pipe
    //When a node is validated its parent adds it to the trace
    //There has to be a better way, having this public is unsafe -
    //what if something from another tree decides to add itself to this trace
    //but maybe that would be cool??
    public void traceNode(Node n){
        trace.push(n);
    }

    //untraceNode now responsibility of TaskTree
    private Node untraceNode(){
        return trace.pop();
    }
    // standard invalidation / trace unrolling
    protected int invalidate(){
        int totalInvalidated = 0;
        while(trace.peek().isInvalid(this)) {
            totalInvalidated++;
            untraceNode();
        }
        c.log("Invalidated " + totalInvalidated);
        return totalInvalidated;
    }
}

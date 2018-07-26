package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

import java.util.ArrayList;
import java.util.Stack;

public abstract class TaskTree extends AbstractNode {
    protected Node root;
    protected Stack<Node> trace;

    protected ArrayList<Node> interruptHandler;
    protected void interrupt(int interruptCode){
        interruptHandler.get(interruptCode).execute(this);
    }

    public TaskTree(){}

    //I don't like having to make TaskTree have these parameters too but I see no other way...
    public final int execute(){return this.execute(this);}
    //both valid and invalid by default
    public boolean validate(MethodContext x, TaskTree y){return true;}
    public boolean invalidate(MethodContext x, TaskTree y){return true;}

    //Pass reference of tree down execution pipe
    //When a node is validated its parent adds it to the trace
    //There has to be a better way, having this public is unsafe -
    //what if something from another tree decides to add itself to this trace
    //but maybe that would be cool??
    public void traceNode(Node n){
        trace.push(n);
    }
    public Node untraceNode(){
        return trace.pop();
    }
}

package org.errware.tasktree.looperbehaviour;

import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.TaskTree;

import java.util.List;


//Ideally should be an interface, making it a class for simplicity for now
//want to implement some default non-public methods

public abstract class LooperBehaviour {
    protected List<AbstractNode> nodes;
    public LooperBehaviour(List<AbstractNode> nodes){
        this.nodes = nodes;
    }
    public abstract int execute(TaskTree t);
    public void init(){}
    protected boolean validate(AbstractNode n, TaskTree t){
        if(n.isValid(t)) {
            t.traceNode(n);
            return true;
        }
        return false;
    }
}

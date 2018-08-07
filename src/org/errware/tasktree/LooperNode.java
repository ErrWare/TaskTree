package org.errware.tasktree;

import org.errware.tasktree.looperbehaviour.*;

import java.util.ArrayList;
import java.util.List;

import static org.errware.tasktree.LooperNode.ExecutionType.*;

public class LooperNode extends AbstractNode{
    //<editor-fold desc="Validation">
    @Override
    public void init(){
        myBehaviour.init();
        for(AbstractNode n: nodes)
            n.init();
    }
    @Override
    public boolean isValid(TaskTree t) {
        return true;
    }
    @Override
    public boolean isInvalid(TaskTree t) { return false; }

    public enum ExecutionType { NORMAL, STATEFUL, INSISTENT }
    private LooperBehaviour myBehaviour;

    private List<AbstractNode> nodes;          //could've used polymorphism to allow unspecified iterable type

    public LooperNode(){
        nodes = new ArrayList<>();
        setExecutionType(NORMAL);
    }
    public LooperNode(ExecutionType type){
        nodes = new ArrayList<>();
        setExecutionType(type);
    }

    @Override
    public int execute( TaskTree t) {
        return myBehaviour.execute(t);
    }

    public void add(AbstractNode n){nodes.add(n);}

    public void setExecutionType(ExecutionType newMode) {
        switch(newMode){
            case STATEFUL:
                myBehaviour = new LooperBehaviourStateful(nodes);
                break;
            case INSISTENT:
                myBehaviour = new LooperBehaviourInsistent(nodes);
                break;
            case NORMAL:
                myBehaviour = new LooperBehaviourNormal(nodes);
                break;
        }
    }
}

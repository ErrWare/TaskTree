package org.errware.tasktree.looperbehaviour;

import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.TaskTree;

import java.util.Iterator;
import java.util.List;

public class LooperBehaviourInsistent extends LooperBehaviour{
    private Iterator<AbstractNode> iterator;
    //For insistent execution
    private AbstractNode nextNode;
    private AbstractNode executingNode;

    public LooperBehaviourInsistent(List<AbstractNode> nodes){
        super(nodes);
    }

    public int execute(TaskTree t){
        //<editor-fold desc="Insistent Execution">;
        assert nextNode != null;
        if(validate(nextNode, t)){
            // set up node for execution
            executingNode = nextNode;
            // set up nextnode to check
            if(!iterator.hasNext()) iterator = nodes.iterator();
            nextNode = iterator.next();
            //c.log("Now executing: " + executingNode.getClass() + " : " + executingNode.toString());
            return executingNode.execute(t);
        }
        //</editor-fold>
        return 200;
    }

    @Override
    public void init(){
        iterator = nodes.iterator();
        nextNode = iterator.next();
    }
}

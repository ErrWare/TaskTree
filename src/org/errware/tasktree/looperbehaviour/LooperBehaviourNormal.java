package org.errware.tasktree.looperbehaviour;

import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.TaskTree;

import java.util.Iterator;
import java.util.List;

// NORMAL:   executes first valid child in list
public class LooperBehaviourNormal extends LooperBehaviour{
    public LooperBehaviourNormal(List<AbstractNode> nodes){
        super(nodes);
    }
    @Override
    public int execute(TaskTree t){
        Iterator<AbstractNode> iterator = nodes.iterator();
        AbstractNode n;
        while(iterator.hasNext()) {
            n = iterator.next();
            if (validate(n,t))
                return n.execute(t);
        }
        return 200;
    }
}

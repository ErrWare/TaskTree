package org.errware.tasktree.looperbehaviour;

import org.errware.tasktree.AbstractNode;
import org.errware.tasktree.TaskTree;

import java.util.Iterator;
import java.util.List;

// STATEFUL: like normal but continues child validation after previously validated child
public class LooperBehaviourStateful extends LooperBehaviour{
    private Iterator<AbstractNode> iterator;
    public LooperBehaviourStateful(List<AbstractNode> nodes){
        super(nodes);
    }
    @Override
    public int execute(TaskTree t){
        //<editor-fold desc="Stateful Execution">
        if (!iterator.hasNext()) iterator = nodes.iterator();
        AbstractNode n;
        while(iterator.hasNext()) {
            n = iterator.next();
            if (validate(n,t))
                return n.execute(t);
        }
        //</editor-fold>
        return 200;
    }
    @Override
    public void init(){
        iterator = nodes.iterator();
    }
}

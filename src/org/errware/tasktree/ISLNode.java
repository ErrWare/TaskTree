package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

//insistent node
//every subnode must execute in order
public abstract class ISLNode extends SLNode{
    private Node nextNode;
    private Node executingNode;

    @Override
    public int execute(MethodContext c, TaskTree t){
        if(nextNode.validate(c, t)){
            executingNode = nextNode;
            nextNode = i.next();
            return executingNode.execute(c,t);
        }
        return(500);
    }
}

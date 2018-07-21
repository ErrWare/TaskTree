package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

public class LooperNode extends LNode{
    @Override
    public boolean validate(MethodContext c, TaskTree t) {
        return true;
    }

    @Override
    public boolean invalidate(MethodContext c, TaskTree t) {
        return false;   //false means we will never make the player
        //to leave this decision tree
    }
}

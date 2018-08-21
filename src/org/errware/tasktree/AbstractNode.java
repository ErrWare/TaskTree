package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

import java.util.function.Predicate;

public abstract class AbstractNode implements Node {
    protected static MethodContext c;
    public static void setContext(MethodContext _c){c = _c;}
    public static MethodContext getContext(){return c;}
    public void init(){}
    
    protected Predicate<MethodContext> validityPred;
    protected Predicate<MethodContext> invalidityPred;
    protected abstract void onValid();
    protected abstract void onInvalid();
    @Override
    public final boolean isValid(TaskTree t){
        if(validityPred.test(c)){
            onValid();
            return true;
        }
        return false;
    }
    @Override
    public final boolean isInvalid(TaskTree t){
        if(invalidityPred.test(c)){
            onInvalid();
            return true;
        }
        return false;
    }
}

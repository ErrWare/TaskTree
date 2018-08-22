package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

import java.util.function.Predicate;

public abstract class AbstractNode implements Node {
    protected static MethodContext c;
    public static void setContext(MethodContext _c){c = _c;}
    public static MethodContext getContext(){return c;}
    public void init(){}

    protected Predicate<MethodContext> validityPred = c -> false;
    protected Predicate<MethodContext> invalidityPred = c -> true;
    protected void onValid(){}
    protected void onInvalid(){}
    protected AbstractNode(){};
    protected AbstractNode(Predicate<MethodContext> vP, Predicate<MethodContext> iP){
        setValidityPred(vP);
        setInvalidityPred(iP);
    }
    protected void setValidityPred(Predicate<MethodContext> vP){
        if (vP != null)
            validityPred = vP;
    }
    protected void setInvalidityPred(Predicate<MethodContext> iP){
        if (iP != null)
            invalidityPred = iP;
    }
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

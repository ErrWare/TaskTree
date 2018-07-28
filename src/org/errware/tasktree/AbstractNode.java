package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

public abstract class AbstractNode implements Node {
    protected static MethodContext c;
    public static void setContext(MethodContext _c){c = _c;}
    public static MethodContext getContext(){return c;}
}

import org.dreambot.api.methods.MethodContext;

import java.util.Stack;

public abstract class TaskTree {
    protected Node root;
    protected Stack<Node> trace;
    protected MethodContext c;

    public TaskTree(MethodContext _c){_c.log("TaskTree reached");c=_c;}

    public abstract int execute();
    //Pass reference of tree down execution pipe
    //Nodes that validate will add themselves to the Tree's trace
    //There has to be a better way, having this public is unsafe -
    //what if something from another tree decides to add itself to this trace
    //but maybe that would be cool??
    public void traceNode(Node n){
        trace.push(n);
    }
    public Node untraceNode(){
        return trace.pop();
    }
}

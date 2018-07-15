package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

import java.util.Iterator;

//Stateful Logic Node
public abstract class SLNode extends LNode {
    private Iterable<Node> nodes;   //using polymorphism to allow unspecified iterable type
    protected Iterator<Node> i;   //declaring from interface!

    @Override
    public int execute(MethodContext c, TaskTree t) {
        //c.log("SLNode executing");

        while(i.hasNext()){
            Node n = i.next();
            if (n.validate(c, t))
                return n.execute(c, t);
        }
        //Should remake iterator if stateful Lnode found no valid nodes?
        //c.log("Reinitializing iterator");
        i = nodes.iterator();

        c.log("LNode didn't validate any children");
        return(250);
    }
    protected void initerator(){i=nodes.iterator();};
    public void setNodes(Iterable<Node> _nodes){nodes=_nodes;i=nodes.iterator();}

}

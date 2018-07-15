package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

import java.util.Iterator;

public abstract class LNode extends Node {

    private Iterable<Node> nodes;   //using polymorphism to allow unspecified iterable type

    @Override
    public int execute(MethodContext c, TaskTree t) {
        //c.log("LNode executing");

        Iterator<Node> i = nodes.iterator();
        while(i.hasNext()) {
            Node n = i.next();
            c.log("Node in LNode: " + n.toString());
            if (n.validate(c, t)) {
                c.log("LNode validated " + n);
                int z = n.execute(c,t);
                c.log(z + " returned by " + n.toString());
                return z;//n.execute(c, t);
            }
        }
        c.log("LNode didn't validate any childrennnn");
        return(250);
    }

    public void setNodes(Iterable<Node> _nodes){nodes=_nodes;}
}

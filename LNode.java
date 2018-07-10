import org.dreambot.api.methods.MethodContext;

import java.util.Iterator;
import java.util.List;

public abstract class LNode extends Node{

    private Iterable<Node> nodes;   //using polymorphism to allow unspecified iterable type
    private boolean stateful = false;
    private Iterator<Node> i;   //declaring from interface!

    @Override
    public int execute(MethodContext c, TaskTree t) {
        //c.log("LNode executing");
        //If not stateful just iterate through and execute on first valid node
        if(!stateful){
            //c.log("Stateless execution");
            i = nodes.iterator();
            while(i.hasNext()) {
                Node n = i.next();
                if (n.validate(c, t)) {
                    //c.log("LNode validated " + n);
                    return n.execute(c, t);
                }
            }
        }
        //If stateful start where the last iterator left off
        else{
            c.log("Stateful execution");
            while(i.hasNext()){
                Node n = i.next();
                if (n.validate(c, t))
                    return n.execute(c, t);
            }
            //Should remake iterator if stateful Lnode found no valid nodes?
            //c.log("Reinitializing iterator");
            i = nodes.iterator();
        }
        c.log("LNode didn't validate any children");
        return(250);
    }
    protected void initerator(){i=nodes.iterator();};
    public void setNodes(Iterable<Node> _nodes){nodes=_nodes;if(stateful)i=nodes.iterator();}
}

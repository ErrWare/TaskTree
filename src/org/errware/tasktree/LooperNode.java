package org.errware.tasktree;

import org.dreambot.api.methods.MethodContext;

import java.util.List;
import java.util.Iterator;

import static org.errware.tasktree.LooperNode.ExecutionType.NORMAL;

public class LooperNode extends AbstractNode{

    //<editor-fold desc="Validation">
    @Override
    public boolean validate( TaskTree t) {
        return true;
    }

    @Override
    public boolean invalidate( TaskTree t) { return false; }
    //</editor-fold>

    //<editor-fold desc="ExecutionType setup"
    public enum ExecutionType { NORMAL, STATEFUL, INSISTENT }
    // NORMAL:   executes first valid child in list
    // STATEFUL: like normal but continues child validation after previously validated child
    // INSITENT: every subnode must execute in order, implies stateful
    //</editor-fold>

    ExecutionType executionType = NORMAL;
    private List<Node> nodes;          //could've used polymorphism to allow unspecified iterable type
    protected Iterator<Node> iterator;      //instead kept initial release simple - show how to make a generic type

    private Node nextNode;
    private Node executingNode;

    @Override
    public int execute( TaskTree t) {
        switch(executionType)
        {
            //<editor-fold desc="Normal Execution">
            case NORMAL:
                iterator = nodes.iterator();
                while(iterator.hasNext()) {
                    Node n = iterator.next();
                    c.log("Node in LNode: " + n.toString());
                    if (n.validate(t)) {
                        c.log("LNode validated " + n);
                        int z = n.execute(t);
                        c.log(z + " returned by " + n.toString());
                        return z;//n.execute(c, t);
                    }
                }
                c.log("LNode didn't validate any childrennnn");
                return(250);
            //</editor-fold>
            //<editor-fold desc="Normal Execution">
            case STATEFUL:
                while(iterator.hasNext()){
                    Node n = iterator.next();
                    if (n.validate(t))
                        return n.execute(t);
                }
                //Should remake iterator if stateful Lnode found no valid nodes?
                //c.log("Reinitializing iterator");
                iterator = nodes.iterator();

                c.log("LNode didn't validate any children");
                return(250);
            //</editor-fold>
            //<editor-fold desc="Normal Execution">
            case INSISTENT:
                if(nextNode.validate(t)){
                    executingNode = nextNode;
                    nextNode = iterator.next();
                    return executingNode.execute(t);
                }
                return(500);
            //</editor-fold>
        }
        c.log("Node " + this.toString() + " executed no children");
        return 200;
    }

    public void add(Node n){nodes.add(n);}
}

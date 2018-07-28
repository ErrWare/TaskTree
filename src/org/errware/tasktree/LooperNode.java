package org.errware.tasktree;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import static org.errware.tasktree.LooperNode.ExecutionType.INSISTENT;
import static org.errware.tasktree.LooperNode.ExecutionType.NORMAL;
import static org.errware.tasktree.LooperNode.ExecutionType.STATEFUL;

public class LooperNode extends AbstractNode{

    //<editor-fold desc="Validation">
    @Override
    public boolean isValid(TaskTree t) {
        setExecutionType(executionType);
        return true;
    }

    @Override
    public boolean isInvalid(TaskTree t) { return false; }
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

    //For insisten execution
    private Node nextNode;
    private Node executingNode;
    public LooperNode(){
        nodes = new ArrayList();
        executionType = NORMAL;
    }
    public LooperNode(ExecutionType type){
        nodes = new ArrayList<>();
        executionType = type;
    }

    @Override
    public int execute( TaskTree t) {
        assert iterator != null;
        if (executionType == INSISTENT) {
            //<editor-fold desc="Insistent Execution">
            assert nextNode != null;
            if(validate(nextNode, t)){
                // set up node for execution
                executingNode = nextNode;
                // set up nextnode to check
                if(!iterator.hasNext()) iterator = nodes.iterator();
                nextNode = iterator.next();

                return executingNode.execute(t);
            }
            //</editor-fold>
        } else {
            //<editor-fold desc="Normal & Stateful Execution">
            if(executionType==NORMAL || !iterator.hasNext()) iterator = nodes.iterator();
            Node n;
            while(iterator.hasNext()) {
                n = iterator.next();
                if (validate(n,t))
                    return n.execute(t);
            }

            c.log("Looper didn't validate any children");
            //</editor-fold>
        }

        c.log("Node " + this.toString() + " executed no children");
        return 200;
    }

    public void add(Node n){nodes.add(n);}

    private boolean validate(Node n, TaskTree t){
        if(n.isValid(t)) {
            t.traceNode(n);
            return true;
        }
        return false;
    }
    //performs necessary setup for new executiontype
    //initially thought might need different transition
    //functionality for switching between execution types
    //in case it is wanted to be done during run time
    //but I think this is enough
    public void setExecutionType(ExecutionType newMode){
        //setup iterator if necessary, keep old otherwise
        iterator = (iterator==null || !iterator.hasNext() ? nodes.iterator() : iterator);
        if(newMode==INSISTENT)
            nextNode = iterator.next();

        executionType = newMode;
    }
}

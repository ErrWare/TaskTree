import org.dreambot.api.methods.MethodContext;

public abstract class Node {
    protected String name;

    public abstract int execute(MethodContext c, TaskTree t);
    public abstract boolean validate(MethodContext c, TaskTree t);
    public abstract boolean invalidate(MethodContext c, TaskTree t);

    //Wanted some way for each Node to easily tell of what type it was
    //but changing the String type within each class/class instance is hard?
    @Override
    public String toString(){
        return name + super.toString();
    }

}

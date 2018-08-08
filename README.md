# TaskTree
Architecture for decision making. Implemented in Java

Decision Trees manage the execution of high level tasks. Each task is recursively broken down into subtasks handled by subtrees. These trees are explored in a greedy, optionally stateful, Depth First manner. To enter a subtree the root node of the tree must be validated according to some defined precondition. To exit a subtree the root node must be re-reached (perhaps after exploration of subtrees) and the defined invalidation/post-condition must be satisfied.

Everything, including the High-Level managing Decision Trees, are subclasses of the Node type. This allows everything to be considered a task through polymorphism and enables it to be reused in another task. In this particular domain/implementation everything subclasses AbstractNode which offers a convenient location to store an oft used static variable.

The classes that are to be used or extended are:

1. TaskTree
2. CombatTree
3. LooperNode

And their intended responsibilities:

1. Task Tree
Starting point of a task tree. Provides a starting point for task exploration and manage the             exploration of the tree/subtrees by maintaining the trace of current execution
2. Combat Tree
Tree specifically used for combat. Enforces custom rules on the root node and execution flow meant to be helpful for the generic combat task.
3. Looper Node
Inner node in a Task Tree. Provides a way to root other trees together to turn them into             subtrees dependent on the same pre and post condition. Should not be used for actuation.

More on tree traversal – the default:

1. Tree instantiated – the root node of the tree is pushed onto the trace stack.

____ 1.1. Top most tree is initialized with public void init(). This recursively initializes the subtrees.

2. Tree execution step:

____ 2.1. If the node on the top of the stack meets its invalidate (post) condition pop it off the stack, repeat until a node is not popped. If stack is empty the task is completely done and the root node has been popped for some reason.

____ 2.2. Execute the node on the top of the stack

3. Logic Node execution step:

____ 3.1. Iterate through subtree roots checking for node validity. If node is valid add it onto the             calling tree’s trace stack then execute it. If no subtree nodes are valid do nothing.

4. ? extends AbstractNode execution step:

____ 4.1. Whatever you make it. This is where actuation is supposed to go.

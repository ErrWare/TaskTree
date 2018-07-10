# TaskTree
Architecture for decision making. Implemented in Java

Decision Trees manage the execution of high level tasks. Each task is recursively broken down into subtasks handled by subtrees. These trees are tentatively explored in a tentatively greedy, tentatively stateful Depth First manner (the former is inherent in the idea of the Decision Tree, the latter are implementation decisions). To enter a subtree the root node of the tree must be validated according to some defined precondition. To exit a subtree the root node must be re-reached (after tentative exploration of subtrees) and the defined postcondition must be satisfied.

Everything, including the High-Level managing Decision Trees, are subclasses of the Node type. This allows everything to be considered a task through polymorphism and enables it to be reused in another task.

The more specific abstract classes that are to be extended are:

1. Task Tree
2. Logic Node
3. Execution Node (May be renamed to Action Node)

And their intended responsibilities:

1. Task Tree
Starting point of a task tree. Provides a starting point for task exploration and manage the             exploration of the tree/subtrees by maintaining the trace of current execution
2. Logic Node
Inner node in a Task Tree. Provides a way to root other trees together to turn them into             subtrees dependent on the same pre and post condition. Should not be used for actuation.
3. Execution Node
Leaf node in a Task Tree. Responsible for actuation of the decision making agent.

More on tree traversal – the default:

1. Tree instantiated – the root node of the tree is pushed onto the trace stack.
2. Tree execution step:
____ 2.1. If the node on the top of the stack meets its invalidate (post) condition pop it off the stack, repeat until a node is not popped. If stack is empty the task is completely done.
____ 2.2. Execute the node on the top of the stack
3. Logic Node execution step:
____ 3.1. Iterate through subtree roots checking for node validity. If node is valid add it onto the             calling tree’s trace stack then execute it. If no subtree nodes are valid do nothing.
4. Execute Node execution step: (Why I think it might be better called Action Node)
____ 4.1. Execute some action
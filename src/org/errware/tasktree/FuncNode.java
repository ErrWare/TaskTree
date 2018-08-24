package org.errware.tasktree;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import org.dreambot.api.methods.MethodContext;

// Node that can be described almost functionally
// on functions relying only on game state (method context)
public class FuncNode extends AbstractNode {
	private Predicate<MethodContext> validityPred = mc -> true
			, invalidityPred = mc -> true;
	private ToIntFunction<MethodContext> exe;
	
	
	@Override
	public final int execute(TaskTree t) {
		return exe.applyAsInt(c);
	}

	@Override
	public final boolean isValid(TaskTree t) {
		if(validityPred.test(c)) {
			onValid();
			return true;
		}
		return false;
	}

	@Override
	public final boolean isInvalid(TaskTree t) {
		if(invalidityPred.test(c)) {
			onInvalid();
			return true;
		}
		return false;
	}
	
	// Hooks for running code when valid / invalid
	// Methods to be overwritten instead of Consumer<MC>
	// b/c Consumer route may not allow accessing 
	// internal variables of subclasses - check this
	protected void onInvalid() {}
	protected void onValid() {}

}

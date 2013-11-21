package uk.co.q3c.util;

import java.util.Collection;
import java.util.Stack;

public class DynamicDAG<V> extends BasicForest<V> {

	public DynamicDAG() {
		super();
	}

	/**
	 * Checks the proposed connection between parent and child nodes, and returns true if a cycle would be created by
	 * adding the child to the parent, or false if not
	 * 
	 * @param parentNode
	 * @param childNode
	 * @return
	 */
	protected boolean detectCycle(V parentNode, V childNode) {
		if (parentNode == childNode) {
			return true;
		}
		Stack<V> stack = new Stack<>();
		stack.push(parentNode);
		while (!stack.isEmpty()) {
			V node = stack.pop();
			Collection<V> predecessors = getGraph().getPredecessors(node);
			if (predecessors != null) {
				for (V pred : predecessors) {
					if (pred == childNode) {
						return true;
					}
				}
				stack.addAll(predecessors);
			}
		}
		return false;

	}

	@Override
	public void addChild(V parentNode, V childNode) {
		if (!detectCycle(parentNode, childNode)) {
			super.addChild(parentNode, childNode);
		} else {
			throw new CycleDetectedException();
		}
	}

}

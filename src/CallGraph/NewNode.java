package CallGraph;

import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;

import fj.data.HashSet;
import polyglot.ast.New;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Expr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class NewNode {
	private SootMethod sMethod;
	private ArrayList<NewNode> children;
	private ArrayList<NewNode> parents;
	private boolean isVisted = false;
	private BriefUnitGraph CFG;

	public NewNode(SootMethod sm) {
		this.sMethod = sm;

		if (sm.hasActiveBody())
			this.CFG = new BriefUnitGraph(sm.retrieveActiveBody());
		children = new ArrayList<>();
		parents = new ArrayList<>();
	}

	public boolean addChild(NewNode child) {
		if (children.contains(child))
			return false;
		children.add(child);
		return true;

	}

	public BriefUnitGraph getCFG() {
		return CFG;
	}

	public boolean getIsVisited() {
		return isVisted;
	}

	public void visit() {
		isVisted = true;
	}

	public boolean addParent(NewNode parent) {
		if (parents.contains(parent))
			return false;
		parents.add(parent);
		return true;

	}

	public ArrayList<NewNode> getChildren() {
		return children;
	}

	public ArrayList<NewNode> getParents() {
		return parents;
	}

	public SootMethod getSootMethod() {
		return sMethod;

	}

	public boolean dotCFG() {
		if (CFG == null) {
			return false;
		}
		String result = "digraph G {\n";
		result += "\n}";
		return true;
	}

	public ArrayList<SootMethod> extractWorkflowsFromMainMethod() {
		ArrayList<SootMethod> workflowsMethods = new ArrayList<SootMethod>();
		BriefUnitGraph cfg = CFG;

		Iterator<Unit> units = cfg.iterator();
		while (units.hasNext()) {
			Unit u = units.next();
			// System.out.println("unit ++= " + u);
			Stmt stmt = (Stmt) u;
			if (stmt.containsInvokeExpr()) {
				System.out.println("method " + stmt.getInvokeExpr().getMethod().getName());
				workflowsMethods.add(stmt.getInvokeExpr().getMethod());
			}

		}
		return workflowsMethods;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		NewNode other = (NewNode) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		}
		// else if (!children.equals(other.children)) {
		// return false;
		// }
		if (parents == null) {
			if (other.parents != null) {
				return false;
			}
			// else if (!parents.equals(other.parents))
			// return false;
		}

		if (!sMethod.equals(other.sMethod)) {
			return false;
		}
		return true;
	}

}

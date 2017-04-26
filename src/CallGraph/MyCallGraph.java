package CallGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class MyCallGraph {
	private NewNode entry;
	private int size = 1;;
	private CallGraph callGraph;
	private ArrayList<NewNode> callGraphNodes;
	private ArrayList<NewNode> exits;

	public MyCallGraph(CallGraph cg) {
		callGraph = cg;
		callGraphNodes = new ArrayList<>();
		exits = new ArrayList<>();
	}

	public void setEntry(SootMethod entryMethod) {
		this.entry = new NewNode(entryMethod);
		callGraphNodes.add(getEntry());
	}

	public NewNode getEntry() {
		return entry;
	}

	public ArrayList<NewNode> getNodes() {
		return callGraphNodes;
	}

	public NewNode getNode(SootMethod sm) {
		for (NewNode n : callGraphNodes) {
			if (n.getSootMethod().equals(sm)) {
				return n;
			}
		}
		return null;
	}

	public void createMyCG() {
		Iterator<Edge> it = callGraph.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			SootMethod src = (SootMethod) edge.getSrc();
			SootMethod tgt = (SootMethod) edge.getTgt();
			NewNode nSrc = new NewNode(src);
			NewNode ntgt = new NewNode(tgt);
			if(nSrc.getSootMethod().getName().equals("onCreate"))
				System.out.println(nSrc.getSootMethod().retrieveActiveBody());
			if (!src.getName().equals("<init>") && !src.getName().equals("<clinit>")) {
				if (!callGraphNodes.contains(nSrc)) {
					callGraphNodes.add(nSrc);

				} else {
					nSrc = getNode(src);
				}
				if (!tgt.getName().equals("<init>") && !tgt.getName().equals("<clinit>")) {
					if (!callGraphNodes.contains(ntgt)) {
						callGraphNodes.add(ntgt);
					} else {
						ntgt = getNode(tgt);
					}
					nSrc.addChild(ntgt);
					ntgt.addParent(nSrc);

					//System.out.println(nSrc.getSootMethod().getName() + " - > " + ntgt.getSootMethod().getName());

				}

			}

		}
		
		//createDotFile();
		
		
	}
	public void unvisitNodes(){
		
	}

	public String printCG(NewNode node) {
		String result = "";
		System.out.println("----------------------------");
		System.out.println(node.getSootMethod().getName());
		
		for (NewNode n : node.getChildren()) {
			result += node.getSootMethod().getName() + " -> " + n.getSootMethod().getName() + ";\n";
			//System.out.println(result);
			System.out.println("-> " + n.getSootMethod());
			if (!n.getIsVisited()) {
				n.visit();
				result += printCG(n);
			}
		}
		return result;

	}
	
	public void findExits(){
		for(NewNode n: callGraphNodes){
			if(n.getChildren().size() == 0){
				exits.add(n);
				System.out.println(n.getSootMethod());
			}
		}
	}
	public void findPaths(){
		
	}
	public void createDotFile(){
		String result = "digraph G {\n";
		result += printCG(entry);
		result += "\n}";
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("display.dot"))) {

			

			bw.write(result);

			// no need to close it.
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		}
		
	}

}

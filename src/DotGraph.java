import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;
import soot.util.dot.DotGraphUtility;
import soot.util.dot.Renderable;

public class DotGraph implements Renderable {
	public final static String DOT_EXTENSION = ".dot";
	private HashMap<String, DotGraphNode> nodes = new HashMap<>();
	private boolean isSubGraph;
	private List<Renderable> drawElementsDi;
	private List<Renderable> drawElements;
	private String graphname;
	BufferedOutputStream out;
	private String entryMain;

	public DotGraph(String graphname) {
		this.drawElements = new LinkedList<Renderable>();
		this.drawElementsDi = new LinkedList<Renderable>();
		this.graphname = graphname;
	}

	public DotGraph createSubGraph(String label) {
		DotGraph subgraph = new DotGraph(label);
		subgraph.isSubGraph = true;
		this.drawElements.add(subgraph);
		System.out.println(subgraph);
		return subgraph;
	}

	@Override
	public void render(OutputStream out, int indent) throws IOException {
		String graphname = this.graphname;
		if (!isSubGraph) {
			DotGraphUtility.renderLine(out, "digraph \"" + graphname + "\" {", indent);
		} else {
			DotGraphUtility.renderLine(out, "subgraph \"" + graphname + "\" {", indent);

		}
	}

	public void setIsSubGraph(Boolean b) {
		isSubGraph = b;
	}

	public void plot(String filename) {
		try {
			out = new BufferedOutputStream(new FileOutputStream(filename));
			out.write("digraph {\n".getBytes());
//			for (Renderable r : drawElements) {
//				r.render(out, 0);
//				out.write(";".getBytes());
//			}
			for (Renderable r : drawElementsDi) {
				r.render(out, 0);
			
			}
			out.write("\n}".getBytes());
			out.close();
		} catch (IOException ioe) {
		}
	}

	public DotGraphNode getNode(String name) {
		// System.out.println(nodes.containsKey(name));
		if (nodes.size() == 0) {
			DotGraphNode node = new DotGraphNode(name);
			nodes.put(name, node);
		} else {
			if (!nodes.containsKey(name)) {
				DotGraphNode node = new DotGraphNode(name);
				nodes.put(name, node);
			}
		}
		DotGraphNode n = nodes.get(name);
		return n;
	}
	
	
	
	

	public DotGraphNode drawNode(String name) {

		DotGraphNode node = getNode(name);
		if (node == null)
			throw new RuntimeException("Assertion failed.");
		if (!this.drawElements.contains(node))
			this.drawElements.add(node);
		return node;
	}

	public DotGraphEdge drawEdge(String from, String to) {
		DotGraphNode src = drawNode(from);
		DotGraphNode dst = drawNode(to);
		DotGraphEdge edge = new DotGraphEdge(src, dst);
		this.drawElementsDi.add(edge);
//		if (!this.drawElementsDi.contains(src)) {
//			this.drawElementsDi.add(src);
//			this.drawElements.remove(src);
//		}
//		if (!this.drawElementsDi.contains(dst)) {
//			this.drawElementsDi.add(dst);
//			this.drawElementsDi.remove(dst);
//		}
		return edge;

	}

	public void setEntryPoint(String name) {
		entryMain = name;

	}

}

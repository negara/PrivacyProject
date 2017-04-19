
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

public class Setup {
	private String apkPath;
	private String androidJAR;
	private String classesFile;
	private String SourceSinkPath;

	private SetupApplication app;
	private CallGraph callGraph;
	private SootMethod entryPoint;

	private DotGraph dg = new DotGraph("callgraph");
	private HashSet<String> visited = new HashSet<>();

	private ArrayList<SootMethod> entryPoints;
	private ArrayList<SootMethod> allMethods;
	private Set<SootClass> classes = new HashSet<SootClass>();

	public Setup(String apkPath, String androidJAR, String classesFile) throws IOException, XmlPullParserException {
		this.androidJAR = androidJAR;
		this.apkPath = apkPath;
		this.classesFile = classesFile;
		SourceSinkPath = "SourcesAndSinks.txt";
		config(androidJAR, apkPath);
	}

	private void config(String androidPath, String apkPath) throws IOException, XmlPullParserException {

		Options.v().no_bodies_for_excluded();
		System.out.println(androidPath);
		app = new SetupApplication("/home/negar/workspaceneon/android/android--1/android.jar", apkPath);

		app.calculateSourcesSinksEntrypoints("SourcesAndSinks.txt");
		soot.G.reset();

		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_android_jars(androidPath); // parent folder of
													// android--1
		Options.v().set_whole_program(true);
		Options.v().set_verbose(false);
		Options.v().set_keep_line_number(true);
		Options.v().set_keep_offset(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_output_format(Options.output_format_class);
		Options.v().setPhaseOption("cg.spark", "on");

		List<String> stringlist = new LinkedList<String>();
		stringlist.add(apkPath);
		Options.v().set_process_dir(stringlist);
		Scene.v().loadNecessaryClasses();

		entryPoint = app.getEntryPointCreator().createDummyMain();
		Options.v().set_main_class(entryPoint.getSignature());
		Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
		System.out.println("dummyMain = " + entryPoint.retrieveActiveBody().toString());
		PackManager.v().runPacks();
		callGraph = Scene.v().getCallGraph();

	}

	private void visit(CallGraph cg, SootMethod m) {
		String identifier = m.getName();
		visited.add(m.getSignature());
		System.out.println(identifier);
		
		dg.drawNode(identifier);
		
		Iterator<MethodOrMethodContext> itChilds = new Targets(callGraph.edgesOutOf(m));

		if (itChilds != null) {
			while (itChilds.hasNext()) {
				SootMethod sm = (SootMethod) itChilds.next();
				if (sm != null) {
					dg.drawEdge(identifier, sm.getName());
					if (!visited.contains(sm.getSignature())) {
						visit(cg, sm);
					}
				}
			}
		}

		Iterator<MethodOrMethodContext> itParents = new Targets(callGraph.edgesInto(m));

		if (itParents != null) {
			while(itParents.hasNext()){
				SootMethod sm = (SootMethod) itParents.next();
				if(sm != null){
					//System.out.println("sm is null");
					if(!visited.contains(sm.getSignature())){
						visit(cg,sm);
					}
				}
			}
		}
	}

	public void runAnalysis() {
		System.out.println("CallGraph is constructed and the size is = " + callGraph.size());
		String label = callGraph.listener().toString();

		System.out.println(label);
		visit(callGraph, entryPoint);
		dg.setEntryPoint(entryPoint.getName());
		dg.plot("plot"+dg.DOT_EXTENSION);
	}

	public void setInputs() {
		ArrayList<File> files = new ArrayList<>();
		entryPoints = new ArrayList<>();
		allMethods = new ArrayList<>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(classesFile)));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				if (line.length() == 0)
					continue;
				System.out.println(line);
				SootClass sc = Scene.v().loadClassAndSupport(line);
				// System.out.println("hello");
				// if(sc.getName().startsWith("android))
				allMethods.addAll(sc.getMethods());
				sc.setApplicationClass();
				classes.add(sc);
				for (SootMethod sm : sc.getMethods()) {
					if (sm.isConcrete()) {
						entryPoints.add(sm);
					}
				}

			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("No line can be read");
		}
		Scene.v().loadNecessaryClasses();
		Scene.v().setEntryPoints(entryPoints);
		CHATransformer.v().transform();
		callGraph = Scene.v().getCallGraph();
		System.out.println("call " + callGraph.size());

	}

	public ArrayList<SootMethod> getAllMethods() {
		return allMethods;
	}

	public ArrayList<SootMethod> getEntryPoints() {
		return entryPoints;
	}

	public CallGraph getCG() {
		return callGraph;
	}

}
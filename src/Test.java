import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;



public class Test {
	public static void main(String[] args) throws IOException, XmlPullParserException {

		if (args.length != 3) {
			System.err
					.println("Expected 3 arguments: (1) apkPath (2) apkClassFile "
							+ "(3) path of parent of android--1 folder which contains androidjar");
			System.exit(-1);
		}

		runAnalysis(args[0], args[1], args[2]);

	}

	private static void runAnalysis(String apkPath, String apkClassFile,
			String androidJAR) throws IOException, XmlPullParserException {
		System.out
				.println("starting analysis of android application in this path: "
						+ apkPath);
		long start = System.nanoTime();
		System.out.println(androidJAR);
		Setup setup = new Setup(apkPath, androidJAR, apkClassFile);
		
		setup.runAnalysis();;
		long end = System.nanoTime();
		System.out.println(end - start);
		
	}

}
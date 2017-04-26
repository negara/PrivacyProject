import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class EnergyCalculater {
	public static void main(String[] args) {
		// The name of the file to open.
		String logFile = "log.txt";
		String energyFile = "energy.txt";
		// This will reference one line at a time
		String line = null;
		String result = "";
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(logFile);
			
			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String unixTime = findInFile(energyFile, "unixtime").replaceAll(" ", "").split(":")[1];
			long unixtime = Long.parseLong(unixTime);
			String preLine = "";
			while ((line = bufferedReader.readLine()) != null) {
				long timeMili = getTime(line, unixtime);
				long preTimeMili = getTime(preLine, unixtime);

				if (line.contains("add")) {
					result += "add: time = " + timeMili + " values = ";
					result += findInFile(energyFile, "#" + timeMili);
					result += " pre time = " + preTimeMili + " values = ";
					result += findInFile(energyFile, "#" + preTimeMili);
				}

				preLine = line;
				
			}
			
			// Always close files.
			bufferedReader.close();
			
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + logFile + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + logFile + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
		System.out.println(result);
	}

	private static long getTime(String line, long unixtime) {
		if (line == "") {
			return 0;
		}
		line = line.replaceAll("<", "");
		line = line.replaceAll(" ", "");
		line = line.replaceAll(">", " ");
		String elements[] = line.split(" ");
		String timedone = elements[1].split(":")[1];
		return Long.parseLong(timedone) - unixtime;
	}

	private static String findInFile(String filename, String str) {
		FileReader fileReader2;
		try {
			fileReader2 = new FileReader(filename);
			BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
			String line = "";
			String target = str;
			if (str.startsWith("#")) {
				target = str.substring(1);
			}
			
				while ((line = bufferedReader2.readLine()) != line) {
					if (line.contains(target)) {
						if (!str.startsWith("#"))
							return line;
						else if(line.startsWith("#")){
							return line.split(" ")[3] + " "+line.split(" ")[5];
						}
					}

				}
			
			bufferedReader2.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return "";
	}
}

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

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(logFile);
			FileReader fileReader2 = new FileReader(energyFile);
			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
			String unixTime = findInFile(bufferedReader2);
			while ((line = bufferedReader.readLine()) != null) {
				line = line.replaceAll("<", "");
				line = line.replaceAll(" ", "");
				line = line.replaceAll(">", " ");
				String elements[] = line.split(" ");
				
			}

			// Always close files.
			bufferedReader.close();
			bufferedReader2.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + logFile + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + logFile + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
	}

	private static String findInFile(BufferedReader bufferedReader2) {
		String line = "";
		try {
			while((line = bufferedReader2.readLine()) != line){
				if(line.contains("unixtime")){
					return line;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}

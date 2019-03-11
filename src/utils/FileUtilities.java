package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtilities {
	private String rootFolder = System.getProperty("user.dir") + "/";

	public void writeToFile(String filename, String content, boolean isAppendToFile) {
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			File file = new File(rootFolder + filename);

			// if file doesnt exists then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile(), isAppendToFile);
			bw = new BufferedWriter(fw);
			bw.newLine();
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger(FileUtilities.class.getName()).log(Level.SEVERE, null, e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				Logger.getLogger(FileUtilities.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public String readFromFile(String filename) throws IOException {
		String content = null;
		File file = new File(rootFolder + filename); // For example, foo.txt
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return content;
	}
}

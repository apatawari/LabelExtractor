package Extractor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Extractor extends StopWords {
	
	
	static final String DDName="CLSA";
	
	static List<String> Elements=new ArrayList<String>();

	BufferedReader br = null;

	static List<String> set = new ArrayList<String>();

	List<String> subset = new ArrayList<String>();

	ArrayList<String> lines = new ArrayList<String>();

	Map<String, Integer> checkMapping = new HashMap<String, Integer>();

	int rowNumber = 0;

	String line = "";

	private static Excel excel;

	public void extractElements(String fileloc) throws Exception		{

		String[] words;

		ArrayList<String> finalLines = new ArrayList<String>();

		try {

			br = new BufferedReader(new FileReader(fileloc));

			while ((line = br.readLine()) != null) {

				if (line.length() > 2) {

					lines.add(line);

					line.replaceAll("[!@$.%^&*(),;}{+='/]", "");

					String newline = removeStopword(line);

					if (newline.length() > 1)

					{
						words = newline.split(" ");

						finalLines.add(words[0]);

						if (words.length > 1)

							finalLines.add(words[0] + " " + words[1]);

						if (words.length > 2)

							finalLines.add(words[0] + " " + words[1] + " "
									+ words[2]);

					}

				}

			}

			Set<String> unique = new HashSet<String>(finalLines);

			int possibleCount = 0;

			int possibleCountKey = 0;

			// taking in consideration the Occurrence of the counts got

			ArrayList<Integer> countOccur = new ArrayList<Integer>();

			for (String key : unique) {

				if (Collections.frequency(finalLines, key) > 100)

					countOccur.add(Collections.frequency(finalLines, key));

			}

			Set<Integer> UniqueCount = new HashSet<Integer>(countOccur);

			for (Integer key : UniqueCount) {

				if (possibleCount < Collections.frequency(countOccur, key)) {

					possibleCount = Collections.frequency(countOccur, key);

					possibleCountKey = key;

				}

			}

			ArrayList<String> elementNames = new ArrayList<String>();

			for (String key : unique) {

				if (Collections.frequency(finalLines, key) <= possibleCountKey + 5

						&& Collections.frequency(finalLines, key) >= possibleCountKey - 5) {

					elementNames.add(key);

				}

			}

			 Elements = getUnique(elementNames);

			Elements.add("Comment");

			Elements.add("Blanks");

			// Content Extraction using the key words
			/*
			 * Separate section for extraction of the content from the PDF and
			 * putting it back into the excel file in the tabulated manner
			 * 
			 * JXL API is used for writing into the Excel
			 */

			getContent(Elements);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public List<String> getUnique(List<String> lst) {
		
		List<String> uniq = new ArrayList<String>(lst);
		
		lst.forEach(elem -> uniq.removeIf(x -> !x.equals(elem)
		
				&& elem.contains(x)));
		
		return uniq;

	}

	public void getContent(List<String> Elements) throws Exception
		
	 {

	// Declared to read line from the text file
	
		String line1 = "";

		/*
		 * These variables below are for row numbers and having consistency rows
		 */

		checkMapping = initializeMapping(Elements);
		
		// List<String> oneVariable = new ArrayList<String>();

		int lineNumber = 0;
		
		String line2 = "";

		while ((lineNumber < lines.size())) {
		
			line1 = lines.get(lineNumber);
			
			if ((lineNumber + 1) < lines.size())
			
				line2 = lines.get(lineNumber + 1);

			if (!line1.contains("Page") && !line1.contains("page")){
				
				parseLine(line1, line2, Elements);
				
				lineNumber++;
			} 
			
			else{
			
				lineNumber++;
				
			}

		}
		
		

	}

	public Map<String, Integer> initializeMapping(List<String> Elements) {

		Map<String, Integer> checkMapping = new HashMap<String, Integer>();
		
		for (int i = 0; i < Elements.size(); i++) {
		
			checkMapping.put(Elements.get(i), 0);
		
		}
		
		return checkMapping;

	}

	public void parseLine(String line1, String line2, List<String> Elements) {
		
		// find a match in line with element name
		
		boolean found = false;
		
		int foundElementLocation = 0;
		
		for (int i = 0; i < Elements.size(); i++) {

		Pattern p = Pattern.compile(Elements.get(i));
		
		Matcher m = p.matcher(line1);

		// if we get the element in the line, we take the position and flag
		// out of it and break from the loop.
		// else the default value we will have the default value as false
		// and we should not use the location now.
			
		if (m.find()) {
		
			found = true;
		
			foundElementLocation = i;

			break;
			
		}

		}

		// now we have whether a Element is there in line or not.
		// target is to retain the order with respect to element blocks in the
		// file.

		if (found) {
		
			if (checkMapping.get(Elements.get(foundElementLocation)) == 0) {
			
				checkMapping.replace(Elements.get(foundElementLocation), 0, 1);
			} 
			
			else {
			
				String finalString = "";
				
				for (String str : subset) {
				
					finalString = str + ";" + finalString;
				
				}
				
				set.add(finalString);
				
				subset.clear();
				
				checkMapping = initializeMapping(Elements);
				
				rowNumber++;
				
				checkMapping.replace(Elements.get(foundElementLocation), 0, 1);
			
			}

			subset.add(line1);
		
		} 
		else {
		
			String replacement = "";
			
			if (subset.size() > 0) {
			
				line1 = line1.replaceAll("\\s+", " ");
				
				replacement = subset.get(subset.size() - 1) + line1;
				
				subset.set(subset.size() - 1, replacement);
			
			}
		
		}

	}

	public static void main(String[] args) throws Exception {

		PDFManager pdfManager = new PDFManager();
		
		pdfManager.setFilePath("DataDictionaries/Original/"+ DDName + ".pdf");
		
		PrintWriter writer = new PrintWriter("DataDictionaries/DDTextFormat/"+DDName+".txt","UTF-8");
		
		writer.println(pdfManager.ToText());
		
		
		writer.close();
		
		Extractor extract = new Extractor();

		extract.extractElements("DataDictionaries/DDTextFormat/"+DDName+".txt");
		
		excel = new Excel();
		
		Excel.writeDDExcell(DDName,set, Elements);
		
	}

}

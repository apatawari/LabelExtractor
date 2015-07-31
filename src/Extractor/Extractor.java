package Extractor;

import java.io.BufferedReader;
import java.io.File;
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

import jxl.Workbook;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

public class Extractor extends StopWords {
	BufferedReader br = null;

	String line = "";
	
	

	public void read(String fileloc) throws IOException, WriteException {

		String[] words;
		
		ArrayList<String> finalLines = new ArrayList<String>();
		// ArrayList<String> finalList2 = new ArrayList<String>();
		// add String data
		try {

			br = new BufferedReader(new FileReader(fileloc));
			while ((line = br.readLine()) != null) {

				// String [] x=line.split(" ");
				if (!line.isEmpty()) {
					// System.out.println(line);
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
			List<String> Elements = getUnique(elementNames);
			Elements.add("Comment");
			Elements.add("Blanks");

			// Content Extraction using the key words
			/*
			 * Separate section for extraction of the content from the PDF and
			 * putting it back into the excel file in the tabulated manner
			 * 
			 * JXL API is used for writing into the Excel
			 */

			getContent(Elements, fileloc);

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

	public static void main(String[] args) throws RowsExceededException,
			WriteException, IOException {
		
		PDFManager pdfManager = new PDFManager();
	       pdfManager.setFilePath("PDF/nacc.pdf");
	       PrintWriter writer = new PrintWriter("OutputTextFile/output.txt", "UTF-8");
	       writer.println(pdfManager.ToText());
	      // System.out.println(pdfManager.ToText()); 
	       writer.close();
		Extractor extract = new Extractor();
		
		extract.read("OutputTextFile/output.txt");

	}

	public List<String> getUnique(List<String> lst) {
		List<String> uniq = new ArrayList<String>(lst);
		lst.forEach(elem -> uniq.removeIf(x -> !x.equals(elem)
				&& elem.contains(x)));
		return uniq;

	}

	public Map<String, Integer> createKeyValueListForElements(
			List<String> Elements, WritableSheet sheet)
			throws RowsExceededException, WriteException {
		/*
		 * Key value pair is to identify where the values should be placed
		 * Corresponding elements in a row
		 */

		Map<String, Integer> map = new HashMap<String, Integer>();

		for (int i = 0; i < Elements.size(); i++) {
			// Add the created Cells to the sheet
			Label label = new Label(i, 0, Elements.get(i));
			sheet.addCell(label);

			map.put(Elements.get(i), i);

		}

		return map;

	}

	public void getContent(List<String> Elements,
			String fileloc) throws IOException, WriteException {
		
		File exlFile = new File("Generated_Excel_File/output_nacc.xls");
		WritableWorkbook writableWorkbook = Workbook.createWorkbook(exlFile);
		WritableSheet sheet = writableWorkbook.createSheet("Sheet1", 0);

		
		Label label;
		
		Map<String, Integer> map = createKeyValueListForElements(Elements,sheet);
		
		// Declared to read line from the text file
		String line1 = "";

		/*
		 * These variables below are for row numbers and having consistency rows
		 */
		Map<String, Integer> checkMapping = new HashMap<String, Integer>();
		for (int i = 0; i < Elements.size(); i++) {
			checkMapping.put(Elements.get(i), 0);
		}
		int rowNumber = 3;

	//	List<String> oneVariable = new ArrayList<String>();

		// BufferedReader
		br = new BufferedReader(new FileReader(fileloc));

		// Main loop for getting line by line data analysis
		int executed=0;
		String str="";
		while ((line1 = br.readLine()) != null) {
		
			if (!line1.contains("Page") && !line1.isEmpty()
					&& !line1.contains("page")) {
					
				Matcher m = null;
				for (int i = 0; i < Elements.size(); i++) {
					Pattern p = Pattern.compile(Elements.get(i));
					m = p.matcher(line1);
					
//					if(m.find() && !str.equals(""))
//					{
//						System.out.println("hello");
//					}
						 
						if (m.find()) {
						str="";
						if (checkMapping.get(Elements.get(i))==0)
						{
							checkMapping.replace(Elements.get(i), 0, 1);
						}
						else{
							
							//call re-initialization method because element is re-encountered
							checkMapping=initializeMapping(Elements);
							rowNumber++;
							checkMapping.replace(Elements.get(i), 0, 1);
								
							}
						str=line1.replace(Elements.get(i), "");
						
						label = new Label(map.get(Elements.get(i)), rowNumber, str.trim());
						sheet.addCell(label);
						
						}
					else if (!str.equals("") && !m.find())
					{
						int pass=0;
						//for multiple lines
						for(int j=0;j<Elements.size();j++)
						{
							if(line1.contains(Elements.get(j)))
							{ pass=1; }
						}
							
						if (executed==0 && pass==0)
						{
						str=str+" "+line1.trim();
						System.out.println(str);
						executed=1;
						}
						
					}
					
					
					
				}
			}
			executed=0;
			
		}
		writableWorkbook.write();
		writableWorkbook.close();
System.out.println("Excel File generated");
	}

public Map<String, Integer> initializeMapping(List<String> Elements)
{
	
	Map<String, Integer> checkMapping = new HashMap<String, Integer>();
	for (int i = 0; i < Elements.size(); i++) {
		checkMapping.put(Elements.get(i), 0);
	}
	return checkMapping;
			
}




}

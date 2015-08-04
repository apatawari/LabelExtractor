package Extractor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class Excel {
	static Map<String, Integer> map = new HashMap<String, Integer>();
	
	public static void writeDDExcell(String DDname, List<String> set, List<String> Elements) throws Exception{
		
		int S=set.size();
		
		WritableWorkbook wworkbook;
	      
		wworkbook = Workbook.createWorkbook(new File("DataDictionaries/Normalized/"+DDname+"-normalized.xls"));
	      
		WritableSheet wsheet = wworkbook.createSheet("First Sheet", 0);  


		//Initialization
		
		for (int i = 0; i < Elements.size(); i++) {
			// Add the created Cells to the sheet
			
			Label label = new Label(i, 0, Elements.get(i));
			
			wsheet.addCell(label);
			
			map.put(Elements.get(i), i);
		}
			
		
		for (int i=0;i<S; ++i){
			
			String s="";
			String str=set.get(i);
	    	  
			String[] separatedColumn = str.split(";");

			for (String word : separatedColumn) {
			
				
				for (int j = 0; j < Elements.size(); j++) {
				
				if (word.contains(Elements.get(j)))
						{
						
				s=word.replace(Elements.get(j), "");
				
				Label label = new Label(map.get(Elements.get(j)), i+2, s.trim());
				
				wsheet.addCell(label);
					}
						}
				
				}
			
		}
	     
		System.out.println("Excel File Generated");
		wworkbook.write();
	    
		wworkbook.close();
	}

}

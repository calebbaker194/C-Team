package GradleDemo;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class ReadWriteDemo {

	
	public static void main(String ars[]) throws Exception{
// Create a new Java.io.Reader This is included in all java installs
Reader reader = Files.newBufferedReader(Paths.get("timepunches.csv"));

// Create a new com.opencsv.CSVReader This is an external library
CSVReader csvReader = new CSVReader(reader);

// Read The CSV file into list of String arrays. Each array represnts a row. 
List<String[]> records = csvReader.readAll();

// Close Our readers to prevent a resource leak
csvReader.close();
reader.close();

// Create empty List of String arrays
List<String[]> newCsv = new ArrayList<String[]>();

// Add headers to the newCsv
String[] headers = {"Name","Time Worked"};
newCsv.add(headers);

// iterate through the records
int x=0;
for(String[] row:records)
{
    if(x==0)// You can use something like this to process headers
    {
        x++;
        // Handle headers here
        
        //
        continue; // This will skip to the next iteration of the for loop without.
    }
    // Get row Name
    String tname = row[0];
    //Get the hour and minutes into there own strings. 
    String[] startTime = row[1].split(":");
    String[] stoptime = row[2].split(":");
    System.out.println(Arrays.toString(stoptime));
    // Get the minutes of the day for both
    int startminutes = Integer.parseInt(startTime[0])*60+Integer.parseInt(startTime[1]);
    int stopminutes = Integer.parseInt(stoptime[0])*60+Integer.parseInt(stoptime[1]);
    
    // Reformate into hh:mm and place into a String[]
    String[] tempRow = {tname,((stopminutes-startminutes)/60)+":"+((stopminutes-startminutes)%60)};
    // Add temp row into the Variable that we will use to write the new CSV
    newCsv.add(tempRow);
}


// Create Java.io.Writer
Writer writer = Files.newBufferedWriter(Paths.get("timetotal.csv"));
// Create com.opencsv.CSVWriter
	CSVWriter csvWriter = new CSVWriter(writer);
			
	csvWriter.writeAll(newCsv);
	csvWriter.flush();
	csvWriter.close();
}
	
}

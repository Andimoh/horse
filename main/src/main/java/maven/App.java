package maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

/**
 * Hello world!
 */

public class App {
    private App() {
    }

        /**The final test score for each student is the highest of the two scores if the student retook the test.
    If the student did not retake the test, the final test score is 
    the score from their first attempt (i.e., from the "Test Scores" workbook). **/

    private static Map<Integer, Student> importTestScores(final Map<Integer, Student> students) {
        try {
            final FileInputStream file = new FileInputStream(new File("Test Scores.xlsx"));
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheetAt(0);
            final Iterator<Row> rowIterator = sheet.iterator();

            boolean firstRow = true;
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                if (firstRow == false) {

                    final Iterator<Cell> cellIterator = row.cellIterator();
                    int cellIndex = 0;
                    int currentID = 0;
                    while (cellIterator.hasNext()) {
                        final Cell cell = cellIterator.next();
                        switch (cellIndex) {
                            case 0:
                            if(cell.getCellType() == CellType.NUMERIC) {
                                currentID = (int) cell.getNumericCellValue();
                            }
                                cellIndex++;
                                break;

                            case 1:
                            if(cell.getCellType() == CellType.NUMERIC) {                    
                                        students.get(currentID).testScore = (int) cell.getNumericCellValue();
                                    
                                }
                                cellIndex++;
                                break;
                            default:
                                break;
                        }
                    }

                } else {
                    firstRow = false;
                    continue;
                }
            }

            file.close();

            return students;
        } catch (final Exception e) {
            e.printStackTrace();
            return students;

        }

    }

    private static Map<Integer, Student> importRetakeTestScores(final Map<Integer, Student> students) {
        try {
            final FileInputStream file = new FileInputStream(new File("Test Retake Scores.xlsx"));
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheetAt(0);
            final Iterator<Row> rowIterator = sheet.iterator();
            boolean firstRow = true;
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                if (firstRow == false) {

                    final Iterator<Cell> cellIterator = row.cellIterator();
                    int cellIndex = 0;
                    int currentID = 0;
                    while (cellIterator.hasNext()) {
                        final Cell cell = cellIterator.next();
                        switch (cellIndex) {
                            case 0:
                            if(cell.getCellType() == CellType.NUMERIC) {
                                currentID = (int) cell.getNumericCellValue();
                            }
                                cellIndex++;
                                break;

                            case 1:
                                if(cell.getCellType() == CellType.NUMERIC) {
                                    // if (students.containsKey(currentID)) {
                                        students.get(currentID).tookRetake = true;
                                        students.get(currentID).retakeScore = (int) cell.getNumericCellValue();
                                    }
                                
                                cellIndex++;
                                break;
                        }
                    }

                } else {
                    firstRow = false;
                    continue;
                }
            }

            file.close();

            return students;
        } catch (final Exception e) {
            e.printStackTrace();
            return students;

        }

    }
     //Find the class average using the "final test score" for each student.
     private static int calculateClassAverage(final Map<Integer, Student> students) {
        final Set<Map.Entry<Integer, Student>> st = students.entrySet();
        double total = 0;
        for (final Map.Entry<Integer, Student> me : st) 
	       { 
	           total += me.getValue().getFinalTestScore();
	       } 			
		return (int)Math.rint(total / students.size());
	}

    public static void main(final String[] args) throws IOException {
        Map<Integer, Student> students = new HashMap<Integer, Student>();
        final ArrayList<String> femaleIDs = new ArrayList<String>();    
                
        try
        {
            //You will need to parse and import the three workbooks 
            //into memory (use appropriate data structures or create classes).
            final FileInputStream file = new FileInputStream(new File("Student Info.xlsx"));
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheetAt(0);
            final Iterator<Row> rowIterator = sheet.iterator();
            
            boolean firstRow = true;
            while (rowIterator.hasNext())
            {
                final Row row = rowIterator.next();
                if(firstRow == false) {
                	
                	final Student newStudent = new Student();
                    final Iterator<Cell> cellIterator = row.cellIterator();
                    int cellIndex = 0;
                    while (cellIterator.hasNext())
                    {
                        final Cell cell = cellIterator.next();
                        switch (cellIndex)
                        {
                            case 0:
                            	if(cell.getCellType() == CellType.NUMERIC) {
                            		newStudent.studentId = (int)cell.getNumericCellValue();
                                    cellIndex++;
                            	}
                                break;
                                
                            case 1:
                            	if(cell.getCellType() == CellType.STRING) {
                            		newStudent.major = cell.getStringCellValue();
                                    cellIndex++;
                            	}else {
                            		newStudent.major = null;
                            	}
                                break;
                                
                            case 2:
                            	if(cell.getCellType() == CellType.STRING) {
                                    newStudent.gender = cell.getStringCellValue();
                                    cellIndex++;
                            	}else {
                            		newStudent.gender = null;
                            	}
                                break;
                        }
                        
                    }
                    
                    //Find the student IDs of computer science majors who are female.
                    if(newStudent.studentId != 0 && newStudent.major != null && newStudent.gender != null) {
                        students.put(newStudent.studentId, newStudent);               
                        if(newStudent.major.equalsIgnoreCase("computer science") 
                        		&& newStudent.gender.equalsIgnoreCase(("f"))) {
                        	femaleIDs.add(Integer.toString(newStudent.studentId));
                        }        
                    }
 		
                }else {
                	firstRow = false;
                	continue;
                }
            }
            
            Collections.sort(femaleIDs);
            
            file.close();
            
            students = importTestScores(students);
            students = importRetakeTestScores(students);

            final JSONObject json = new JSONObject();
            json.put("id", "mabdulk1@mail.depaul.edu");
            json.put("name", "Mohammed Abdul");
            json.put("Average", calculateClassAverage(students));
            json.put("studentIds", femaleIDs.toArray());

            final URL url = new URL ("http://54.90.99.192:5000/challenge"); 
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
    
            final String jsonString = json.toString();
            System.out.println("JSON Request String: ");
            System.out.println(jsonString);
            con.connect();
            try(OutputStream os = con.getOutputStream()){
                final byte[] input = jsonString.getBytes("StandardCharsets.UTF_8");
                os.write(input, 0, input.length);
            }
            System.out.println("");
            System.out.println("Server Response: ");
            try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"StandardCharsets.UTF_8"))) {
                final StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }

        } finally{
            //left on purpose
        }
      
    }
	
}
import java.io.*;

public class ResourceInjection {
	
    private static void test() {

        String fileName = null;
        int    checkInteger  = 0;

        try {
            BufferedReader inStream = new BufferedReader (
                                          new InputStreamReader(System.in)
                                      );
            System.out.print("Please enter a filename: ");
            fileName = inStream.readLine();
           
        }  catch (IOException e) {
            System.out.println("IOException: " + e);
            return;
        }
    
    	File myFile = new File("/var/tmp/" + fileName);
    	
    	if (myFile.delete()) 
    		System.out.println ("deleted file");

    	 
    	
    	
    }

public static void main(String[] args) {
    test();
}
}
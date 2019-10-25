/* This software was developed at the National Institute of Standards and Technology by employees of the Federal Government
in the course of their official duties. Pursuant to title 17 Section 105 of the United States Code this software is
not subject to copyright protection and is in the public domain. NIST assumes no responsibility whatsoever for its use by
other parties, and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.

We would appreciate acknowledgement if the software is used. The SAMATE project website is: http://samate.nist.gov
*/

import java.io.*;
public class PrivateArrayPublicMethod {
     private  int [] foo = new int[1];
     public PrivateArrayPublicMethod () 
	{
		foo = test ();  /* BAD */
	}
     public int []  test ()
     {

        int [] bar = new int [1];
        String inLine = null;
        int    checkInput  = 0;

        try {
            BufferedReader inStream = new BufferedReader (
                                          new InputStreamReader(System.in)
                                      );
            System.out.print("Enter a valid Integer and hit <ENTER>: ");
            inLine = inStream.readLine();
            checkInput    = Integer.parseInt(inLine);

        } catch (NumberFormatException nfe) {
            System.out.println("You did not enter a valid Integer: " + inLine);
            return null;
        }
             catch (IOException e) {
            System.out.println("IOException: " + e);
            return null;
        }
      
         bar [0] = checkInput;
         return bar;
     }
    public static void main (String[] args)
{  
    PrivateArrayPublicMethod TestCase = new PrivateArrayPublicMethod ();
} 
}

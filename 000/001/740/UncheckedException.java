/* This software was developed at the National Institute of Standards and Technology by employees of the Federal Government
in the course of their official duties. Pursuant to title 17 Section 105 of the United States Code this software is 
not subject to copyright protection and is in the public domain. NIST assumes no responsibility whatsoever for its use by 
other parties, and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.

We would appreciate acknowledgement if the software is used. The SAMATE project website is: http://samate.nist.gov

*/

import java.io.*;

class UncheckedException {

  public static FileInputStream getInput(String fileName)
    throws FileNotFoundException
  {
    FileInputStream fis = new FileInputStream(fileName);
    System.out.println("f1: File input stream created");
    return fis;
  }

  
  public static void main(String args[])
  {
    FileInputStream fis1 = null;
    String fileName = "foo.bar";

    try {
      fis1 = getInput(fileName);
    }
    catch (FileNotFoundException ex) {} /* BAD */     
  }
}

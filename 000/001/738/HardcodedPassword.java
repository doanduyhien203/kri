/* This software was developed at the National Institute of Standards and Technology by employees of the Federal Government
in the course of their official duties. Pursuant to title 17 Section 105 of the United States Code
this software is not subject to copyright protection and is in the public domain.
NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.

We would appreciate acknowledgement if the software is used. The SAMATE project website is: http://samate.nist.gov
*/

import java.io.*;
import java.sql.*;

public class HardcodedPassword {
  public static void main(String[] args) {
    try {
      Connection con=null;
      Class.forName("oracle.jdbc.driver.OracleDriver");
con=DriverManager.getConnection("jdbc:oracle:thin:@machine_hosting_database:1521:database_name","scott", "tiger");
      Statement s=con.createStatement();
      s.close();
      con.close();
   } catch(Exception e){e.printStackTrace();}
 }
}


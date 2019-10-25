/*
Description: The credentials for connecting to the database are hard-wired into the sourcecode.
Keywords: Port Java Size0 Complex1 HasCreds

Copyright 2005 Fortify Software.

Permission is hereby granted, without written agreement or royalty fee, to
use, copy, modify, and distribute this software and its documentation for
any purpose, provided that the above copyright notice and the following
three paragraphs appear in all copies of this software.

IN NO EVENT SHALL FORTIFY SOFTWARE BE LIABLE TO ANY PARTY FOR DIRECT,
INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE
USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF FORTIFY SOFTWARE HAS
BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMANGE.

FORTIFY SOFTWARE SPECIFICALLY DISCLAIMS ANY WARRANTIES INCLUDING, BUT NOT
LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, AND NON-INFRINGEMENT.

THE SOFTWARE IS PROVIDED ON AN "AS-IS" BASIS AND FORTIFY SOFTWARE HAS NO
OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
MODIFICATIONS.
*/

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import java.sql.*;

public class Hascreds1_bad extends HttpServlet
{
	private Object _obj;

    public void doGet(HttpServletRequest req, HttpServletResponse res)
    	throws ServletException, IOException
    {
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println("<HTML><HEAD><TITLE>Test</TITLE></HEAD><BODY><blockquote><pre>");

		Connection conn = getConnection();	/* BAD */
		out.println("got connection");

		try {
			PreparedStatement stmt = conn.prepareStatement("select * from mytab");
			ResultSet rs = stmt.executeQuery();
			out.println("results:");
			while(rs.next())
				out.println(rs.getString(1) + ", " + rs.getString(2));
		} catch(Exception e) {
			throw new ServletException(e);
		} finally {
			close(conn);
		}

        out.println("</pre></blockquote></BODY></HTML>");
        out.close();
    }

	public void close(Connection conn)
		throws ServletException
	{
		try {
			conn.close();
		} catch(Exception e) {
			throw new ServletException(e);
		}
	}

    public Connection getConnection()
		throws ServletException
    {
		try {
			if(_obj == null)
				_obj = Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			String uname = getInitParameter("data-url");
			return  DriverManager.getConnection(uname, "root", "foobar"); /* BAD */
		} catch(Exception e) {
			throw new ServletException(e);
		} 
	}
}


/*
Description: Tainted input allows command execution.
Keywords: Port Java Size0 Complex0 Taint Unsafe
InvalidParam: "user=bogus;ls -l /"
ValidParam: user=root

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
import java.lang.Runtime;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Proc1_bad extends HttpServlet
{
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    	throws ServletException, IOException
    {
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println("<HTML><HEAD><TITLE>Test</TITLE></HEAD><BODY><blockquote><pre>");

		String user = req.getParameter("user");
		if(user != null) {
			try {
				String[] args = { "/bin/sh", "-c", "finger " + user };
				Process p = Runtime.getRuntime().exec(args);
				BufferedReader fingdata = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while((line = fingdata.readLine()) != null)
					out.println(line);
				p.waitFor();
			} catch(Exception e) {
				throw new ServletException(e);
			}
		} else {
			out.println("specify a user");
		}

        out.println("</pre></blockquote></BODY></HTML>");
        out.close();
    }
}


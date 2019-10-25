/*
Description: Two file operations are performed on a filename, allowing a filename race condition to occur.
Keywords: Port Java Size0 Complex0 Race FileRace
InvalidParam: name=test

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

public class File2_bad extends HttpServlet
{
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    	throws ServletException, IOException
    {
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println("<HTML><HEAD><TITLE>Test</TITLE></HEAD><BODY><blockquote><pre>");

		String name = req.getParameter("name");
		String msg = req.getParameter("msg");
		if(name != null) {
			try {
				File f = new File("/tmp", name.replaceAll("[^a-z]", "_"));
				if(msg != null) {
					FileWriter fw = new FileWriter(f);	/* BAD */
					fw.write(msg, 0, msg.length());
					fw.close();
					f.setReadOnly();					/* BAD */
					out.println("message stored");
				} else {
					String line;
					BufferedReader fr = new BufferedReader(new FileReader(f));
					while((line = fr.readLine()) != null)
						out.println(line);
				}
			} catch(Exception e) {
				throw new ServletException(e);
			}
		} else {
			out.println("specify a name and an optional msg");
		}

        out.println("</pre></blockquote></BODY></HTML>");
        out.close();
    }
}


/*
Description: Tainted output allows log entries to be forged.
Keywords: Port Java Size0 Complex0 Taint LogForge
ValidParam: "cmd=forge%0aThis is a forged line"
ValidParam: cmd=ok

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
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;

public class Logforge1_ok extends HttpServlet
{
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    	throws ServletException, IOException
    {
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
		Logger log = Logger.getLogger("Logforge");
        out.println("<HTML><HEAD><TITLE>Test</TITLE></HEAD><BODY><blockquote><pre>");

		String cmd = req.getParameter("cmd");
		if(cmd != null) {
			if(cmd == "valid")
				out.println("good");
			else
				log.info("invalid command: " + cmd.replaceAll("\n", "_"));		/* OK */
		} else {
			out.println("specify a command");
		}

        out.println("</pre></blockquote></BODY></HTML>");
        out.close();
    }
}


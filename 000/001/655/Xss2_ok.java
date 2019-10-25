/*
Description: Tainted output allows cross-site scripting attack.
XXX verify that we can inject the appropriate cookie from an URL to perform the xss.
Keywords: Port Java Size0 Complex0 WebServer XSS
ValidCookie: cmd=<script language=javascript>alert("gotcha")</script>
ValidCookie: cmd=gotcha

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

public class Xss2_ok extends HttpServlet
{
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    	throws ServletException, IOException
    {
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.println("<HTML><HEAD><TITLE>Test</TITLE></HEAD><BODY><blockquote><pre>");

		String cmd = null;
		Cookie[] cookie = req.getCookies();
		if(cookie != null) {
			for(int i = 0; i < cookie.length; i++) {
				if(cookie[i].getName() == "cmd")
					cmd = cookie[i].getValue();
			}
		}

		if(cmd != null) {
			if(cmd == "valid")
				out.println("good");
			else
				out.println("invalid command: " + cmd);		/* OK */
		} else {
			out.println("specify a command");
		}

        out.println("</pre></blockquote></BODY></HTML>");
        out.close();
    }
}


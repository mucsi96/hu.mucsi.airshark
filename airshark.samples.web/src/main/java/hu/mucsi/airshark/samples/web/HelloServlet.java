package hu.mucsi.airshark.samples.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class HelloServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.append("Welcome to airshark!\n");
		writer.append("Hello from simple (web.xml) servlet of specification Servlet 2.5!");
	}
}

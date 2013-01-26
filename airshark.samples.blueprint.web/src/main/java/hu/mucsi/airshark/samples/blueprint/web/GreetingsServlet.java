package hu.mucsi.airshark.samples.blueprint.web;

import hu.mucsi.airshark.samples.blueprint.multiple.interfacce.Greeting;
import hu.mucsi.airshark.samples.blueprint.multiple.interfacce.Greetings;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class GreetingsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();
		writer.append("Welcome to airshark!\n");

		List<Greeting> greetings = getGreetings();
		if (greetings != null) {
			writer.append("The avaible greetings services are:\n");
			for (Greeting greeting : greetings) {
				writer.append(greeting.getGreeting()+"\n");
			}
		} else {
			writer.append("There is no avaible greeting.");
		}

	}

	private List<Greeting> getGreetings() {
		try {
			InitialContext ctx = new InitialContext();
			String jndiName = "osgi:service/" + Greetings.class.getName();
			Greetings greetings = (Greetings) ctx.lookup(jndiName);
			return greetings.getGreetings();
		} catch (NamingException e) {
			return null;
		}
	}

}

package controller;

/**
 * Servlet implementation class Login
 * @author Will Henry
 * @author Vincent Lee
 * @version 2.0
 * @since April 23, 2014
 */

import helper.SSLConnection;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.User;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/** @see HttpServlet#HttpServlet() */
	public Login() {
		super();
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("myid") != null && request.getParameter("password") != null) {
			String username = request.getParameter("myid");
			String password = request.getParameter("password");
			SSLConnection connection = new SSLConnection(username, password);
			connection.setConn(connection.connect());
			
			System.out.println((connection.getConn().isBound()) ? "Authenticated to the server ( ssl )\n" : "Not authenticated to the server");
			User you = connection.getUser();
			request.setAttribute("User", you);
			
			request.getRequestDispatcher("/user.jsp").forward(request, response);
		}
	}
}

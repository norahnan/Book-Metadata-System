package edu.mtholyoke.cs341bd.bookz;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HTMLView {

	private String metaURL;

	public HTMLView(String baseURL) {
		this.metaURL = "<base href=\"" + baseURL + "\">";
	}

	/**
	 * HTML top boilerplate; put in a function so that I can use it for all the
	 * pages I come up with.
	 * 
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 * @param title
	 *            the title of the page, since that goes in the header.
	 */
	void printPageStart(PrintWriter html, String title) {
		html.println("<!DOCTYPE html>"); // HTML5
		html.println("<html>");
		html.println("  <head>");
		html.println("    <title>" + title + "</title>");
		html.println("    " + metaURL);
		html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"" + getStaticURL("bookz.css") + "\">");
		html.println("  </head>");
		html.println("  <body>");
		html.println("  <a href='/front'><h1 class=\"logo\">"+title+"</h1></a>");
	}

	public String getStaticURL(String resource) {
		return "static/" + resource;
	}

	/**
	 * HTML bottom boilerplate; close all the tags we open in
	 * printPageStart.
	 *
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 */
	void printPageEnd(PrintWriter html) {
		html.println("  </body>");
		html.println("</html>");
	}

	void showFrontPage(Model model, HttpServletResponse resp, Boolean random) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");

			//create a row
			html.println("<table>");
			//create a table row
			html.println("<tr>");
			
			//add the words input space and two buttons
			html.println("<h3>Enter key word to search</h3>");
			html.println("<form action=\"searchT\" method=\"POST\" align = \"center\">");
			html.println("      <input type=\"text\" name=\"user\" />");
			//html.println("     <label>Comment: <input type=\"text\" name=\"comment\" /></label>");
			html.println("     <input type=\"submit\" value=\"Search by title\" />");

			html.println("  </form>");
			
			html.println("<form action=\"searchC\" method=\"POST\" align = \"center\"/>");
			html.println("      <input type=\"text\" name=\"user\" />");
			html.println("     <input type=\"submit\" value=\"Search by content\" style = \"padding-left: 20px\"/>");

			html.println("  </form>");

			//close a table row
			html.println("</tr>");
			
			//create a table row
			html.println("<tr>");


			html.println("<h3>Browse books by title</h3>");
			for(char letter = 'A'; letter <= 'Z'; letter++) {
				html.println("<a style = \"margin-left: 10pt;\" href='/title/"+letter+"'>"+letter+"</a> ");
			}

			//close a table row
			html.println("</tr>");
			
			//close the table
			html.println("</table>");
			
			if(random){
				//print random books page
				randomBooks(html, model);
			}
			else{
				html.println("<h3 align = \"center\" style = \"font-size: 30px\">Thank you for your feedback!</h3>");
				html.println("<div class = \"form\" align = \"center\">");
				html.println("<form action = \"/front\" method = \"GET\">");
				html.println("<input type = \"submit\" value = \"back to home\">");
				html.println("</form>");
				html.println("</div>");
			}
			
			printPageEnd(html);
		}
	}
	
	/**
	 * Prints out html that will post random books
	 */
	private void randomBooks(PrintWriter html, Model model){
		// get 5 random books:
					html.println("<h3>Check out these random books</h3>");
					List<GutenbergBook> randomBooks = model.getRandomBooks(5);
					for (GutenbergBook randomBook : randomBooks) {
						printBookHTML(html, randomBook);
					}
	}
	
	/**
	 * Prints the comment page
	 */
	public void displayBookComment(GutenbergBook book, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			book.writePageIntro(html);
			book.writePageComments(html);
		}
	}

	public void showBookPage(GutenbergBook book, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");
			printBookHTML(html, book);
			printPageEnd(html);
		}
	}

	private void printBookHTML(PrintWriter html, GutenbergBook book) {
		html.println("<div class='book'>");
		html.println("<table width=\"50%\">");
		html.println("<td>");
		//print link to comment section
		html.println("<a class='none' href='/book/"+book.id+"'>");
		html.println("<div class='title'><a href= \"/comments:"+ book.title.substring(0,1) +"/"+ book.id + "\">"+book.title+"</a></div>");
		if(book.creator != null) {
			html.println("<div class='creator'>" + book.creator + "</div>");
		}
		html.println("<a href='"+book.getGutenbergURL()+"'>On Project Gutenberg</a>");
		html.println("</td>");
		html.println("<td align = \"right\">");
		html.println("<form action=\"/flag\" method=\"POST\"/>");
		html.println("     <input type=\"submit\" value=\"Flag book\"/>");
		html.println("</form>");
		html.println("</td>");
		// TODO, finish up fields.
		html.println("</a>");
		html.println("</table>");
		html.println("</div>");
	}

	public void showBookCollection(List<GutenbergBook> theBooks, HttpServletResponse resp, char firstLetter, int numPages) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");

			for (int i = 0; i < theBooks.size(); i++) {
				printBookHTML(html, theBooks.get(i));
			}
			printPages( html, numPages, firstLetter);

			printPageEnd(html);
		}
	}
	
	private void printPages(PrintWriter html, int numPages, char firstLetter){
		//for the number of pages
		for(int i = 0; i < numPages; i++){
			//print a link to the book
			html.print("<a style = \"margin-left: 10pt;\" href = \"/title/" + firstLetter + "/" + i +"\"> " + (i+1) + " </a>");
		}
	}
}

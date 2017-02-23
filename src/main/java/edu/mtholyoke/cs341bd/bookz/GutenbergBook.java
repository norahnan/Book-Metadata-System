package edu.mtholyoke.cs341bd.bookz;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author jfoley.
 */
public class GutenbergBook {
  public String id;
  public String title;
  public String longTitle;
  public String creator;
  public String uploaded;
  public List<String> maybeWikipedias = new ArrayList<>();
  public List<String> libraryOfCongressSubjectHeading = new ArrayList<>();
  public List<String> libraryOfCongressSubjectCode = new ArrayList<>();
  public int downloads;
  public LinkedList<Comment> comments;
  
  public GutenbergBook(){
	  comments = new LinkedList<Comment>();
  }

  public int getBookNumber() {
    return Integer.parseInt(
        Objects.requireNonNull(
            Util.getAfterIfStartsWith("etext", id)));
  }

  public String getGutenbergURL() {
    return "http://www.gutenberg.org/ebooks/"+getBookNumber();
  }
  /**
   * Creates the page intro for the user to view the comments
   * @param html
   */
	public void writePageIntro(PrintWriter html) {
		html.println("<html>");
		html.println("<head>");
		html.println("<meta charset=\"UTF-8\">");
		html.println("<style>");

		//internal css (it honestly works a lot more reliably than the external one does)
		html.println("body {");
		html.println("background-image: url('http://orig00.deviantart.net/c088/f/2012/301/b/3/old_paper_stock_by_dl_stockandresources-d5j9yxo.jpg');");
		html.println("}");

		html.println("h2 {");
		html.println("color: rgb(122, 29, 3);");
		html.println("padding: 10px 12px;");
		html.println("text-align: center;");
		html.println("font-family: \"Apple Chancery\";");
		html.println("border-style: inset;");
		html.println("border-color: rgb(150,150,150);");
		html.println("margin: auto;");
		//html.println("margin: 80px 100px 80px 60px;");
		html.println("width: 80%;");
		html.println(" border-width: 6px;");
		html.println("} ");

		html.println("form {");
		html.println("padding: 15px 15px;");
		html.println("text-align: center;");
		html.println("}");

		html.println("input{");
		html.println("font-size: 20px;");
		//html.println("text-align: center;");
		html.println("}");

		html.println("h1{");
		html.println("font-size: 30;");
		html.println("font-family: Apple Chancery;");
		html.println("}");

		html.println("h5{");
		html.println("font-family: \"Noteworthy\";");
		html.println("font-size: 15;");
		html.println("border-style: dashed;");
		html.println("background-color:rgb(250,250,250);");
		html.println("margin: auto;");
		html.println("margin: 20px 150px 20px 150px;");
		html.println("padding: 15px 15px 15px 15px;");
		html.println("}");

		html.println("h3{");
		html.println("text-align: center;");
		html.println("font-family: \"Apple Chancery\";");
		html.println("}");


		html.println("</style>");
		html.println("<title>Comments on Post " + title + "</title>");
		html.println("</head>");
		html.println("<body>");

		//print the user's name
		html.println("<h1> Creator: "+ creator +"  </h1>");
		//add the original post text
		html.println("<h2> "+ longTitle +" </h2>");

		//write the form for posting a comment
		html.println("<div class = \"form\">");	
		html.println("<form action = \"/comments:"+ title.substring(0,1) +"/"+ id + "\"" + id + "\" method = \"POST\" >");
		html.println("<input style = \"font-size: 20px\" type = \"text\" name = \"comment\"/>");
		html.println("<input type = \"submit\" value = \"post comment\">");
		html.println("</form>");
		html.println("</div>");

		//return to home page buttons
		html.println("<div class = \"form\">");
		html.println("<form action = \"/front\" method = \"GET\">");
		html.println("<input type = \"submit\" value = \"back to home\">");
		html.println("</form>");
		html.println("</div>");

	}

	/**
	 * Writes the comments to the html page
	 * @param html
	 */
	public void writePageComments(PrintWriter html)
	{
		//for each comment create a comment space. alternate border colors
		for(int i = 0; i < comments.size(); i++){
			html.println("<div class = \"comment\">");
			if(i % 2 == 0){
				html.println("<h5 style = \"border-color: #492102;\">" + comments.get(i).comment + "</h5>");
			}
			else{
				html.println("<h5 style = \"border-color: rgb(122, 29, 3);\">" + comments.get(i).comment + "</h5>");
			}
			html.println("</div>");
		}
	}
}

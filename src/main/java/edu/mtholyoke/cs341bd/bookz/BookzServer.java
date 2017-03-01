package edu.mtholyoke.cs341bd.bookz;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * @author jfoley
 */
public class BookzServer extends AbstractHandler {
	Server jettyServer;
	HTMLView view;
	Model model;

	public BookzServer(String baseURL, int port) throws IOException {
		view = new HTMLView(baseURL);
		jettyServer = new Server(port);
		model = new Model();

		// We create a ContextHandler, since it will catch requests for us under
		// a specific path.
		// This is so that we can delegate to Jetty's default ResourceHandler to
		// serve static files, e.g. CSS & images.
		ContextHandler staticCtx = new ContextHandler();
		staticCtx.setContextPath("/static");
		ResourceHandler resources = new ResourceHandler();
		resources.setBaseResource(Resource.newResource("static/"));
		staticCtx.setHandler(resources);

		// This context handler just points to the "handle" method of this
		// class.
		ContextHandler defaultCtx = new ContextHandler();
		defaultCtx.setContextPath("/");
		defaultCtx.setHandler(this);

		// Tell Jetty to use these handlers in the following order:
		ContextHandlerCollection collection = new ContextHandlerCollection();
		collection.addHandler(staticCtx);
		collection.addHandler(defaultCtx);
		jettyServer.setHandler(collection);
	}

	/**
	 * Once everything is set up in the constructor, actually start the server
	 * here:
	 * 
	 * @throws Exception
	 *             if something goes wrong.
	 */
	public void run() throws Exception {
		jettyServer.start();
		jettyServer.join(); // wait for it to finish here! We're using threads behind the scenes; so this keeps the main thread around until something can happen!
	}

	/**
	 * The main callback from Jetty.
	 * 
	 * @param resource
	 *            what is the user asking for from the server?
	 * @param jettyReq
	 *            the same object as the next argument, req, just cast to a
	 *            jetty-specific class (we don't need it).
	 * @param req
	 *            http request object -- has information from the user.
	 * @param resp
	 *            http response object -- where we respond to the user.
	 * @throws IOException
	 *             -- If the user hangs up on us while we're writing back or
	 *             gave us a half-request.
	 * @throws ServletException
	 *             -- If we ask for something that's not there, this might
	 *             happen.
	 */
	@Override
	public void handle(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		System.out.println(jettyReq);

		String method = req.getMethod();
		String path = req.getPathInfo();
		System.out.println("\nThe path is: " + path);

		if(path.contains("etext")){
			
			//get the book id
			String id = path.substring(path.indexOf("etext"));
			//get the first letter
			char firstLetter = path.charAt( (path.indexOf("s:") + 2) );
			System.out.println("getting comments for: " + firstLetter + "/" + id);
			
			//get the list from the book
			List<GutenbergBook> list = model.getBooksStartingWith(firstLetter);
			
			GutenbergBook book = searchByID(list, id);
			
			if(method.equals("POST")){
				//get the comment
				Map<String, String[]> parameterMap = req.getParameterMap();
				String text = Util.join(parameterMap.get("comment"));
				
			    if(text != null) {
			    	book.comments.add(new Comment(text));
			    }
			}
			
			view.displayBookComment(book, resp);
			
			
		}
		
		
		
		else if("GET".equals(method)&& "/searchT".equals(path))
		{
			try (PrintWriter html = resp.getWriter()) {
			//get the input search word
			Map<String, String[]> parameterMap = req.getParameterMap();

			String titleCmdW = Util.join(parameterMap.get("title"));
			//System.out.println("We are inside titleCmdW" + titleCmdW);

			if(titleCmdW != null) {
				
					//char firstChar = titleCmd.charAt(0);
					//find the number of entries
					int numEntries = (int)Math.ceil((double)(model.searchTitle(titleCmdW).size()) / (double)(model.getEntriesPerPage()));
					html.println(titleCmdW);
					//html.println("</html>");
					//update the url to be searchT/"search word"/"page"
					
					String pageCmd = Util.getAfterIfStartsWith(("/searchT/"+titleCmdW+"/"), path);
					//System.out.println("We are inside the searhc title" + pageCmd);
					if(pageCmd != null) {
						//get the number to pass in 
						int page = Integer.parseInt(pageCmd);
						//set the page number in the model
						model.setCurrentPage(page);
						//System.out.println("We are inside the searhc title");
					}
					else{
						//set the page number in the model to zero
						model.setCurrentPage(0);
					}
					List<GutenbergBook> randomBooks = model.searchTitle(titleCmdW);

					for (GutenbergBook randomBook : randomBooks) {
						view.printBookHTML(html, randomBook);
					}

					view.showBookCollectionW(model.pageW(titleCmdW), resp, titleCmdW, numEntries);
			//handlesearchT(req,resp);
			
		}
			// Check for startsWith and substring
						String bookId = Util.getAfterIfStartsWith("/book/", path);
						System.out.println("This is the bookId in search: " + bookId);
						System.out.println("This is the path in search: " + path);

						if(bookId != null) {
							view.showBookPage(this.model.getBook(bookId), resp);
						}

						// Front page!
						if ("/front".equals(path) || "/".equals(path)) {
							view.showFrontPage(this.model, resp, true);
							//set the page in the model to zero
							model.setCurrentPage(0);
							return;
						}
			}
		}
		else if("POST".equals(method)&& "/flag".equals(path))
		{
			view.showFrontPage(this.model, resp, false);
			return;
		}
		else if("POST".equals(method)&& "/searchC".equals(path))
		{
			handlesearchC(req,resp);
			return;
		}
		
		else if ("GET".equals(method)) {
			
			String titleCmd = Util.getAfterIfStartsWith("/title/", path);
			
			if(titleCmd != null) {
				
					char firstChar = titleCmd.charAt(0);
					//find the number of entries
					int numEntries = (int)Math.ceil((double)(model.getBooksStartingWith(firstChar).size()) / (double)(model.getEntriesPerPage()));
					
					String pageCmd = Util.getAfterIfStartsWith(("/title/"+firstChar+"/"), path);
					//view.printPagesW();
					if(pageCmd != null) {
						//get the number to pass in 
						int page = Integer.parseInt(pageCmd);
						//set the page number in the model
						model.setCurrentPage(page);
					}
					else{
						//set the page number in the model to zero
						model.setCurrentPage(0);
					}
					

					view.showBookCollection(model.page(firstChar), resp, firstChar, numEntries);
			}

			// Check for startsWith and substring
			String bookId = Util.getAfterIfStartsWith("/book/", path);
			System.out.println("This is the bookId in initial letter: " + bookId);

			if(bookId != null) {
				view.showBookPage(this.model.getBook(bookId), resp);
			}

			// Front page!
			if ("/front".equals(path) || "/".equals(path)) {
				view.showFrontPage(this.model, resp, true);
				//set the page in the model to zero
				model.setCurrentPage(0);
				return;
			}
		}
		
		
	}
	
	private void handlesearchC(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}

	private void handlesearchT(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * finds a book from a list
	 */
	private GutenbergBook searchByID(List<GutenbergBook> list, String id){
		//for each book
		for (int i = 0; i < list.size(); i++){
			//if the current book matches the id
			if(list.get(i).id.equals(id)){
				//return book
				return list.get(i);
			}
		}
		//return null
		return null;
	}
}

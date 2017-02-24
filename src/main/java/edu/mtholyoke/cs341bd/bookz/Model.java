package edu.mtholyoke.cs341bd.bookz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
	Map<String, GutenbergBook> library;
	
	private int entriesPerPage;
	private int currentPage;

	public Model() throws IOException {
		// start with an empty hash-map; tell it it's going to be big in advance:
		library = new HashMap<>(40000);
		// do the hard work:
		DataImport.loadJSONBooks(library);
		entriesPerPage = 10;
		currentPage = 0;
	}

	public GutenbergBook getBook(String id) {
		return library.get(id);
	}

	public List<GutenbergBook> getBooksStartingWith(char firstChar) {
		// TODO, maybe it makes sense to not compute these every time.
		char query = Character.toUpperCase(firstChar);
		List<GutenbergBook> matches = new ArrayList<>(10000); // big
		for (GutenbergBook book : library.values()) {
			char first = Character.toUpperCase(book.title.charAt(0));
			if(first == query) {
				matches.add(book);
			}
		}
		return matches;
	}
	
	//get title with a certain word
	public List<GutenbergBook> searchTitle(String search) {
		// TODO, maybe it makes sense to not compute these every time.
		String query = search.toUpperCase();
		
		List<GutenbergBook> matches = new ArrayList<>(10000); // big
		for (GutenbergBook book : library.values()) {
			String first = book.title.toUpperCase();
			if(first.equals(query)) {
				matches.add(book);
			}
		}
		return matches;
	}
	
	//get title with a certain word
	public List<GutenbergBook> searchContent(String search) {
		// TODO, maybe it makes sense to not compute these every time.
		String query = search.toUpperCase();
		
		List<GutenbergBook> matches = new ArrayList<>(10000); // big
		for (GutenbergBook book : library.values()) {
			String first = book.longTitle.toUpperCase();
			if(first.equals(query)) {
				matches.add(book);
			}
		}
		return matches;
	}
	
	/**
	 * Returns the next page
	 */
	public List<GutenbergBook> page(char firstChar){
		//calculate the index entries*current
		int index = (entriesPerPage * currentPage);
		//if the 
		//create a new list that is only the entries from index to index + entries
		return getBooksStartingWith(firstChar).subList(index, (index + entriesPerPage));
	}
	
	/**
	 * Sets the number of entries allowed per page
	 */
	public void setEntriesPerPage(int entries){
		entriesPerPage = entries;
	}
	
	public void setCurrentPage(int page){
		currentPage = page;
	}
	
	/**
	 * Sets the number of entries allowed per page
	 */
	public int getEntriesPerPage(){
		return entriesPerPage;
	}

	public List<GutenbergBook> getRandomBooks(int count) {
		return ReservoirSampler.take(count, library.values());
	}
	
}

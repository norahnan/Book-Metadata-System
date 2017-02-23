package edu.mtholyoke.cs341bd.bookz;
import javax.annotation.Nonnull;

//import main.java.edu.mtholyoke.cs341bd.writr.WritrMessage;

/**
 * 
 * @author Surabhi
 */
public class Comment implements Comparable<Comment>
{
	/** This is the timestamp of when this comment was added */
	public long time;

	/** The comment the user typed in. */
	public String comment;
	
	/** The user who wrote the comment. */
	public Integer userID;
	
	/**
	 * Create a comment and init its time stamp.
	 * @param text the text of the message.
	 */
	public Comment(String text) 
	{
		comment = text;
		time = System.currentTimeMillis();
	}

	public Integer getUserID() 
	{
		return userID;
	}

	public void setUserID(Integer userID) 
	{
		this.userID = userID;
	}

	public long getTime() 
	{
		return time;
	}

	public void setTime(long time) 
	{
		this.time = time;
	}

	public String getComment() 
	{
		return comment;
	}

	public void setComment(String message) 
	{
		this.comment = message;
	}
	
	/**
	   * Adds the comment to the output
	   * @param output a string builder object, to which we'll add our HTML representation.
	  */ 
	  public void appendHTML(StringBuilder output) 
	  {
	    output
	        .append("<div class=\"message\">")
	        .append("<span class=\"datetime\">").append(Util.dateToEST(time)).append("</span>")
	        .append(comment)
	        .append("</div>");
	  }


	/**
	 * Sort newer comments to top by default. 
	 * @param o the other comments to compare to.
	 * @return comparator of (this, o).
	 */
	@Override
	public int compareTo(@Nonnull Comment o) 
	{
		return -Long.compare(time, o.time);
	}

	


}

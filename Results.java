package lucenedem;

public class Results {
	private String path;
	private String title;
	private String highlighted;
	private String totalhits;
	private String pagerank;
	public String getPath() {
		return path;
	}
	
	public Results()
	{
		
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPagerank() {
		return pagerank;
	}
	public void setPagerank(String pagerank) {
		this.pagerank = pagerank;
	}

	public String getHighlighted() {
		return highlighted;
	}
	public void setHighlighted(String highlighted) {
		this.highlighted = highlighted;
	}
	
	public String getTotalhits() {
		return totalhits;
	}
	
	public void setTotalhits(String totalhits) {
		this.title = totalhits;
	}
	
	
}
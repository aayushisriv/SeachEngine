package lucenedem;

import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;



public class Searchh {

	public List<Results> search(String text, HttpServletResponse response) {
		String index = "C:\\Users\\aayushi srivastava\\Documents\\Course MaterialTerm1\\InformationRetrievalSystem\\ProjectWork\\pagerank";
		List<Results> list=new ArrayList();
		
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();	
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("topic_title", analyzer);
			Query query = parser.parse(text);
			DoubleValuesSource boostByField = DoubleValuesSource.fromLongField("boost");//exposes a document score
			FunctionScoreQuery modifiedQuery = new FunctionScoreQuery(query, boostByField);//creates a query based on the old query and the boost
			TopDocs results = searcher.search(modifiedQuery, 5);//search as usual
			Formatter formatter = new SimpleHTMLFormatter("<span style=\"background:red;\">", "</span>");
			
	        //It scores text fragments by the number of unique query terms found
	        //Basically the matching score in layman terms
	        QueryScorer scorer = new QueryScorer(query, "topic_title");
	         
	        //used to markup highlighted terms found in the best sections of a text
	        Highlighter highlighter = new Highlighter(formatter, scorer);
	         
	        //It breaks text up into same-size texts but does not split up spans
	        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 5);
			//out.println("<br>"+results.totalHits + " total matching documents" + "<br>");
			for (int i = 0; i < 100; i++) {
				System.out.println("hdddddi");
				Results result = new Results();
				Document doc = searcher.doc(results.scoreDocs[i].doc);
				String title = doc.get("topic_title");
				String contents = 	doc.get("rank");
				System.out.println(contents);
				System.out.println((i + 1) + ":Paper Id: " + doc.get("topic_title"));
				System.out.println("   Rank: " + doc.get("rank1"));
				TokenStream stream = analyzer.tokenStream("", new StringReader(title));
				String highlightedText = "";
               highlightedText = highlighter.getBestFragments(stream, title, 2, "...<br/>");
                System.out.println(highlightedText);
				out.println("<p>");
				if (title != null) {
					
					result.setHighlighted(highlightedText);
					result.setTitle("   Rank: " + doc.get("node"));
				}
				
				
				list.add(result);
			}
			System.out.println(results.totalHits + " total matching documents");
			
			reader.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return list;
	}
}

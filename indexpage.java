package lucenedem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Index all text files under a directory.
 */
public class indexpage {
	static int counter = 0;
	static Integer id = 1000;

	public static void main(String[] args) throws Exception {
		String indexPath = "C:\\Users\\aayushi srivastava\\Documents\\Course MaterialTerm1\\InformationRetrievalSystem\\ProjectWork\\pagerank\\"; 
		String docsPath = "C:\\Users\\aayushi srivastava\\Documents\\Course MaterialTerm1\\InformationRetrievalSystem\\ProjectWork\\pagerank_final.txt";
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(dir, iwc);
		writer.deleteAll();
		indexDoc(writer, Paths.get(docsPath));
		System.out.println("done");
		writer.close();
	}

	/*static void indexDocs(final IndexWriter writer, Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				indexDoc(writer, file);
				return FileVisitResult.CONTINUE;
			}
		});
	}
*/
	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, Path file) throws IOException {
		InputStream stream = Files.newInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		String title;		
	    while ((title = br.readLine()) != null) {
	    	String[] arr = title.split(",");
	    	String node = arr[0];
	    	//String rank = arr[1];
	    	float rank =  Float.valueOf(arr[1]);
	    	String topic_title = arr[2];
	    	String rank1 = arr[1];
	    	Document doc = new Document();
			doc.add(new StringField("node", node.toString(), Field.Store.YES));
			doc.add(new FloatDocValuesField("boost",rank));
			doc.add(new TextField("topic_title", topic_title.toString(), Field.Store.YES));
			doc.add(new TextField("rank1", rank1.toString(), Field.Store.YES));
			writer.addDocument(doc);
			//doc = new Document();
			//doc.add(new StringField("node", node.toString(), Field.Store.YES));
			//doc.add(new TextField("topic_title", topic_title.toString(), Store.YES));
			//doc.add(new NumericDocValuesField("boost", 2L));
			//writer.addDocument(doc);
			counter++;
			if (counter % 1000 == 0)
				System.out.println("indexing " + counter + "-th topic_title " + file.getFileName());
			;
        }
	}
}

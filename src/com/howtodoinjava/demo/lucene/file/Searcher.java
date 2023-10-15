package com.howtodoinjava.demo.lucene.file;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
    private static final String INDEX_DIR = "indexedFiles";
    private static IndexSearcher searcher;

    private Searcher() {}

    public static HashSet<String> search(String query) throws Exception{

        //String[] parts = query.split(" ");
        HashSet<String> results = new HashSet<String>();
        return Searcher.handleSimpleSearch(query);
        //return Searcher.SearchThis(query);
    }

        

    /**
     * Performs a query on a single word.
     * @param query
     * @return
     */
    private static HashSet<String> handleSimpleSearch (String query) throws Exception {
        searcher = createSearcher();
        HashSet<String> results = new HashSet<String>();
        results.add(" ");
        TopDocs foundDocs = searchInContent(query, searcher);
        System.out.println("Total Results :: " + foundDocs.totalHits + "\n");
        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            String[] parts = d.get("contents").toString().split("\n");
            //String[] partss = parts[1].toString().split("\n");
            results.add((parts[0].toString().substring(2)));           
            System.out.println("Path: " + d.get("path") + ", Score: " + sd.score + "\n");
            //System.out.println(d.get("title"));
            }
        return results;
    }
     

    private static HashSet<String> SearchThis (String indexpath) throws IOException, ParseException {
        String index = indexpath;
    	IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
    	IndexSearcher searcher = new IndexSearcher(reader);
    	Analyzer analyzer = new StandardAnalyzer();
    	QueryParser parser = new QueryParser("contents", analyzer);
    	Query query = parser.parse("George J. Pappas");
    	TopDocs results = searcher.search(query, 5);
    	System.out.println(results.totalHits + " total matching documents");
        HashSet<String> resultss = new HashSet<String>();
        //TopDocs results = searcher.search(parsedQuery, 5);
    	//System.out.println(results.totalHits + " total matching documents");
    	for (int i = 0; i < 5; i++) {
    			Document doc = searcher.doc(results.scoreDocs[i].doc);
    			String path = doc.get("path");
    			//System.out.println((i + 1) + ". " + path);
    			String title = doc.get("title");
    			String contents = doc.get("contents");
    			String searchstring = contents.toLowerCase(); //lower case contents
    			int start = searchstring.indexOf("abstract");
    			int end = searchstring.indexOf("introduction");
    			String Abstract = "";
    			if (start > 0 && end > start) {
    				Abstract = contents.substring(start, end);
                    resultss.add(Abstract); 
    			} else {
    				Abstract = "not found " + "path: " + path ;
    			}

            }
        reader.close();
        System.out.println("This is resultss:"+resultss);
        return resultss;
    }
   
    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception {
        QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
        Query query = qp.parse(textToFind);
        return searcher.search(query, 10);
    }

    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        return new IndexSearcher(reader);
    }

}
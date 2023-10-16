package com.howtodoinjava.demo.lucene.file;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneReadIndexFeature_1 {
    private static final String INDEX_DIR = "indexedFiles";
    private static IndexSearcher searcher;
    private static int frameWidth = 500;
    private static int frameHeight = 300; 
    private static int numTopHits = 10;
    private static int lineLength = 40;
    private static int titleLength = 40;
    private static int contentLength = 100;
    
    public static void main(String[] args) throws Exception {
        // Create Lucene searcher. It searches over a single IndexReader.
        searcher = createSearcher();

        // Create and set up the GUI frame
        JFrame frame = new JFrame("Lucene Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.getContentPane().setLayout(new BorderLayout());

        // Create components
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Enter search query:");
        JTextField textField = new JTextField(lineLength);
        JButton searchButton = new JButton("Search");

        JPanel resultPanel = new JPanel();

        JTextArea resultArea = new JTextArea(numTopHits, lineLength);
        resultArea.setEditable(false);

        // Add components to the top panel
        topPanel.add(label);
        topPanel.add(textField);
        topPanel.add(searchButton);
        
        // Add action listener for the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchQuery = textField.getText();
                String targetSection = "abstract";
                String latterSection = "introduction";
                String section = "";
                try {
                    TopDocs foundDocs = searchInContent(searchQuery, searcher);
                    System.out.println(foundDocs.scoreDocs.length);
                    resultArea.setText("Total Results :: " + foundDocs.totalHits + "\n");
                    resultArea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
                    for (ScoreDoc sd : foundDocs.scoreDocs) {
                        Document d = searcher.doc(sd.doc);
                     // get the desired section from the result document
                        section = findSection(targetSection, latterSection, d);
                        resultArea.append("Path: " + d.get("path") + ", \tScore: " + sd.score + "\n");
                        resultArea.append(d.get("contents").substring(0,contentLength));
                        resultArea.append("Section : "+ targetSection + "  .................\n" + section);
                    }
                } catch (Exception ex) {
                    resultArea.setText("An error occurred: " + ex.getMessage());
                }
            }
        });
        resultPanel.setLayout(new BorderLayout(0, 0));

        // Add components to the result panel
        resultPanel.add(new JScrollPane(resultArea));

        // Add panels to the frame
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(resultPanel, BorderLayout.CENTER);

        // Set frame visibility and center on screen
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception {
        QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
        Query query = qp.parse(textToFind);
        return searcher.search(query, numTopHits);
    }
    
    // Finds the appropriate section of the resultDocument considering the given strings as correct ones
    private static String findSection(String targetSection, String latterSection, Document resultDocument){
    	String section = "";
    	String result = resultDocument.get("contents").toLowerCase();
    	int start = result.indexOf(targetSection);
    	int end = result.indexOf(latterSection);
    	System.out.println("The substring start and end is : " + start + " , " + end);
    	if(start >= 0 && end >= 0) {
    		section = result.substring(start, end);
    	}
    	return section;
    }
    
    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        return new IndexSearcher(reader);
    }
    
}

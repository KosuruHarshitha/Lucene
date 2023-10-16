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

public class Hsample {
    private static final String INDEX_DIR = "indexedFiles";
    private static IndexSearcher searcher;

    public static void main(String[] args) throws Exception {
        // Create Lucene searcher. It searches over a single IndexReader.
        searcher = createSearcher();

        // Create and set up the GUI frame
        JFrame frame = new JFrame("Lucene Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        // Create components
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Enter search query:");
        JTextField textField = new JTextField(30);
        JButton searchButton = new JButton("Search");

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());

        JTextArea resultArea = new JTextArea(10, 30);
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
                try {
                    TopDocs foundDocs = searchInContent(searchQuery, searcher);
                    resultArea.setText(""); // Clear previous results
                    resultArea.append("Total Results :: " + foundDocs.totalHits + "\n");
                    for (ScoreDoc sd : foundDocs.scoreDocs) {
                        Document d = searcher.doc(sd.doc);
                        String path = d.get("path");
                        //String title = d.get("title");
                        String contents = d.get("contents");
                        String searchstring = contents.toLowerCase(); //lower case contents
                        int start = searchstring.indexOf("abstract");
                        int end = searchstring.indexOf("introduction");
                        String abstractText = "";
                        if (start > 0 && end > start) {
                            abstractText = contents.substring(start, end);
                        } else {
                            abstractText = "Abstract not found " ;
                        }

                        // Append the results to the result area
                        resultArea.append("Result: " + abstractText + "file name is " + getfilenamefrompath(path) + "\n----------------\n");
                    }
                } catch (Exception ex) {
                    resultArea.setText("An error occurred: " + ex.getMessage());
                }
            }
        });

        // Add components to the result panel
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Add panels to the frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(resultPanel, BorderLayout.CENTER);

        // Set frame visibility and center on screen
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        //search(INDEX_DIR, resultArea);
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

    public static String getfilenamefrompath(String input) {
        
        String pattern = "\\d+.txt";
        //Boolean b = Pattern.matches(pattern,input);
        String[] arrOfStr = input.split(pattern);
        String retval = input.replace(arrOfStr[0],"").replace(".txt","");
        //for (String a : arrOfStr)
        return retval;
    }
}

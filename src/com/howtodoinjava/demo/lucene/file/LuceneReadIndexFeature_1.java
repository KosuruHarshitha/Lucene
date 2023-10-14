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
                    resultArea.setText("Total Results :: " + foundDocs.totalHits + "\n");
                    for (ScoreDoc sd : foundDocs.scoreDocs) {
                        Document d = searcher.doc(sd.doc);
                        resultArea.append("Path: " + d.get("path") + ", Score: " + sd.score + "\n");
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

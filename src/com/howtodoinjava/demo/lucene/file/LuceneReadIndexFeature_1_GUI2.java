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
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.GroupLayout.Alignment;

public class LuceneReadIndexFeature_1_GUI2 {
	//Lucene search components
    private final String INDEX_DIR = "indexedFiles";
    private final int numTopHits = 10;
    private final int resultListSize = 10;
    private IndexSearcher searcher;
    
    // Lucene result components
    private String result = "Sample"; 
	
    // GUI elements declaration
    // the naming are <Component_Name> followed by the <Component_Purpose>
    private JFrame frame;
    
    // GUI elements properties
    private int frameWidth = 600;
    private int frameHeight = 450; 
    private int lineLength = 30;
    private int resultAreaLength = 50;
    private int titleLength = 40;
    private int contentLength = 30;
    
    // for ActionListener Sections
    String searchQuery;
    String targetSection = "abstract";
    String latterSection = "introduction";
    String mainResultText = "";
    String[] topResultsText = new String[numTopHits];
    String section = "";
    private JPanel panelSearchParent;
    private JLabel lblSearchPanel;
    private JTextField tfSearch;
    private JButton btnSearch;
    private JPanel panelResultParent;
    private JPanel panelResultChild2;
    private JList listResults;
    
    // Lucene methods
    public void initLucene() throws IOException {
    	searcher = createSearcher();
    }
    
    // Create the IndexSearcher
    private IndexSearcher createSearcher() throws IOException{
    	Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        return new IndexSearcher(reader);
    }
    
    private TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception {
        QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
        Query query = qp.parse(textToFind);
        return searcher.search(query, numTopHits);
    }
    
    // Finds the appropriate section of the resultDocument considering the given strings as correct ones
    private String findSection(String targetSection, String latterSection, Document resultDocument){
    	String section = "";
    	result = resultDocument.get("contents").toLowerCase();
    	int start = result.indexOf(targetSection);
    	int end = result.indexOf(latterSection);
    	System.out.println("The substring start and end is : " + start + " , " + end);
    	if(start >= 0 && end >= 0) {
    		section = result.substring(start, end);
    	}
    	return section;
    }
    
    // Main processing of the query results for the GUI actions
    private void collectQueryResults() throws Exception {
    	int i = 0;
    	searchQuery = tfSearch.getText();
    	System.out.println("searchQuery = " + searchQuery);
    	
    	TopDocs foundDocs = searchInContent(searchQuery, searcher);
        System.out.println(foundDocs.scoreDocs.length);
        mainResultText = "Total Number of Results : " + foundDocs.totalHits + "\n";
        System.out.println(mainResultText);
        
        // Take each row for the short results list
        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document document = searcher.doc(sd.doc);
            // This will have to be replaced by the actual matching content in the document
            topResultsText[i++] = "Path: " + document.get("path") + ", \tScore: " + sd.score + "\n";
            System.out.println("topResultsText[" + (i - 1) + "] = " + topResultsText[i - 1]);
        }
    }
    
    ///////////////////////////////////////////////////
    // GUI Section
    ///////////////////////////////////////////////////
    
    // Create the GUI and add components
    public void initGUI() {
    	// Create and set up the GUI frame
        frame = new JFrame("Lucene Search");
        frame.setSize(new Dimension(frameWidth, frameHeight));
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().setSize(frameWidth, frameHeight);
    	frame.getContentPane().setLayout(null);
    	
    	panelSearchParent = new JPanel();
    	panelSearchParent.setBounds(0, 0, 584, 39);
    	frame.getContentPane().add(panelSearchParent);
    	
    	lblSearchPanel = new JLabel("Enter Query Text");
    	lblSearchPanel.setFont(new Font("Tahoma", Font.BOLD, 11));
    	lblSearchPanel.setHorizontalAlignment(SwingConstants.LEFT);
    	panelSearchParent.add(lblSearchPanel);
    	
    	tfSearch = new JTextField();
    	tfSearch.setColumns(lineLength);
    	panelSearchParent.add(tfSearch);
    	
    	btnSearch = new JButton("Search");
    	panelSearchParent.add(btnSearch);
    	
    	panelResultParent = new JPanel();
    	panelResultParent.setBounds(0, 38, 584, 372);
    	frame.getContentPane().add(panelResultParent);
    	panelResultParent.setLayout(null);
    	
    	JPanel panelResultChild1 = new JPanel();
    	panelResultChild1.setBackground(new Color(192, 223, 233));
    	panelResultChild1.setBounds(0, 0, 293, 372);
    	panelResultParent.add(panelResultChild1);
    	panelResultChild1.setLayout(null);
    	
    	listResults = new JList();
    	listResults.setBounds(10, 11, 273, 350);
    	panelResultChild1.add(listResults);
    	
    	panelResultChild2 = new JPanel();
    	panelResultChild2.setBackground(new Color(240, 240, 240));
    	panelResultChild2.setBounds(291, 0, 293, 372);
    	panelResultParent.add(panelResultChild2);
    	frame.setVisible(true);
    }
    
    public void showGUI() {
    	// Collect the data for showing 
    	
    	
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setVisible(true);
    }
    
    public void printSomething(String x) {
    	System.out.println(x);
    }
    
    // populate the JList with the short result rows
    private void populateResultList() {
    	listResults.setListData(topResultsText);
    	for(int i = 0; i < resultListSize; i++) {
    		// Edit the JButtons in the list and make them visible
    		
    	}
    }
    
    public void addActionListeners() {
        
        // CLickable result link button (leading to the details of a result)
    	btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// with the press of the search button, all the subsequent sections will change
                try {
                    collectQueryResults();
                    //tpMainResultText.setText(mainResultText);
                    // populate JList with the results
                    populateResultList();
                } catch (Exception ex) {
                    System.out.println("An error occurred: " + ex.getMessage());
                }
            }
        });
    }
    
    // template method to generate and show a gui
    public void createGUI() throws Exception {
        // Initialize all the components
        initGUI();
        
        // Add Action / Event Listeners
        addActionListeners();
        
        // View the GUI
        //showGUI();
        
    }
    
    public static void main(String[] args) throws Exception{
    	LuceneReadIndexFeature_1_GUI2 lucene = new LuceneReadIndexFeature_1_GUI2();
    	
    	// Create Lucene searcher. It searches over a single IndexReader.
        lucene.initLucene();
        
        //Create GUI
        lucene.initGUI();
        // Add ActionListeners and edit GUI as needed
        lucene.addActionListeners();
        lucene.showGUI();
    }
}

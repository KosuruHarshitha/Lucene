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

public class LuceneReadIndexFeature_1_GUI {
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
	private JPanel panelSearchParent;
	private SpringLayout sl_paneSearchChild;
    private JPanel paneSearchChild;
    private JLabel lblSearch;
    private JTextField tfSearch;
    private JButton btnSearch;
    private JPanel panelResultParent;
    private JPanel panelResultChild1;
    private JList listResults;
    private JPanel panelResultChild2;
    
    // GUI elements properties
    private int frameWidth = 500;
    private int frameHeight = 300; 
    private int lineLength = 20;
    private int resultAreaLength = 50;
    private int titleLength = 40;
    private int contentLength = 30;
    
    // for ActionListener Sections
    String searchQuery;
    String targetSection = "abstract";
    String latterSection = "introduction";
    String mainResultText = "";
    String[] topResultsText = {};
    String section = "";
    private JTextPane tpMainResultText;
    
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
    	tfSearch.getText();
    	TopDocs foundDocs = searchInContent(searchQuery, searcher);
        System.out.println(foundDocs.scoreDocs.length);
        mainResultText = "Total Number of Results : " + foundDocs.totalHits + "\n";       
        
        // Take each row for the short results list
        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document document = searcher.doc(sd.doc);
            // This will have to be replaced by the actual matching content in the document
            topResultsText[i++] = "Path: " + document.get("path") + ", \tScore: " + sd.score + "\n";
        }
    }
    
    ///////////////////////////////////////////////////
    // GUI Section
    ///////////////////////////////////////////////////
    
    // Create the GUI and add components
    public void initGUI() {
    	// Create and set up the GUI frame
        frame = new JFrame("Lucene Search");
        frame.getContentPane().setLayout(null);
        
        // Add a panel to hold the search contents
        panelSearchParent = new JPanel();
        panelSearchParent.setBackground(UIManager.getColor("Button.light"));
        panelSearchParent.setBounds(0, 0, 746, 42);
        frame.getContentPane().add(panelSearchParent);
    	SpringLayout sl_panelSearchParent = new SpringLayout();
    	panelSearchParent.setLayout(sl_panelSearchParent);
    	
    	paneSearchChild = new JPanel();
    	paneSearchChild.setBackground(UIManager.getColor("Button.light"));
    	sl_panelSearchParent.putConstraint(SpringLayout.NORTH, paneSearchChild, 10, SpringLayout.NORTH, panelSearchParent);
    	sl_panelSearchParent.putConstraint(SpringLayout.WEST, paneSearchChild, 80, SpringLayout.WEST, panelSearchParent);
    	sl_panelSearchParent.putConstraint(SpringLayout.SOUTH, paneSearchChild, -5, SpringLayout.SOUTH, panelSearchParent);
    	sl_panelSearchParent.putConstraint(SpringLayout.EAST, paneSearchChild, 655, SpringLayout.WEST, panelSearchParent);
    	sl_paneSearchChild = new SpringLayout();
    	paneSearchChild.setLayout(sl_paneSearchChild);
    	
    	lblSearch = new JLabel("Enter Search Query");
    	sl_paneSearchChild.putConstraint(SpringLayout.NORTH, lblSearch, 3, SpringLayout.NORTH, paneSearchChild);
    	sl_paneSearchChild.putConstraint(SpringLayout.WEST, lblSearch, 0, SpringLayout.WEST, paneSearchChild);
    	lblSearch.setFont(new Font("Arial", Font.BOLD, 13));
    	paneSearchChild.add(lblSearch);
    	
    	tfSearch = new JTextField();
    	sl_paneSearchChild.putConstraint(SpringLayout.NORTH, tfSearch, -2, SpringLayout.NORTH, lblSearch);
    	sl_paneSearchChild.putConstraint(SpringLayout.WEST, tfSearch, 6, SpringLayout.EAST, lblSearch);
    	sl_paneSearchChild.putConstraint(SpringLayout.EAST, tfSearch, -77, SpringLayout.EAST, paneSearchChild);
    	tfSearch.setMinimumSize(new Dimension(25, 20));
    	tfSearch.setColumns(10);
    	paneSearchChild.add(tfSearch);
    	
    	btnSearch = new JButton("Search");
    	sl_paneSearchChild.putConstraint(SpringLayout.NORTH, btnSearch, -3, SpringLayout.NORTH, lblSearch);
    	sl_paneSearchChild.putConstraint(SpringLayout.WEST, btnSearch, 6, SpringLayout.EAST, tfSearch);
    	btnSearch.setFont(new Font("Arial", Font.PLAIN, 12));
    	paneSearchChild.add(btnSearch);
    	panelSearchParent.add(paneSearchChild);
    	
    	panelResultParent = new JPanel();
    	panelResultParent.setBackground(UIManager.getColor("ScrollBar.background"));
    	panelResultParent.setBounds(0, 40, 746, 366);
    	frame.getContentPane().add(panelResultParent);
    	SpringLayout sl_panelResultParent = new SpringLayout();
    	panelResultParent.setLayout(sl_panelResultParent);
    	
    	panelResultChild1 = new JPanel();
    	sl_panelResultParent.putConstraint(SpringLayout.NORTH, panelResultChild1, 26, SpringLayout.NORTH, panelResultParent);
    	sl_panelResultParent.putConstraint(SpringLayout.WEST, panelResultChild1, 21, SpringLayout.WEST, panelResultParent);
    	sl_panelResultParent.putConstraint(SpringLayout.SOUTH, panelResultChild1, -25, SpringLayout.SOUTH, panelResultParent);
    	sl_panelResultParent.putConstraint(SpringLayout.EAST, panelResultChild1, -386, SpringLayout.EAST, panelResultParent);
    	panelResultParent.add(panelResultChild1);
    	
    	panelResultChild2 = new JPanel();
    	sl_panelResultParent.putConstraint(SpringLayout.NORTH, panelResultChild2, 0, SpringLayout.NORTH, panelResultChild1);
    	sl_panelResultParent.putConstraint(SpringLayout.WEST, panelResultChild2, 23, SpringLayout.EAST, panelResultChild1);
    	panelResultChild1.setLayout(null);
    	
    	listResults = new JList();
    	listResults.setBackground(new Color(192, 223, 233));
    	listResults.setFont(new Font("Arial", Font.PLAIN, 12));
    	listResults.setBounds(10, 304, 319, -263);
    	panelResultChild1.add(listResults);
    	
    	tpMainResultText = new JTextPane();
    	tpMainResultText.setFont(new Font("Arial", Font.PLAIN, 11));
    	tpMainResultText.setBounds(10, 11, 319, 20);
    	panelResultChild1.add(tpMainResultText);
    	
    	sl_panelResultParent.putConstraint(SpringLayout.SOUTH, panelResultChild2, -25, SpringLayout.SOUTH, panelResultParent);
    	sl_panelResultParent.putConstraint(SpringLayout.EAST, panelResultChild2, -24, SpringLayout.EAST, panelResultParent);
    	panelResultParent.add(panelResultChild2);
    	panelResultChild2.setLayout(new SpringLayout());
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    	for(int i = 0; i < resultListSize; i++) {
    		// Edit the JButtons in the list and make them visible
    		
    	}
    }
    
    public void addActionListeners() {
    	
    	// Search button
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// with the press of the search button, all the subsequent sections will change
            	int i = 0;
                try {
                    collectQueryResults();
                    tpMainResultText.setText(mainResultText);
                    // populate JList with the results
                    populateResultList();
                } catch (Exception ex) {
                    System.out.println("An error occurred: " + ex.getMessage());
                }
            }
        });
        
        // CLickable result link button (leading to the details of a result)
        
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
    	LuceneReadIndexFeature_1_GUI lucene = new LuceneReadIndexFeature_1_GUI();
    	
    	// Create Lucene searcher. It searches over a single IndexReader.
        lucene.initLucene();
        
        //Create GUI
        lucene.initGUI();
        // Add ActionListeners and edit GUI as needed
        lucene.addActionListeners();
        lucene.showGUI();
    }
}

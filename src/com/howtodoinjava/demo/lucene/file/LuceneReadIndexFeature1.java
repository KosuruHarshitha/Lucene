package com.howtodoinjava.demo.lucene.file;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
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

public class LuceneReadIndexFeature1 {
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
    private String searchQuery;
    private String targetSection = "abstract";
    private String latterSection = "introduction";
    private String mainResultText = "";
    private String[] topResultsText = new String[numTopHits];
    private String section = "";
    // Storing the entire "contents" of the particular documents by list
    private String[] resultDocuments;
    // the total result documents got from the query
    private TopDocs foundDocs;
    
    // More GUI elements
    private JPanel panelSearchParent;
    private JLabel lblSearchPanel;
    private JTextField tfSearch;
    private JButton btnSearch;
    private JPanel panelResultParent;
    private JPanel panelResultChild2;
    private JList listResults;
    private JPanel panelResultChild1;
    private JTextArea detailedResults;
    private JScrollPane spDetailedResults;
    
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
    private String findSection(String targetSection, String latterSection, Document resultDoc){
    	String section = "";
    	result = resultDoc.get("contents").toLowerCase();
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
    	
    	foundDocs = searchInContent(searchQuery, searcher);
        System.out.println(foundDocs.scoreDocs.length);
        mainResultText = "Total Number of Results : " + foundDocs.totalHits + "\n";
        System.out.println(mainResultText);
        // store the result documents in a vector for later access to the contents by index
        resultDocuments = new String[numTopHits];
        
        // Take each row for the short results list
        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document document = searcher.doc(sd.doc);
            // This will have to be replaced by the actual matching content in the document
            topResultsText[i] = "Path: " + document.get("path") + ", \tScore: " + sd.score + "\n";
            resultDocuments[i++] = document.get("contents").toString();
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
    	
    	panelResultChild1 = new JPanel();
    	panelResultChild1.setBackground(SystemColor.inactiveCaption);
    	panelResultChild1.setBounds(0, 0, 293, 372);
    	panelResultParent.add(panelResultChild1);
    	panelResultChild1.setLayout(null);
    	
    	listResults = new JList();
    	listResults.setBounds(10, 11, 273, 350);
    	panelResultChild1.add(listResults);
    	
    	panelResultChild2 = new JPanel();
    	panelResultChild2.setBackground(SystemColor.inactiveCaption);
    	panelResultChild2.setBounds(291, 0, 293, 372);
    	panelResultParent.add(panelResultChild2);
    	panelResultChild2.setLayout(null);
    	
    	spDetailedResults = new JScrollPane();
    	spDetailedResults.setBounds(10, 11, 273, 350);
    	panelResultChild2.add(spDetailedResults);
    	
    	detailedResults = new JTextArea();
    	spDetailedResults.setViewportView(detailedResults);
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
    
    private void addActionListeners() {
        
        // CLickable result link button (leading to the details of a result)
    	btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// with the press of the search button, all the subsequent sections will change
                try {
                    collectQueryResults();
                    // populate JList with the results
                    populateResultList();
                    // Makes the JList to read the mouse click on individual search result for a detailed view
                    addMouseListenerToList();
                } catch (Exception ex) {
                    System.out.println("An error occurred: " + ex.getMessage());
                }
            }
        });
    }
    
    // MouseListener for the JList
    private void addMouseListenerToList() {
    	listResults.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent mouseEvent) {
    			JList <String> tmpList  = (JList) mouseEvent.getSource();
    			if (mouseEvent.getClickCount() == 2) {
    				int index = tmpList.locationToIndex(mouseEvent.getPoint());
    				if (index >= 0) {
    					Object singleResult = tmpList.getModel().getElementAt(index);
    					// Append to the detailedResults Section
    					detailedResults.setText(resultDocuments[index]);
    					System.out.println("Double-clicked on: " + singleResult.toString());
    				}
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
    	LuceneReadIndexFeature1 lucene = new LuceneReadIndexFeature1();
    	
    	// Create Lucene searcher. It searches over a single IndexReader.
        lucene.initLucene();
        
        //Create GUI
        lucene.initGUI();
        // Add ActionListeners and edit GUI as needed
        lucene.addActionListeners();
        lucene.showGUI();
    }
}

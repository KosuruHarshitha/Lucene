package com.howtodoinjava.demo.lucene.file;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.*;
import org.apache.lucene.search.suggest.analyzing.BlendedInfixSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


import com.howtodoinjava.demo.lucene.gui.*;

public class LuceneReadIndexFeature1 {
	//Lucene search components
    private final String INDEX_DIR = "indexedFiles";
    private final String INDEX_DIR_SUGGESTER = "suggesterIndexedFiles";
    private final int numTopHits = 10;
    private final int resultListSize = 10;
    private IndexSearcher searcher;
    
    // Lucene result components
    private String result = "Sample"; 
	
    // GUI elements declaration
    // the naming are <Component_Name> followed by the <Component_Purpose>
    private JFrame frame;
    
    // GUI elements properties
    private Dimension screenDimension;
    private int screenWidth, screenHeight;
    // the X,Y for centering the JFrame
    private int centerX, centerY;
    private final int frameWidth = 977;
    private final int frameHeight = 682; 
    private int lineLength = 30;
    private int resultAreaLength = 50;
    private int titleLength = 40;
    private int contentLength = 30;
    // The content length of the list items in the JList
    private int listItemContentLength = 100;
    
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
    private JScrollPane spListResults;
    
    // Lucene methods
    public void initLucene() throws IOException {
    	searcher = createSearcher();
    }
    
    // Create Index for the suggester
    private void createSuggesterIndex() {
    	StandardAnalyzer analyzer = new StandardAnalyzer();
    	//AnalyzingSuggester suggester = new AnalyzingSuggester(indexDir, analyzer);
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
    	if(start >= 0 && end > start) {
    		section = result.substring(start, end);
    	}else{
    		section = "Abstract not found";
    	}
    	return section;
    }
    
    private String getFilenameFromPath(String input) {
        String pattern = "\\d+.txt";
        //Boolean b = Pattern.matches(pattern,input);
        String[] arrOfStr = input.split(pattern);
        String retval = input.replace(arrOfStr[0],"").replace(".txt","");
        System.out.println("The fetched filename is : " + retval);
        //for (String a : arrOfStr)
        return retval;
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
            String path = document.get("path");
            String tmpSection, subTmpSection;
            // This will have to be replaced by the actual matching content in the document
            //topResultsText[i] = "Path: " + document.get("path") + ", \tScore: " + sd.score + "\n";
            // each topResultsText[i] should now show the portion of Abstracts of the papers and the file name
            tmpSection = findSection("abstract", "introduction", document);
            System.out.println("tmpSection = " + tmpSection);
            
            subTmpSection = tmpSection.substring(0, Math.min(listItemContentLength, tmpSection.length()));
            System.out.println("subTmpSection = " + subTmpSection);
            String keywordsString = getKeywords(document.get("contents"));
            //topResultsText[i] = keywordsString + subTmpSection + "\nFile #" + (i + 1) + " : " + getFilenameFromPath(path);
            topResultsText[i] = "<html>" + keywordsString + "<br>" + subTmpSection + "<br>File #" + (i + 1) + " : " + getFilenameFromPath(path)+ "</html>";
            resultDocuments[i++] = document.get("contents").toString();
            System.out.println("topResultsText[" + (i - 1) + "] = " + topResultsText[i - 1]);
        }
    }
    public static String getKeywords(String content) throws IOException {
        final List<String> stopwords;
        stopwords = Files.readAllLines(Paths.get("resources\\english_stopwords.txt"));
        String stopwordsRegex = stopwords.stream()
            .collect(Collectors.joining("|", "\\b(", ")\\b\\s?"));
        String refined = content.replaceAll("[^a-zA-Z]", " ");
        String result = refined.toLowerCase().replaceAll(stopwordsRegex, " ");
        HashMap<String, Integer> res = ngrams(result.toString(),3); 
        res.remove(" ");
        List<String> resKeys = new ArrayList<String>(res.keySet());
        Integer lenOfres = resKeys.size();
        for (int i = 0; i < lenOfres; i++) {
                //System.out.println("reskey:" +resKeys.get(i) + " - "+ resKeys.get(i).length());
                //List<String> items = Arrays. asList(resKeys.get(i). split("\\s*,\\s*"));
                List<String> items = Arrays. asList(resKeys.get(i). split(" *,  *"));
                for (int j = 0; j < items.size(); j++){
                    if(items.get(j).length()<2){
                        res.remove(resKeys.get(i));}
                    String stripped =  items.get(j).replaceAll("", " ");
                    if(stripped.length()<=2){res.remove(resKeys.get(i));}
                }
                
            }
        List<String> resKeyss = new ArrayList<String>(res.keySet());
        Integer lenOfress = resKeyss.size();
        for (int i = 0; i < lenOfress; i++){    
        if (resKeyss.get(i).length()<5)
                {
                    //System.out.println("reskey:" +resKeyss.get(i) + " - "+ resKeyss.get(i).length());
                    res.remove(resKeyss.get(i));
                }}

        HashMap terms = sortedHashMapByValues(res);
        //Object[] keyset = terms.keySet().toArray();
        List<String> perms = new ArrayList<String>(terms.keySet());
        //List<String> perms = (List<String>)(terms.keySet());
        //String[] keys = terms.keySet().toArray(new String[0]);
        Integer lenOfList = perms.size();
        for (int i = 0; i < lenOfList; i++) {
            for (int j = i+1; j < lenOfList; j++){
                //System.out.println(perms.get(i));
                Double sc = score(perms.get(i),perms.get(j));
                if(sc>0.5){
                    System.out.println(perms.get(i)+','+perms.get(j)+':'+score(perms.get(i),perms.get(j)));
                    terms.remove(perms.get(j));}
            }
            }
            System.out.println("Key words in the paper: \n"+ terms.toString());
            String returnString = "Frequent words : \n"+ terms.toString();
            return returnString;
            //String s = terms.entrySet().stream().map(Object::toString).collect(joining("&"));
        }

    public static double score(String first, String second) {
        int maxLength = Math.max(first.length(), second.length());
        //Can't divide by 0
        if (maxLength == 0) return 1.0d;
        return ((double) (maxLength - computeEditDistance(first, second))) / (double) maxLength;
    }

    public static int computeEditDistance(String first, String second) {
        first = first.toLowerCase();
        second = second.toLowerCase();

        int[] costs = new int[second.length() + 1];
        for (int i = 0; i <= first.length(); i++) {
            int previousValue = i;
            for (int j = 0; j <= second.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                }
                else if (j > 0) {
                    int useValue = costs[j - 1];
                    if (first.charAt(i - 1) != second.charAt(j - 1)) {
                        useValue = Math.min(Math.min(useValue, previousValue), costs[j]) + 1;
                    }
                    costs[j - 1] = previousValue;
                    previousValue = useValue;

                }
            }
            if (i > 0) {
                costs[second.length()] = previousValue;
            }
        }
        return costs[second.length()];
    }


    public static HashMap<String, Integer> ngrams(String text, int n) {
        ArrayList<String> words = new ArrayList<String>();
        for(String word : text.split(" ")) {
            words.add(word);
        }

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        int c = words.size();
        for(int i = 0; i < c; i++) {
            if((i + n - 1) < c) {
                int stop = i + n;
                String ngramWords = words.get(i);

                for(int j = i + 1; j < stop; j++) 
                {
                    ngramWords +=" "+ words.get(j);
                }
                map.merge(ngramWords, 1, Integer::sum);
            }
        }

        return map;

    }
    private static HashMap sortedHashMapByValues(Map hashmap) {
        // Create a TreeMap with a custom comparator to sort by values in descending order
        TreeMap<String, Integer> sortedMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                // Sort in descending order
                return Integer.compare((Integer)(hashmap.get(key2)), (Integer) (hashmap.get(key1)));
            }
        });

        // Put all entries from the HashMap into the TreeMap
        sortedMap.putAll(hashmap);

        // Get the top 10 elements
        int count = 0;
        HashMap<String, Integer> SortedList = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            if (count < 11) {
                //System.out.println(entry.getKey() + ": " + entry.getValue());
                SortedList.put(entry.getKey(),entry.getValue());
                count++;
            } else {
                break;
            }

        }
        return SortedList;
    }

    ///////////////////////////////////////////////////
    // GUI Section
    ///////////////////////////////////////////////////
    
    // Create the GUI and add components
    public void initGUI() {
    	// get the device's screen dimensions
    	screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    	screenWidth = (int) screenDimension.getWidth();
    	screenHeight = (int) screenDimension.getHeight();
    	centerX = (int)(screenWidth / 2) - (int) (frameWidth / 2);
    	centerY = (int)(screenHeight / 2) - (int) (frameHeight / 2);
    	
    	// Create and set up the GUI frame
        frame = new JFrame("Lucene Search");
        frame.setSize(new Dimension(frameWidth, frameHeight));
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().setSize(frameWidth, frameHeight);
    	frame.setLocation(centerX, centerY);
    	frame.getContentPane().setLayout(null);
    	
    	panelSearchParent = new JPanel();
    	panelSearchParent.setBounds(0, 0, 961, 38);
    	frame.getContentPane().add(panelSearchParent);
    	
    	lblSearchPanel = new JLabel("Enter Query Text");
    	lblSearchPanel.setFont(new Font("Tahoma", Font.BOLD, 12));
    	lblSearchPanel.setHorizontalAlignment(SwingConstants.LEFT);
    	panelSearchParent.add(lblSearchPanel);
    	
    	tfSearch = new JTextField();
    	tfSearch.setColumns(50);
    	panelSearchParent.add(tfSearch);
    	
    	btnSearch = new JButton("Search");
    	btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 12));
    	panelSearchParent.add(btnSearch);
    	
    	panelResultParent = new JPanel();
    	panelResultParent.setBounds(0, 38, 961, 605);
    	frame.getContentPane().add(panelResultParent);
    	panelResultParent.setLayout(null);
    	
    	panelResultChild1 = new JPanel();
    	panelResultChild1.setBackground(SystemColor.inactiveCaption);
    	panelResultChild1.setBounds(0, 0, 479, 605);
    	panelResultParent.add(panelResultChild1);
    	panelResultChild1.setLayout(null);
    	
    	spListResults = new JScrollPane();
    	spListResults.setBounds(10, 11, 459, 583);
    	panelResultChild1.add(spListResults);
    	
    	listResults = new JList();
    	spListResults.setViewportView(listResults);
    	//listResults.setCellRenderer(new CustomListCellRenderer());
    	listResults.setCellRenderer(new MultilineListCellRenderer());
    	
    	panelResultChild2 = new JPanel();
    	panelResultChild2.setBackground(SystemColor.inactiveCaption);
    	panelResultChild2.setBounds(477, 0, 484, 605);
    	panelResultParent.add(panelResultChild2);
    	panelResultChild2.setLayout(null);
    	
    	spDetailedResults = new JScrollPane();
    	spDetailedResults.setBorder(null);
    	spDetailedResults.setBounds(10, 11, 464, 583);
    	panelResultChild2.add(spDetailedResults);
    	
    	detailedResults = new JTextArea();
    	detailedResults.setBorder(null);
    	spDetailedResults.setViewportView(detailedResults);
    	frame.setVisible(true);
    }
    
    public void printSomething(String x) {
    	System.out.println(x);
    }
    
    // populate the JList with the short result rows
    private void populateResultList() {
    	listResults.setListData(topResultsText);
    }
    
    private void updateSuggestions() {
    	String query = tfSearch.getText();
        //List<Lookup.LookupResult> results = suggester.lookup(query, false, maxSuggestions);
        // Update your UI with the results
    }
    
    // Add all the actionListeners and Eventhandlers when needed or interacted
    private void addActionListeners() {
    	
        // Add documentListener for the Search bar
    	tfSearch.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				updateSuggestions();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				updateSuggestions();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
    	
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
    
    // MouseListener for the resultJList
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
        
    }
    
    public static void main(String[] args) throws Exception{
    	LuceneReadIndexFeature1 lucene = new LuceneReadIndexFeature1();
    	
    	// Create Lucene searcher. It searches over a single IndexReader.
        lucene.initLucene();
        
        //Create GUI
        lucene.initGUI();
        // Add ActionListeners and edit GUI as needed
        lucene.addActionListeners();
    }
}

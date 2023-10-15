package com.howtodoinjava.demo.lucene.file;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.HashSet;



public class SearchHandler<T> implements EventHandler<ActionEvent> {


    private TextArea resultText;
    private TextField searchTextField;

    public SearchHandler(TextField searchTextField, TextArea resultText){
        this.searchTextField = searchTextField;
        this.resultText = resultText;
    }

    public SearchHandler(TextField searchTextField, TextArea resultText, TextField urlTextField, TextField pageTextField) {
        this.searchTextField = searchTextField;
        this.resultText = resultText;
    }

    public void handle(ActionEvent event) {
        
        String searchQuery = searchTextField.getText();

        if (searchQuery.length() == 0) {  // textField does not handle (userInput != null)
            resultText.setText("Please enter a search query");
            return;
        }

        long start = System.currentTimeMillis(); // Search time count start

        try {
            HashSet<String> results;
            results = Searcher.search(searchQuery);
        

        resultText.setText("Search Results for " + searchQuery + ": \n"); //Resets the textArea for new results to be shown

        for(String result: results) // for-each loop through the result and append

            resultText.appendText("-- "+result + "\n\n");

        long time = ((System.currentTimeMillis() - start));  // Search time total ms
        
        resultText.appendText(results.size() + " results in " + time + " millisecond(s).");

        } 
        catch (Exception e) {

            System.out.println("This error again -- Make sure Search method returns a HashSet<String>");
            e.printStackTrace();

        }
}
}

        

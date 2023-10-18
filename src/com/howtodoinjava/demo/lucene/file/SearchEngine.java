package com.howtodoinjava.demo.lucene.file;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SearchEngine extends Application {
    
    Label labelExpl, labelUrlPopup, labelPagePopup, labelSearchPopup;
    TextField SearchTextField, SearchTextFieldPopup, UrlTextField, PagesTextField;
    TextArea resultText;
    Button btn, btnCrawler, btnCrawlerPopup, btnPopupClose;   

    GridPane paneCenterPopup;
    HBox hboxPopup;
    BorderPane borderPopup;
    Scene scenePopup;
    Stage newStage;

    @Override
    public void start(Stage primaryStage) {
        //Objects primaryStage:      
        labelExpl = new Label("Input Search word: ");
        labelExpl.setTextFill(Color.web("#0076a3"));          
        SearchTextField = new TextField(); 
        SearchTextField.setPrefWidth(170);              
        resultText = new TextArea();
        resultText.setText("Search Results: \n");
        resultText.setPrefWidth(700); 
        resultText.setPrefHeight(450);     
        btn = new Button("Search");
        btn.setPrefWidth(170);
        btn.setOnAction(new SearchHandler<ActionEvent>(SearchTextField, resultText));              
        
     
        
        //GridPane (PrimaryStage - border.top)
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.BOTTOM_LEFT);
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPadding(new Insets(5, 0, 0, 45));
        pane.setBorder(Border.EMPTY);  
        //Add all elements to pane (into grid)
        //GridLayout(int rows, int columns, int horizontalGap, int verticalGap)
        pane.add(labelExpl,0,0,2,1);
        pane.add(SearchTextField,0,1,2,2);
    
        //GridPane (PrimaryStage - border.center)
        GridPane paneCenter = new GridPane();
        //paneCenter.setAlignment();
        paneCenter.setHgap(10);
        paneCenter.setVgap(10);
        paneCenter.setPadding(new Insets(0, 0, 0, 45));
        paneCenter.setBorder(Border.EMPTY);
        //Add all elements to pane (into grid)
        //GridLayout(int rows, int columns, int horizontalGap, int verticalGap)  
        paneCenter.add(resultText,0,1,4,4);
        

        
        //HBox (PrimaryStage - scene.border.bottom)
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 10, 10, 45));
        hbox.setSpacing(110);
        hbox.getChildren().addAll(btn);
        

        //BorderPane (PrimaryStage - scene.border)
        BorderPane border = new BorderPane();
        border.setBottom(hbox); //add hbox to border from method
        border.setTop(pane); //add grid to border from method
        border.setCenter(paneCenter);
        

   
        //Scene: (PrimaryStage - primaryStage.scene)
        //Scene scene = new Scene(border, 550, 300);
        Scene scene = new Scene(border, 800, 600);
        

   
        //Stage: (PrimaryStage - primaryStage)
        primaryStage = new Stage(); // initilized in this method: public void start(Stage primaryStage) 
        primaryStage.setTitle("Java Search Engine");
        primaryStage.setScene(scene); // Show Scene
        primaryStage.show();
    }

    public static void main (String[] args) throws IOException {      
        launch(args);
        
    }
}
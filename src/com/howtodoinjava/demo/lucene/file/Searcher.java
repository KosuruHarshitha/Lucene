package com.howtodoinjava.demo.lucene.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
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
            
            String keywordsString = getKeywords(d.get("contents"));      
            System.out.println("Path: " + d.get("path") + ", Score: " + sd.score + "\n");
            results.add((parts[0].toString().substring(2))+keywordsString);
            //System.out.println(d.get("title"));
            }
        return results;
    }
     
    public static String getKeywords(String content) throws IOException {
        final List<String> stopwords;
        stopwords = Files.readAllLines(Paths.get("/english_stopwords.txt"));
        String stopwordsRegex = stopwords.stream()
            .collect(Collectors.joining("|", "\\b(", ")\\b\\s?"));
        String refined = content.replaceAll("[^a-zA-Z]", " ");
        String result = refined.toLowerCase().replaceAll(stopwordsRegex, "");
        HashMap<String, Integer> res = ngrams(result.toString(),2); 
        res.remove(" ");
        List<String> resKeys = new ArrayList<String>(res.keySet());
        Integer lenOfres = resKeys.size();
        for (int i = 0; i < lenOfres; i++) {
                //System.out.println("reskey:" +resKeys.get(i) + " - "+ resKeys.get(i).length());
                List<String> items = Arrays. asList(resKeys.get(i). split("\\s*,\\s*"));
                for (int j = 0; j < items.size(); j++){
                    if(items.get(j).length()<2){
                        res.remove(resKeys.get(i));}
                }
                
            }
        List<String> resKeyss = new ArrayList<String>(res.keySet());
        Integer lenOfress = resKeyss.size();
        for (int i = 0; i < lenOfress; i++){    
        if (resKeyss.get(i).length()<5)
                {
                    System.out.println("reskey:" +resKeyss.get(i) + " - "+ resKeyss.get(i).length());
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
                if(sc>0.6){
                    System.out.println(perms.get(i)+','+perms.get(j)+':'+score(perms.get(i),perms.get(j)));
                    terms.remove(perms.get(j));}
            }
            }
            System.out.println("Key words in the paper: \n"+ terms.toString());
            String returnString = "Key words in the paper: \n"+ terms.toString();
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

                for(int j = i + 1; j < stop; j++) {
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
            if (count < 5) {
                //System.out.println(entry.getKey() + ": " + entry.getValue());
                SortedList.put(entry.getKey(),entry.getValue());
                count++;
            } else {
                break;
            }

        }
        return SortedList;
    }
   }

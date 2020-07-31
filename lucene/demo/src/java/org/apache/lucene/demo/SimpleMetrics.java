package org.apache.lucene.demo;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;



public class SimpleMetrics {

    private int docFrequency;
    private int termFrequency;
    private String term;

    IndexReader reader;

    //public SimpleMetrics(){}
    public static void main(String[] args) throws Exception {
        String usage =
                "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
        if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(usage);
            System.exit(0);
        }

        String index = "index";
        String field = "contents";
        String queries = null;
        int repeat = 0;
        boolean raw = false;
        String queryString = null;
        int hitsPerPage = 10;

        for(int i = 0;i < args.length;i++) {
            if ("-index".equals(args[i])) {
                index = args[i+1];
                i++;
            } else if ("-field".equals(args[i])) {
                field = args[i+1];
                i++;
            } else if ("-queries".equals(args[i])) {
                queries = args[i+1];
                i++;
            } else if ("-query".equals(args[i])) {
                queryString = args[i+1];
                i++;
            } else if ("-repeat".equals(args[i])) {
                repeat = Integer.parseInt(args[i+1]);
                i++;
            } else if ("-raw".equals(args[i])) {
                raw = true;
            } else if ("-paging".equals(args[i])) {
                hitsPerPage = Integer.parseInt(args[i+1]);
                if (hitsPerPage <= 0) {
                    System.err.println("There must be at least 1 hit per page.");
                    System.exit(1);
                }
                i++;
            }
        }

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new CMPT456Analyzer();

        BufferedReader in = null;
        if (queries != null) {
            in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
        } else {
            in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        }
        QueryParser parser = new QueryParser(field, analyzer);
        while (true) {
            if (queries == null && queryString == null) {                        // prompt the user
                System.out.println("Enter query: ");
            }

            String line = queryString != null ? queryString : in.readLine();
            if (line == null || line.length() == -1) {
                break;
            }

            line = line.trim();
            if (line.length() == 0) {
                break;
            }


            SimpleMetrics a = new SimpleMetrics(line, reader);
            int docFreq = a.getDocFrequency();
            long termFreq = a.getTermFrequency();
            System.out.println(String.format("Term frequency of %s is %d", line, termFreq));
            System.out.println(String.format("Document frequency of %s is %d", line, docFreq));
        }
    }

    public SimpleMetrics(String query,IndexReader read){
        term = query;
        reader = read;
    }

    public int getDocFrequency(){
        // public abstract int docFreq(Term term) throws IOException;
        Term termInstance = new Term("contents",term);
        int docFreq = 0;
        try {
            docFreq = reader.docFreq(termInstance);
        }    catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }

        return docFreq;
    }
    public long getTermFrequency(){
        Term termInstance = new Term("contents",term);
        long termFreq = 0;
        try {
            termFreq = reader.totalTermFreq(termInstance);
        }    catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }

        return termFreq;
    }


}
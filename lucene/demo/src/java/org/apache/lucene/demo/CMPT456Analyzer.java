/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.demo;


import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.io.*;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.analysis.tokenattributes;
import org.apache.lucene.util.AttributeSource;



import org.apache.lucene.analysis.en.PorterStemFilter;


import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import java.util.List;
import java.util.*;



public final class CMPT456Analyzer extends StopwordAnalyzerBase {

    public static final CharArraySet ENGLISH_STOP_WORDS_SET;

    static {

        List<String> stopWords = new ArrayList<String>();
        String filename = "lucene/demo/src/java/org/apache/lucene/demo/stopwords.txt";
        try{
            stopWords = Files.readAllLines(Paths.get(filename));
        }
        catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }

        final CharArraySet stopSet = new CharArraySet(stopWords, false);
        ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);

    }

    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;


    public static final CharArraySet STOP_WORDS_SET = ENGLISH_STOP_WORDS_SET;


    public CMPT456Analyzer(CharArraySet stopWords) {
        super(stopWords);
    }

    public CMPT456Analyzer() {
        this(STOP_WORDS_SET);
    }

    public CMPT456Analyzer(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }


    public void setMaxTokenLength(int length) {
        maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        src.setMaxTokenLength(maxTokenLength);
        TokenStream tok = new StandardFilter(src);
        tok = new LowerCaseFilter(tok);
        tok = new StopFilter(tok, stopwords);
//        StringReader reader = new StringReader(str);

////        TokenStream tok  = analyzer.tokenStream(null, new StringReader(text));
//        try {
//            CharTermAttribute cattr = tok.addAttribute(CharTermAttribute.class);
//            tok.reset();
//            while (tok.incrementToken()) {
//                System.out.println(cattr.toString());
//            }
//
//            tok.end();
//            tok.close();
//
//        }catch (IOException e){
//            System.out.println(" caught a " + e.getClass() +
//                    "\n with message: " + e.getMessage());
//        }

//        OffsetAttribute offsetAtt = tok.addAttribute(OffsetAttribute.class);
//        try {
//            tok.reset(); // Resets this stream to the beginning. (Required)
//            while (tok.incrementToken()) {
//                // Use AttributeSource.reflectAsString(boolean)
//                // for token stream debugging.
//                System.out.println("token: " + tok.reflectAsString(true));
//
//                System.out.println("token start offset: " + offsetAtt.startOffset());
//                System.out.println("  token end offset: " + offsetAtt.endOffset());
//            }
//            tok.end();   // Perform end-of-stream operations, e.g. set the final offset.
//        } finally {
//            tok.close(); // Release resources associated with this stream.
//        }

        //Porter stemming-not accurate****
        tok = new PorterStemFilter(tok);



        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) {
                // So that if maxTokenLength was changed, the change takes
                // effect next time tokenStream is called:
                src.setMaxTokenLength(CMPT456Analyzer.this.maxTokenLength);
                super.setReader(reader);
            }
        };
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        TokenStream result = new StandardFilter(in);
        result = new LowerCaseFilter(result);
        return result;
    }
}

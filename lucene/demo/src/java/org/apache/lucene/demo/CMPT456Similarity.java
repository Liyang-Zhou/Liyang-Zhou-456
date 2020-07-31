package org.apache.lucene.demo;

import org.apache.lucene.search.similarities.ClassicSimilarity;

public class CMPT456Similarity extends ClassicSimilarity{

    @Override
    public float tf(float freq) {
        return (float)Math.sqrt(freq + 1.0);
    }
    @Override
    public float idf(long docFreq, long docCount) {
        return (float)((Math.log((docCount+2)/(double)(docFreq+2))) + 1.0);
    }

}
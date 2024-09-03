package com.eCommerce.helper;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;

public class ProcessText {

    public static String processText(Analyzer analyzer, String text) throws IOException {
        StringBuilder result = new StringBuilder();
        try (TokenStream stream = analyzer.tokenStream(null, new StringReader(text))) {
            stream.reset();
            CharTermAttribute termAtt = stream.getAttribute(CharTermAttribute.class);
            while (stream.incrementToken()) {
                String term = termAtt.toString();
                result.append(term).append(" ");
            }
            stream.end();
        }
        return result.toString().trim();
    }

}

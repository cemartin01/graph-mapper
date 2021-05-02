package io.github.cemartin01.graphmapper.graphql.schema;

import graphql.language.Document;
import graphql.parser.Parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class QueryLoader {

    public static Document loadQuery(String name) {

        try(InputStream stream = QueryLoader.class.getClassLoader().getResourceAsStream(name + ".graphql");
            Reader reader = new InputStreamReader(stream)) {
            return new Parser().parseDocument(reader);
        } catch (Exception e) {
            throw new IllegalArgumentException("query" + name + ".graphql does not exist");
        }
    }

}

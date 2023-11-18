package graphql4smr.lib;

import graphql4smr.lib.authorbook.Author;
import graphql4smr.lib.authorbook.AuthorBook;
import graphql4smr.lib.authorbook.Book;
import graphql4smr.lib.util.Util;
import org.apache.commons.text.StringSubstitutor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class FakerExampleText {

    public String include_str(String path){return Util.include_str(getClass(), path);}


    @Test
    public void exampletest() {
        assertTrue("testing something", true);
    }

    @Test public void exampletest1() {


        FakerExample.createdata();

        AuthorBook authorbook = new AuthorBook();

        String bookid = Book.books.get(0).getId();
        String querytemplate = include_str("authorbooktestquerytemplateid.txt");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("id", bookid);
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String query = sub.replace(querytemplate);

        System.out.println(query);

        System.out.println(authorbook.request(query));
        assertTrue("testing something", true);
    }


}

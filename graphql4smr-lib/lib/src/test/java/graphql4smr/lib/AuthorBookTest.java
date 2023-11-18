package graphql4smr.lib;

import graphql4smr.lib.authorbook.AuthorBook;
import graphql4smr.lib.util.Util;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AuthorBookTest {

    public String include_str(String path){return Util.include_str(getClass(), path);}

    @Test
    public void exampletest() {
        assertTrue("testing something", true);
    }


    @Test public void exampletest1() {
        AuthorBook authorbook = new AuthorBook();
        String query = include_str("authorbooktestquery.txt");
        System.out.println(authorbook.request(query));
     }


}

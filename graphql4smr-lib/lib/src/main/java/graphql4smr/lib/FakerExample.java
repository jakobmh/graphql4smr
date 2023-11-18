package graphql4smr.lib;

import com.github.javafaker.Faker;
import graphql4smr.lib.authorbook.Author;
import graphql4smr.lib.authorbook.AuthorBook;
import graphql4smr.lib.authorbook.Book;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class FakerExample {

    public static void main(String[] args) {

    }

    public static void createdata(){
        Faker faker = new Faker();

        String name = faker.name().fullName(); // Miss Samanta Schmidt
        String firstName = faker.name().firstName(); // Emory
        String lastName = faker.name().lastName(); // Barton

        String streetAddress = faker.address().streetAddress();

        System.out.println(UUID.randomUUID());
        System.out.println(faker.internet().domainName());
        System.out.println(faker.random().nextLong());


        System.out.println(UUID.randomUUID());
        System.out.println(faker.name().name());
        System.out.println(faker.company().bs());

        AuthorBook authorBook = new AuthorBook();

        List<Author> authors = new ArrayList<>();
        List<Book> books = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            String uuidauthor = UUID.randomUUID().toString();
            for (int k = 0; k < 3; k++) {
                books.add(new Book(
                        UUID.randomUUID().toString(),
                        faker.internet().domainName(),
                        faker.random().nextInt(2000),
                        uuidauthor
                ));
            }
            authors.add(new Author(
                    uuidauthor,
                    faker.name().name(),
                    faker.company().bs()
            ));

        }

        Author.authors = authors;
        Book.books = books;
    }
}

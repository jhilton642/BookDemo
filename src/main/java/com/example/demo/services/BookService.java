package com.example.demo.services;

import com.example.demo.models.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {
/**
 * 		ATTENTION	BookService
 * 				These are the APIs we expose to the Controllers
 * 				We pretty much have all of them enable. Frequently you would change how the delete code works
 * 				probably by setting an 'active' attribute to false and saving the record and NOT deleting anything
 *          These lines of code to a great deal of work
 *          We extend the CrudRepository which makes this object knowledgeable about
 *              how to add, find, remove records froms a database
 *          The @Service tells SpringBoot that it is able to handle requests to interface with the database
 *          We make it an interface which means someone needs to supply lots of code to fill in the missing methods
 *              SpringBoot does that for us. SpringBoot will see we need find, create, delete, update code
 *              for a Book class and it will generate them
 *
 */
    @Autowired
    private BookRepository bookRepository;

    public Iterable<Book> listAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Integer id) {
        Optional<Book> oBook = bookRepository.findById(id);
        return oBook.orElse(null);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Integer id) {
        Optional<Book> oBook = bookRepository.findById(id);
        oBook.ifPresent(book -> bookRepository.delete(book));
    }

    public Book add(Book book) {
        return bookRepository.save(book);
    }

    public Iterable<Book> findByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    public Iterable<Book> findByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }
}

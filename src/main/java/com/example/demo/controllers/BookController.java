package com.example.demo.controllers;

import com.example.demo.models.Book;
import com.example.demo.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")            //  this will add books to the start of all URL endpoints
public class BookController {

    @Autowired
    //	@Autowired will request SpringBoot to find the BookService class and instantiate one for us
    //	and assign (INJECT) the class property with the value. This is Dependency Injection.
    //	our class depends on this service and SpringBoot will inject it into our class
    private BookService bookService;

    @RequestMapping("/")                                    //  this code will be reached by /books/
    public String index(Model model) {
        //  get a list of all books add to the model and list them
        Iterable<Book> books = bookService.listAllBooks();
        model.addAttribute("books", books);

        //  the the bookList page will be happy to display it
        return "bookList";
    }

    //  let's CREATE a new book
    @RequestMapping("/new")
    public String newCustomer(Model model){
        //  since we do not have a book, let's send an empty book to the bookEdit page
        model.addAttribute("book", new Book());
        return "bookEdit";
    }

    //  id will be the key to the book we want to READ from the database
    @RequestMapping("/{id}")
    public String read(@PathVariable Integer id, Model model){
        //  find in the database a book with id = to our PathVariable
        Book book = bookService.getBookById(id);

        //  did we find a book?
        if ( book != null ) {
            //  yes. add the book to the model and display the bookDetails page
            model.addAttribute("book", book);
            return "bookDetails";
        }
        else {
            //  no, we did not find a book. Display an error message
            model.addAttribute("message", "The Book Id: " + id + " was not found in the database");
            return "404";       //  book (page) not found
        }
    }

    //  id will be the key to the book we want to UPDATE
    @RequestMapping("/edit/{id}")
    public String update(@PathVariable Integer id, Model model){
        //  find the book in the database and send that data to the bookEdit page
        model.addAttribute("book", bookService.getBookById(id));
        return "bookEdit";
    }

    //  we have finished making our changes to our book. The data is POSTed back to the server
    //  all of the data is saved in a Book object and UPDATEd in the database.
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(Book book){
        //  all we have to do is save the book
        bookService.saveBook(book);
        //  go to the list all books page when complete
        return "redirect:/books/";
    }

    //  using the id from the URL find and DELETE our book
    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable Integer id){
        bookService.deleteBook(id);
        //  go to the list all books page when complete
        return "redirect:/books/";
    }

    //  using the author from the search form get all books by this author
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestParam String author, Model model ){
        //  SEARCH for all books by author
        Iterable<Book> list = bookService.findByAuthor(author);

        //  pass the list of books by author
        model.addAttribute("author", author);
        model.addAttribute("books", list);

        //  the the bookList page will be happy to display it
        return "bookList";
    }

    //  using the author from the search form get all books by this author
    @RequestMapping(value = "/title", method = RequestMethod.POST)
    public String searchTitle(@RequestParam String title, Model model ){
        //  SEARCH for all books by author
        Iterable<Book> list = bookService.findByTitle(title);

        //  pass the list of books by title back to the web page
        model.addAttribute("title", title);
        model.addAttribute("books", list);

        //  the the bookList page will be happy to display it
        return "bookList";
    }

    //  create a random book and add to the database
    @RequestMapping("/add")                                    //  this code will be reached by /books/add
    public String add(Model model) {
        //  create lists of random titles and things and authors
        String[] titles = {"Master", "Ruler", "Lord", "King", "Programmer", "Dude"};
        String[] things = {"Rings", "Kitchen", "Code", "Keyboard", "Debugger"};
        String[] authors = {"Tolkien", "Heinlein", "Asimov", "Adams", "Clarke"};
        String title, thing, author;

        //  randomly create choose a title, object and author from our lists
        title = titles[(int)(Math.random()*titles.length)];                   //  choose a random title
        thing = things[(int)(Math.random()*things.length)];               //  choose a random object
        author = authors[(int)(Math.random()*authors.length)];          //  choose a random author
        int numberOfPages = (int)(Math.random()*600) + 100;             //  make book at least 100 page but less than 700

        //  create a book from these random pieces and display the list of books
        Book book = new Book(title + " of the " + thing, author, numberOfPages);
        bookService.add(book);
        return "redirect:/books/";
    }
}

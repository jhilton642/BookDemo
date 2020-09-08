package com.example.demo.repository;

import com.example.demo.models.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TODO    Repository
 *  The @Repository annotation does not DO a lot currently. It is included in all of our repositories to
 *  announce exactly what that object is.
 *  <p>
 *  By extending the CrudRepository interface we enable this interface with all of the rights, powers, and
 *  authority of a CRUD empowered data access object.
 *  The CrudRepository has methods for
 *  count, delete, deleteAll, existsById, findAll, findAllById, findById, save and saveAll
 *  Spring is clever enough to create methods for us, as long as we follow certain patterns.
 *  For details read this page
 *  https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 *  We added to this interface findByCategory. Spring will create a method and SELECT statement to do just
 *  what you would expect - find Book records that match the desired category.
 */
@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
    List<Book> findByAuthor(String author);
    List<Book> findByTitleContaining(String title);
}

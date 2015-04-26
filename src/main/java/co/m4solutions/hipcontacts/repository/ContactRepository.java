package co.m4solutions.hipcontacts.repository;

import co.m4solutions.hipcontacts.domain.Contact;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Contact entity.
 */
public interface ContactRepository extends JpaRepository<Contact,Long> {

    @Query("select contact from Contact contact where contact.user.login = ?#{principal.username}")
    List<Contact> findAllForCurrentUser();

}

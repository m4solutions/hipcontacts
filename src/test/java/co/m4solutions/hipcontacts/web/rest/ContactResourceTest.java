package co.m4solutions.hipcontacts.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import co.m4solutions.hipcontacts.Application;
import co.m4solutions.hipcontacts.domain.Contact;
import co.m4solutions.hipcontacts.repository.ContactRepository;

/**
 * Test class for the ContactResource REST controller.
 *
 * @see ContactResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ContactResourceTest {

    private static final String DEFAULT_FIRST_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_FIRST_NAME = "UPDATED_TEXT";
    private static final String INVALID_FIRST_NAME = "INVALIDA_TEXTINVALIDA_TEXTINVALIDA_TEXTINVALIDA_TEXTINVALIDA_TEXTINVALIDA_TEXT";
    private static final String DEFAULT_LAST_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_LAST_NAME = "UPDATED_TEXT";

    private static final Long DEFAULT_MOBILE = 0L;
    private static final Long UPDATED_MOBILE = 1L;
    private static final Long INVALID_MOBILE = 123456789012345678L;
    private static final String DEFAULT_EMAIL = "SAMPLE@EMAIL.COM";
    private static final String UPDATED_EMAIL = "UPDATED@EMAIL.COM";

    @Inject
    private ContactRepository contactRepository;

    private MockMvc restContactMockMvc;

    private Contact contact;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContactResource contactResource = new ContactResource();
        ReflectionTestUtils.setField(contactResource, "contactRepository", contactRepository);
        this.restContactMockMvc = MockMvcBuilders.standaloneSetup(contactResource).build();
    }

    @Before
    public void initTest() {
        contact = new Contact();
        contact.setFirstName(DEFAULT_FIRST_NAME);
        contact.setLastName(DEFAULT_LAST_NAME);
        contact.setMobile(DEFAULT_MOBILE);
        contact.setEmail(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    public void createContact() throws Exception {
        int databaseSizeBeforeCreate = contactRepository.findAll().size();

        // Create the Contact
         restContactMockMvc.perform(post("/api/contacts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contact)))
                .andExpect(status().isCreated());

        // Validate the Contact in the database
        List<Contact> contacts = contactRepository.findAll();
        assertThat(contacts).hasSize(databaseSizeBeforeCreate + 1);
        Contact testContact = contacts.get(contacts.size() - 1);
        assertThat(testContact.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testContact.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testContact.getMobile()).isEqualTo(DEFAULT_MOBILE);
        assertThat(testContact.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    public void checkFirstNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(contactRepository.findAll()).hasSize(0);
        // set the field null
        contact.setFirstName(null);

        // Create the Contact, which fails.
        restContactMockMvc.perform(post("/api/contacts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contact)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Contact> contacts = contactRepository.findAll();
        assertThat(contacts).hasSize(0);
    }
    
    /**
     * @author marcus.sanchez
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkFirstNameMaxLenght25() throws Exception {
        // Validate the database is empty
        assertThat(contactRepository.findAll()).hasSize(0);
        // set INVALID lengt field value
        contact.setFirstName(INVALID_FIRST_NAME);

        // Create the Contact, which fails.
        restContactMockMvc.perform(post("/api/contacts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contact)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Contact> contacts = contactRepository.findAll();
        assertThat(contacts).hasSize(0);
    }
    
    /**
     * @author marcus.sanchez
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkMobileMaxLenght15() throws Exception {
        // Validate the database is empty
        assertThat(contactRepository.findAll()).hasSize(0);
        // set INVALID lengt field value
        contact.setMobile(INVALID_MOBILE);

        // Create the Contact, which fails.
        restContactMockMvc.perform(post("/api/contacts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contact)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Contact> contacts = contactRepository.findAll();
        assertThat(contacts).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllContacts() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contacts
        restContactMockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(contact.getId().intValue())))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
                .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.intValue())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())));
    }

    @Test
    @Transactional
    public void getContact() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get the contact
        restContactMockMvc.perform(get("/api/contacts/{id}", contact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(contact.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE.intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingContact() throws Exception {
        // Get the contact
        restContactMockMvc.perform(get("/api/contacts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateContact() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);
		
		int databaseSizeBeforeUpdate = contactRepository.findAll().size();

        // Update the contact
        contact.setFirstName(UPDATED_FIRST_NAME);
        contact.setLastName(UPDATED_LAST_NAME);
        contact.setMobile(UPDATED_MOBILE);
        contact.setEmail(UPDATED_EMAIL);
        restContactMockMvc.perform(put("/api/contacts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contact)))
                .andExpect(status().isOk());

        // Validate the Contact in the database
        List<Contact> contacts = contactRepository.findAll();
        assertThat(contacts).hasSize(databaseSizeBeforeUpdate);
        Contact testContact = contacts.get(contacts.size() - 1);
        assertThat(testContact.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testContact.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testContact.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testContact.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void deleteContact() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);
		
		int databaseSizeBeforeDelete = contactRepository.findAll().size();

        // Get the contact
        restContactMockMvc.perform(delete("/api/contacts/{id}", contact.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Contact> contacts = contactRepository.findAll();
        assertThat(contacts).hasSize(databaseSizeBeforeDelete - 1);
    }
    
    /**
     * @author marcus.sanchez
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteNonExistentContact() throws Exception {
    	//Arrange
        // Initialize the database
        contactRepository.saveAndFlush(contact);
		
		int databaseSizeBeforeDelete = contactRepository.findAll().size();

        // ACT Get the contact
		try{
			restContactMockMvc.perform(delete("/api/contacts/{id}", Long.MAX_VALUE));
		}catch(Exception e){
			//Assert
			assertTrue(e.getCause().getMessage().contains("Contact entity with id "+Long.MAX_VALUE+" exists!"));	
		}
		assertEquals(databaseSizeBeforeDelete, contactRepository.findAll().size());
    }
}

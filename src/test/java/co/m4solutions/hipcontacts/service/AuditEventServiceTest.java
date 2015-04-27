package co.m4solutions.hipcontacts.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.audit.AuditEvent;

import co.m4solutions.hipcontacts.config.audit.AuditEventConverter;
import co.m4solutions.hipcontacts.domain.PersistentAuditEvent;
import co.m4solutions.hipcontacts.repository.PersistenceAuditEventRepository;

/**
 * 
 * @author marcus.sanchez
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AuditEventServiceTest {
	
    @Mock
    private PersistenceAuditEventRepository persistenceAuditEventRepository;

    @Mock
    private AuditEventConverter auditEventConverter;
    
    @InjectMocks
    private AuditEventService auditEventService;

	@Test
	public void testFindAll() {
		//Arrange 
		List<PersistentAuditEvent> persistentAuditEventList = new ArrayList<>();
		persistentAuditEventList.add(new PersistentAuditEvent());
		persistentAuditEventList.add(new PersistentAuditEvent());
		doReturn(persistentAuditEventList).when(persistenceAuditEventRepository).findAll();
		
		List<AuditEvent> auditEventList = new ArrayList<AuditEvent>();
		auditEventList.add(new AuditEvent("principal", "type", "data"));
		auditEventList.add(new AuditEvent("principal", "type", "data"));
		when(auditEventConverter.convertToAuditEvent(persistentAuditEventList)).thenReturn(auditEventList);
		
		//Act
		List<AuditEvent> respAuditEventList = auditEventService.findAll();
		
		//Assert
		assertEquals(respAuditEventList, auditEventList);
	}

}

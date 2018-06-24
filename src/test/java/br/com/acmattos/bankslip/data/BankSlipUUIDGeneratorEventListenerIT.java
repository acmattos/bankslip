package br.com.acmattos.bankslip.data;

import br.com.acmattos.bankslip.util.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * BankSlipUUIDGeneratorEventListener Integration Tests.
 * @author acmattos
 */
public class BankSlipUUIDGeneratorEventListenerIT extends IntegrationTest {
   @Autowired
   private BankSlipRepository repository;
   private BankSlip entity;
   
   @Before
   public void setUp() throws Exception {
      this.entity = BankSlip.builder()
         .dueDate(new Date())
         .totalInCents(new BigDecimal("100000"))
         .customer("Customer")
         .status(BankSlipStatusEnum.PENDING)
         .build();
   }
   
   @After
   public void tearDown() throws Exception {
      this.repository.deleteAll();
   }
   
   @Test
   public void onBeforeConvert() throws Exception {
      List<BankSlip> bankSlips = repository.findAll();
      assertTrue("There is no bank slips in database now...",
         bankSlips.isEmpty());
      
      assertTrue("We've just created a new entity.",
         this.entity.isNew());
      
      repository.save(this.entity);
      assertFalse("After saving it, we can see that's not new anymore",
         this.entity.isNew());
   
      bankSlips = repository.findAll();
      assertFalse("And this entity is stored in database",
         bankSlips.isEmpty());
      
      BankSlip bankSlip = repository.findById(this.entity.getId()).get();
      assertFalse("After retrieving it by UUID, we confirm it's not new...",
         bankSlip.isNew());
      assertEquals("...and the ID is the same...",
         bankSlip.getId(), this.entity.getId());
      assertEquals("... and the status is PENDING for both",
         bankSlip.getStatus(), this.entity.getStatus());
      
      bankSlip.setStatus(BankSlipStatusEnum.PAID);
      bankSlip = repository.save(bankSlip);
      assertEquals("Now we've changed the status for PAID...",
         bankSlip, this.entity);
      assertEquals("...as equals() only compares entities by ID....",
         bankSlip.getId(), this.entity.getId());
      assertNotEquals("...after save one copy, the other contains a different status...",
         bankSlip.getStatus(), this.entity.getStatus());
      assertEquals("...and we'll get TRUE to equals(), although one status is PAID..",
         BankSlipStatusEnum.PAID, bankSlip.getStatus());
      assertEquals("...while the other status is PENDING.",
         BankSlipStatusEnum.PENDING, this.entity.getStatus());
   }
}
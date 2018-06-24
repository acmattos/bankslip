package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlip;
import br.com.acmattos.bankslip.data.BankSlipStatusEnum;
import br.com.acmattos.bankslip.util.UnitTest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * NewBankSlipDTO Unit Tests.
 * @author acmattos
 */
public class NewBankSlipDTOUT extends UnitTest {
   
   private Date date;
   private NewBankSlipDTO dto;
   
   @Before
   public void setUp() throws Exception {
      this.date = new Date();
      this.dto = NewBankSlipDTO.builder()
         .dueDate(this.date)
         .totalInCents(BigDecimal.TEN)
         .customer("Customer")
         .status(BankSlipStatusEnum.PENDING)
         .build();
   }
   
   @Test
   public void validateMe_DtoIsInvalid() {
      NewBankSlipDTO dto = new NewBankSlipDTO();
      
      Map<String, String> errorMap = dto.validateMe();
      
      assertNotNull("Can't be null!", errorMap);
      assertFalse("Must not be empty!", errorMap.isEmpty());
      assertNotNull("dueDate Can't be null!", errorMap.get("dueDate"));
      assertNotNull("totalInCents Can't be null or below zero!", errorMap.get("totalInCents"));
      assertNotNull("customer Can't be null or empty!", errorMap.get("customer"));
      assertNotNull("status Can't be null!", errorMap.get("status"));
   }
   
   @Test
   public void validateMe_DtoIsValid() {

      Map<String, String> errorMap = this.dto.validateMe();
      
      assertNotNull("Can't be null!", errorMap);
      assertTrue("Must be empty!", errorMap.isEmpty());
   }

   @Test
   public void toEntity() {
      BankSlip entity = dto.toEntity();
      
      assertNotNull("Can't be null!", entity);
      assertEquals(dto.getDueDate(), entity.getDueDate());
      assertEquals(dto.getTotalInCents(), entity.getTotalInCents());
      assertEquals(dto.getCustomer(), entity.getCustomer());
      assertEquals(dto.getStatus(), entity.getStatus());
   }

   @Test
   public void toStringTest() {
      assertEquals("NewBankSlipDTO(super=AbstractBankSlipDTO(dueDate="
         + this.date
         + ", totalInCents=10, customer=Customer), status=PENDING)", this.dto.toString());
   }
}
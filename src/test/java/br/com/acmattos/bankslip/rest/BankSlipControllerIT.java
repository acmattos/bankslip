package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlip;
import br.com.acmattos.bankslip.data.BankSlipRepository;
import br.com.acmattos.bankslip.data.BankSlipStatusEnum;
import br.com.acmattos.bankslip.util.IntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static br.com.acmattos.bankslip.rest.BankSlipControllerUT.RESOURCE_URL;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


/**
 * BankSlipController Integration Tests.
 * @author acmattos
 */
public class BankSlipControllerIT extends IntegrationTest {
   @Autowired
   private BankSlipController controller;
   
   @Autowired
   private BankSlipRepository repository;
   
   @Autowired
   private ObjectMapper objectMapper;
   
   private MockMvc mvc;
   private BankSlip entity;
   private Date dueDate;
   
   @Before
   public void setUp() throws Exception {
      this.mvc = MockMvcBuilders.standaloneSetup(controller)
         .setControllerAdvice(new BankSlipExceptionHandlerAdvice())
         .build();
      this.dueDate = new Date();
      this.entity = BankSlip.builder()
         .dueDate(this.dueDate)
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
   public void create() throws Exception {
      List<BankSlip> bankSlips = repository.findAll();
      assertTrue("There is no BankSlip in database at this moment...",
         bankSlips.isEmpty());
      
      MvcResult result = this.mvc.perform(
         post(RESOURCE_URL)
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(this.objectMapper.writeValueAsString(this.entity))
      )
      .andReturn()
      ;
      bankSlips = repository.findAll();
      assertFalse(
         "After perform a POST request, now we have a BankSlip in database...",
         bankSlips.isEmpty());
      
      assertTrue("... and the HTTP Status is 201.",
         201 == result.getResponse().getStatus());
      
      assertNull("Request Bank Slip does not have ID...",
         this.entity.getId());
      
      assertNotNull("... but saved one has!", bankSlips.get(0).getId());
   }
   
   @Test
   public void findAll() throws Exception {
      List<BankSlip> bankSlips = repository.findAll();
      assertTrue("There is no BankSlip in database at this moment...",
         bankSlips.isEmpty());
   
      MvcResult result = this.mvc.perform(
         get(RESOURCE_URL)
      )
      .andReturn()
      ;
      assertTrue("As we can confirm now: EMPTY ARRAY",
         result.getResponse().getContentAsString().contains("[]"));
      
      assertTrue("And the HTTP Status is 404",
         404 == result.getResponse().getStatus());
   
      repository.save(this.entity);
      bankSlips = repository.findAll();
      assertFalse("Now, we've just saved an entity...",
         bankSlips.isEmpty());
   
      result = this.mvc.perform(
         get(RESOURCE_URL)
      )
      .andReturn()
      ;
      assertTrue("After call endpoint, we can see that it has changed...",
         result.getResponse().getContentAsString().contains("[{\"dueDate\":\""));
   
      assertTrue("And the HTTP Status is now 200",
         200 == result.getResponse().getStatus());
   }
   
   @Test
   public void findById() throws Exception {
      List<BankSlip> bankSlips = repository.findAll();
      assertTrue("There is no BankSlip in database at this moment...",
         bankSlips.isEmpty());
   
      repository.save(this.entity);
      bankSlips = repository.findAll();
      assertFalse("Now, we've just saved an entity...",
         bankSlips.isEmpty());
   
      MvcResult result = this.mvc.perform(
         get(RESOURCE_URL + "/" + bankSlips.get(0).getId())
      )
      .andReturn()
      ;
   
      assertTrue("After call endpoint, we can see that an entity could be found.",
         result.getResponse().getContentAsString().contains("{\"dueDate\":\""));
   
      assertTrue("And the HTTP Status is 200",
         200 == result.getResponse().getStatus());
   }
   
   @Test
   public void payOrCancelOne() throws Exception {
      List<BankSlip> bankSlips = repository.findAll();
      assertTrue("There is no BankSlip in database at this moment...",
         bankSlips.isEmpty());
   
      repository.save(this.entity);
      bankSlips = repository.findAll();
      assertFalse("Now, we've just saved an entity...",
         bankSlips.isEmpty());
   
      assertTrue("... and its status is PENDING!",
         BankSlipStatusEnum.PENDING.equals(bankSlips.get(0).getStatus()));
      
      MvcResult result = this.mvc.perform(
         put(RESOURCE_URL + "/" + bankSlips.get(0).getId())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(this.objectMapper.writeValueAsString(
            UpdatedBankSlipStatusDTO.builder().status(BankSlipStatusEnum.PAID).build()))
      )
      .andReturn()
      ;
      bankSlips = repository.findAll();
      assertTrue("After endpoint's call, the status has changed to PAID!",
         BankSlipStatusEnum.PAID.equals(bankSlips.get(0).getStatus()));
   }
}
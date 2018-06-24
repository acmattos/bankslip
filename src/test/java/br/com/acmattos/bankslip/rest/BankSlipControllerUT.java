package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlip;
import br.com.acmattos.bankslip.data.BankSlipRepository;
import br.com.acmattos.bankslip.data.BankSlipStatusEnum;
import br.com.acmattos.bankslip.util.UnitTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.HeaderResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BankSlipController Unit Tests.
 * @author acmattos
 */
public class BankSlipControllerUT extends UnitTest{
   static final String RESOURCE_URL = "/bankslips";
   private static final RuntimeException EXCEPTION =
      new RuntimeException("Exception Occurred!");

   @Autowired
   private ObjectMapper objectMapper;

   @InjectMocks
   private BankSlipController controller;

   @Mock
   private BankSlipRepository repository;

   private MockMvc mvc;

   private BankSlip entity;
   private NewBankSlipDTO newDto;
   private UUID uuid;

   @Before
   public void setUp() {
      this.mvc = MockMvcBuilders.standaloneSetup(controller)
         .setControllerAdvice(new BankSlipExceptionHandlerAdvice())
         .build();
      this.uuid = UUID.randomUUID();
      this.newDto = NewBankSlipDTO.builder()
         .dueDate(new Date())
         .totalInCents(BigDecimal.TEN)
         .customer("Customer")
         .status(BankSlipStatusEnum.PENDING)
         .build();
      this.entity = BankSlip.builder()
         .id(this.uuid)
         .dueDate(getDateBeforeToday(10))
         .totalInCents(new BigDecimal("100000"))
         .customer("Customer")
         .status(BankSlipStatusEnum.PENDING)
         .build();
   }
   
   @Test
   public void create_causes400Response() throws Exception {
      MvcResult result = this.mvc.perform(
         post(RESOURCE_URL)
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content("")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
      .andExpect(header().doesNotExist("dueDate"))
      .andExpect(header().doesNotExist("totalInCents"))
      .andExpect(header().doesNotExist("customer"))
      .andExpect(header().doesNotExist("status"))
      .andReturn()
      ;
      String content = result.getResponse().getContentAsString();
      assertEquals("400 : Bankslip not provided in the request body", content);
   }

   @Test
   public void create_causes422Response_OnlyDueDateIsInvalid() throws Exception {
      String json = new StringBuilder("{")
         .append("\"dueDate\":").append("\"invalid!\",")
         .append("\"totalInCents\":").append("100000,")
         .append("\"customer\":").append("\"Customer\",")
         .append("\"status\":").append("\"PENDING\"")
         .append("}").toString();

      MvcResult result =this.mvc.perform(
         post(RESOURCE_URL)
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(json)
      )
      .andExpect(status().isUnprocessableEntity())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
      .andExpect(header().string("dueDate", "Date format accepted: (yyyy-MM-dd)"))
      .andExpect(header().doesNotExist("totalInCents"))
      .andExpect(header().doesNotExist("customer"))
      .andExpect(header().doesNotExist("status"))
      .andReturn()
      ;
      String content = result.getResponse().getContentAsString();
      assertEquals(
        "422 : Invalid bankslip provided. Check HEADERS for more information!", content);
   }

   @Test
   public void create_causes422Response_AllAttributesAreInvalid() throws Exception {
      NewBankSlipDTO dto = new NewBankSlipDTO();
      MvcResult result =this.mvc.perform(
         post(RESOURCE_URL)
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(objectMapper.writeValueAsString(dto))
      )
      .andExpect(status().isUnprocessableEntity())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE ))
      .andExpect(header().string("dueDate", "Can't be null!"))
      .andExpect(header().string("totalInCents", "Can't be null or below zero!"))
      .andExpect(header().string("customer", "Can't be null or empty!"))
      .andExpect(header().string("status", "Can't be null!"))
      .andReturn()
      ;
      String content = result.getResponse().getContentAsString();
      assertEquals(
         "422 : Invalid bankslip provided. Check HEADERS for more information!", content);
   }

   @Test
   public void create_causes500Response() throws Exception {
      String content = objectMapper.writeValueAsString(this.newDto);

      when(repository.save(any(BankSlip.class)))
          .thenThrow(EXCEPTION);
      this.mvc.perform(
         post(RESOURCE_URL)
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isInternalServerError())
      .andExpect(header().doesNotExist("dueDate"))
      .andExpect(header().doesNotExist("totalInCents"))
      .andExpect(header().doesNotExist("customer"))
      .andExpect(header().doesNotExist("status"))
      .andExpect(getISErrorHeaderResultMatchers().string("ISError",
         "Could not create new bank slip: [Ticket-c97456c5-3d32-45bb-b0f1-ec7352465788] - Exception Occurred!"))
      ;
      
      verify(repository, times(1))
          .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }
   
   @Test
   public void create_causes201Response() throws Exception {
      String content = objectMapper.writeValueAsString(this.newDto);

      when(repository.save(any(BankSlip.class)))
         .thenReturn(this.entity);
      MvcResult result = this.mvc.perform(
         post(RESOURCE_URL)
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isCreated())
      .andExpect(header().doesNotExist("dueDate"))
      .andExpect(header().doesNotExist("totalInCents"))
      .andExpect(header().doesNotExist("customer"))
      .andExpect(header().doesNotExist("status"))
      .andExpect(header().string("Location",
         "/rest" + RESOURCE_URL + "/" + this.entity.getId()))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
      .andReturn()
      ;
      String body = result.getResponse().getContentAsString();
      assertEquals("201 : Bankslip created", body);

      verify(repository, times(1))
           .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void findAll_causes404Response() throws Exception {
      when(repository.findAll())
         .thenReturn(new ArrayList<>());
      this.mvc.perform(
         get(RESOURCE_URL)
      )
      .andExpect(status().isNotFound())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(header().doesNotExist("ISError"))
      .andExpect(jsonPath("$", hasSize(0)))
      ;

      verify(repository, times(1))
         .findAll();
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void findAll_causes500Response() throws Exception {
      when(repository.findAll())
         .thenThrow(EXCEPTION);
      this.mvc.perform(
         get(RESOURCE_URL)
      )
      .andExpect(status().isInternalServerError())
      .andExpect(getISErrorHeaderResultMatchers().string("ISError",
      "Could not find all bank slips: Exception Occurred!"))
      ;

      verify(repository, times(1))
         .findAll();
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void findAll_causes200Response() throws Exception {
      when(repository.findAll())
         .thenReturn(new ArrayList<BankSlip>(){{add(entity);}});
      this.mvc.perform(
         get(RESOURCE_URL)
      )
      .andExpect(status().isOk())
      .andExpect(header().doesNotExist("ISError"))
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id", is(this.uuid.toString())))
      .andExpect(jsonPath("$[0].dueDate", is(getLocalDateBeforeToday(10).toString())))
      .andExpect(jsonPath("$[0].totalInCents", is(100000)))
      .andExpect(jsonPath("$[0].customer", is("Customer")))
      ;

      verify(repository, times(1))
         .findAll();
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void findById_causes400Response() throws Exception {
      MvcResult result = this.mvc.perform(
         get(RESOURCE_URL + "/5b034b81595d896db05a38e8a")
      )
      .andExpect(status().isBadRequest())
      .andExpect(header().doesNotExist("ISError"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
      .andReturn()
      ;

      String content = result.getResponse().getContentAsString();
      assertEquals(
         "400 : Invalid id provided - it must be a valid UUID", content);

      verify(repository, times(0))
         .findById(any(UUID.class));
      verifyNoMoreInteractions(repository);
   }
   
   @Test
   public void findById_causes400ResponseForInvalidUUID() throws Exception {
      MvcResult result = this.mvc.perform(
         get(RESOURCE_URL + "/1-1-1-1-1")
      )
      .andExpect(status().isBadRequest())
      .andExpect(header().doesNotExist("ISError"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
      .andReturn()
      ;
   
      String content = result.getResponse().getContentAsString();
      assertEquals(
         "400 : Invalid id provided - it must be a valid UUID", content);
   
      verify(repository, times(0))
         .findById(any(UUID.class));
      verifyNoMoreInteractions(repository);
   }
   
   @Test
   public void findById_causes404Response() throws Exception {
      when(repository.findById(any(UUID.class)))
         .thenReturn(Optional.empty());
      MvcResult result = this.mvc.perform(
         get(RESOURCE_URL + "/84e8adbf-1a14-403b-ad73-d78ae19b59bf")
      )
      .andExpect(status().isNotFound())
      .andExpect(header().doesNotExist("ISError"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
      .andReturn()
      ;

      String content = result.getResponse().getContentAsString();
      assertEquals("404 : Bankslip not found with the specified id", content);

      verify(repository, times(1))
         .findById(any(UUID.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void findById_causes500Response() throws Exception {
      when(repository.findById(any(UUID.class)))
          .thenThrow(EXCEPTION);
      this.mvc.perform(
         get(RESOURCE_URL + "/c2dbd236-3fa5-4ccc-9c12-bd0ae1d6dd89")
      )
      .andExpect(status().isInternalServerError())
      .andExpect(getISErrorHeaderResultMatchers().string("ISError",
         "Could not find this particular bank slip: Exception Occurred!"))
      ;

      verify(repository, times(1))
         .findById(any(UUID.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void findById_causes200Response() throws Exception {
      when(repository.findById(any(UUID.class)))
         .thenReturn(Optional.of(this.entity));
      this.mvc.perform(
         get(RESOURCE_URL + "/3A4DD880-5FA5-350D-5BE5-FDF1C252F793")
      )
      .andExpect(status().isOk())
      .andExpect(header().doesNotExist("ISError"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", is(this.uuid.toString())))
      .andExpect(jsonPath("$.dueDate", is(getLocalDateBeforeToday(10).toString())))
      .andExpect(jsonPath("$.totalInCents", is(100000)))
      .andExpect(jsonPath("$.customer", is("Customer")))
      .andExpect(jsonPath("$.fine", is(500)))
      .andExpect(jsonPath("$.status", is("PENDING")))
      ;

      verify(repository, times(1))
         .findById(any(UUID.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void payOne_causes404Response() throws Exception {
      String content = objectMapper.writeValueAsString(
         UpdatedBankSlipStatusDTO.builder()
            .status(BankSlipStatusEnum.PAID).build());

      when(repository.findById(any(UUID.class)))
         .thenReturn(Optional.empty());
      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING,this.entity.getStatus());

      MvcResult result = this.mvc.perform(
         put(RESOURCE_URL + "/" + this.entity.getId())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isNotFound())
      .andExpect(header().doesNotExist("status"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
      .andReturn()
      ;

      String body = result.getResponse().getContentAsString();
      assertEquals("404 : Bankslip not found with the specified id", body);

      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING, this.entity.getStatus());

      verify(repository, times(1))
         .findById(any(UUID.class));
      verify(repository, times(0))
         .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void payOne_causes422Response() throws Exception {
      String content = objectMapper.writeValueAsString(
         UpdatedBankSlipStatusDTO.builder()
            .status(BankSlipStatusEnum.PENDING).build());

      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING,this.entity.getStatus());

      MvcResult result = this.mvc.perform(
         put(RESOURCE_URL + "/" + this.entity.getId())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isUnprocessableEntity())
      .andExpect(header().doesNotExist("status"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
      .andReturn()
      ;

      String body = result.getResponse().getContentAsString();
      assertEquals("422 : Invalid bankslip status provided (PENDING)", body);

      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING, this.entity.getStatus());

      verify(repository, times(0))
         .findById(any(UUID.class));
      verify(repository, times(0))
         .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void payOne_causes500Response() throws Exception {
      String content = objectMapper.writeValueAsString(
         UpdatedBankSlipStatusDTO.builder()
            .status(BankSlipStatusEnum.PAID).build());

      when(repository.findById(any(UUID.class)))
         .thenThrow(EXCEPTION);
      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING,this.entity.getStatus());

      this.mvc.perform(
         put(RESOURCE_URL + "/" + this.entity.getId())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isInternalServerError())
      .andExpect(header().doesNotExist("status"))
      .andExpect(getISErrorHeaderResultMatchers().string("ISError",
      "Could not pay or cancel this particular bank slip: Exception Occurred!"))
      ;

      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING, this.entity.getStatus());

      verify(repository, times(1))
         .findById(any(UUID.class));
      verify(repository, times(0))
         .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void payOne_causes200Response() throws Exception {
      String content = objectMapper.writeValueAsString(
         UpdatedBankSlipStatusDTO.builder()
            .status(BankSlipStatusEnum.PAID).build());

      when(repository.findById(any(UUID.class)))
         .thenReturn(Optional.of(this.entity));
      when(repository.save(any(BankSlip.class))).thenReturn(this.entity);
      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING,this.entity.getStatus());

      MvcResult result = this.mvc.perform(
         put(RESOURCE_URL + "/" + this.entity.getId())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isOk())
      .andExpect(header().doesNotExist("status"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
      .andReturn()
      ;

      String body = result.getResponse().getContentAsString();
      assertEquals("200 : Bankslip paid", body);
      assertEquals("BankSlipStatusEnum.PAID",
         BankSlipStatusEnum.PAID, this.entity.getStatus());

      verify(repository, times(1))
         .findById(any(UUID.class));
      verify(repository, times(1))
         .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void cancelOne_causes404Response() throws Exception {
      String content = objectMapper.writeValueAsString(
         UpdatedBankSlipStatusDTO.builder()
            .status(BankSlipStatusEnum.CANCELED).build());

      when(repository.findById(any(UUID.class)))
         .thenReturn(Optional.empty());
      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING,this.entity.getStatus());

      MvcResult result = this.mvc.perform(
         put(RESOURCE_URL + "/" + this.entity.getId())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isNotFound())
      .andExpect(header().doesNotExist("status"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
      .andReturn()
      ;

      String body = result.getResponse().getContentAsString();
      assertEquals("404 : Bankslip not found with the specified id", body);

      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING, this.entity.getStatus());

      verify(repository, times(1))
         .findById(any(UUID.class));
      verify(repository, times(0))
         .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void cancelOne_causes422Response() throws Exception {
      String content = objectMapper.writeValueAsString(
         UpdatedBankSlipStatusDTO.builder()
            .status(BankSlipStatusEnum.PENDING).build());

      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING,this.entity.getStatus());

      MvcResult result = this.mvc.perform(
         put(RESOURCE_URL + "/" + this.entity.getId())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isUnprocessableEntity())
      .andExpect(header().doesNotExist("status"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
      .andReturn()
      ;

      String body = result.getResponse().getContentAsString();
      assertEquals("422 : Invalid bankslip status provided (PENDING)", body);

      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING,this.entity.getStatus());

      verify(repository, times(0))
         .findById(any(UUID.class));
      verify(repository, times(0))
         .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void cancelOne_causes500Response() throws Exception {
      String content = objectMapper.writeValueAsString(
         UpdatedBankSlipStatusDTO.builder()
            .status(BankSlipStatusEnum.CANCELED).build());

      when(repository.findById(any(UUID.class)))
         .thenThrow(new RuntimeException("Exception Occurred!"));
      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING,this.entity.getStatus());

      this.mvc.perform(
         put(RESOURCE_URL + "/" + this.entity.getId())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isInternalServerError())
      .andExpect(header().doesNotExist("status"))
      .andExpect(getISErrorHeaderResultMatchers().string("ISError",
      "Could not pay or cancel this particular bank slip: Exception Occurred!"))
      ;

      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING, this.entity.getStatus());

      verify(repository, times(1))
         .findById(any(UUID.class));
      verify(repository, times(0))
         .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }

   @Test
   public void cancelOne_causes200Response() throws Exception {
      String content = objectMapper.writeValueAsString(
         UpdatedBankSlipStatusDTO.builder()
            .status(BankSlipStatusEnum.CANCELED).build());

      when(repository.findById(any(UUID.class))).thenReturn(Optional.of(this.entity));
      when(repository.save(any(BankSlip.class))).thenReturn(this.entity);
      assertEquals("BankSlipStatusEnum.PENDING",
         BankSlipStatusEnum.PENDING,this.entity.getStatus());

      MvcResult result = this.mvc.perform(
         put(RESOURCE_URL + "/" + this.entity.getId())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .content(content)
      )
      .andExpect(status().isOk())
      .andExpect(header().doesNotExist("status"))
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
      .andReturn()
      ;

      String body = result.getResponse().getContentAsString();
      assertEquals("200 : Bankslip canceled", body);

      assertEquals("BankSlipStatusEnum.CANCELED",
         BankSlipStatusEnum.CANCELED,this.entity.getStatus());

      verify(repository, times(1))
         .findById(any(UUID.class));
      verify(repository, times(1))
         .save(any(BankSlip.class));
      verifyNoMoreInteractions(repository);
   }
   
   private LocalDate getLocalDateBeforeToday(int numberOfDaysBefore) {
      LocalDate today = LocalDate.now();
      return LocalDate.of(
         today.getYear(), today.getMonth(),
         today.getDayOfMonth() - numberOfDaysBefore);
   }
   
   private Date getDateBeforeToday(int numberOfDaysBefore) {
      return Date.from(getLocalDateBeforeToday(numberOfDaysBefore)
         .atStartOfDay(ZoneId.systemDefault()).toInstant());
   }
   
   private HeaderResultMatchers getISErrorHeaderResultMatchers(){
      return new HeaderResultMatchers(){
         public ResultMatcher string(final String name, final String value) {
         return result -> {
            assertNotNull("Response header '"
               + name + "' value can't be null!", value);
            assertTrue("Response header '" + name + "' value must have ':'!",
               value.contains(":"));
            assertEquals("Response header '" + name + "'",
               value.substring(0, value.indexOf(":")),
               result.getResponse().getHeader(name).substring(0, value.indexOf(":")));
         };
         }
      };
   }
}
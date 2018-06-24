package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.util.UnitTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * ResponseEntityBuilder Unit Tests.
 * @author acmattos
 */
@SuppressWarnings("unchecked")
public class ResponseEntityBuilderUT extends UnitTest {

   @Test
   public void builder() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      assertNotNull("Builder can't be null!", builder);
   }

   @Test(expected = IllegalArgumentException.class)
   public void key_nullKey() {
      try {
         ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
         builder.key(null);
         fail("An IllegalArgumentException must be thrown!");
      } catch (IllegalArgumentException e){
         assertEquals("Must be the same message",
            "A header KEY must not be null or empty!", e.getMessage());
         throw e;
      }
   }

   @Test(expected = IllegalArgumentException.class)
   public void key_emptyKey() {
      try {
         ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
         builder.key("");
         fail("An IllegalArgumentException must be thrown!");
      } catch (IllegalArgumentException e){
         assertEquals("Must be the same message",
            "A header KEY must not be null or empty!", e.getMessage());
         throw e;
      }
   }

   @Test
   public void key_validKey() {
      try {
         ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
         builder.key("valid");
      } catch (IllegalArgumentException e){
         fail("An IllegalArgumentException must not be thrown!");
         throw e;
      }
   }

   @Test(expected = IllegalArgumentException.class)
   public void value_keyNotSet() {
      try {
         ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
         builder.value("valid");
         fail("An IllegalArgumentException must be thrown!");
      } catch (IllegalArgumentException e){
         assertEquals("Must be the same message",
            "A header KEY must be set first!", e.getMessage());
         throw e;
      }
   }

   @Test(expected = IllegalArgumentException.class)
   public void value_nullValue() {
      try {
         ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
         builder.key("valid").value(null);
         fail("An IllegalArgumentException must be thrown!");
      } catch (IllegalArgumentException e){
         assertEquals("Must be the same message",
            "A header VALUE must not be null or empty!", e.getMessage());
         throw e;
      }
   }

   @Test(expected = IllegalArgumentException.class)
   public void value_empyValue() {
      try {
         ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
         builder.key("valid").value("");
         fail("An IllegalArgumentException must be thrown!");
      } catch (IllegalArgumentException e){
         assertEquals("Must be the same message",
            "A header VALUE must not be null or empty!", e.getMessage());
         throw e;
      }
   }

   @Test
   public void value_validValue() {
      try {
         ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
         builder.key("valid").value("valid");
      } catch (IllegalArgumentException e){
         fail("An IllegalArgumentException must not be thrown!");
         throw e;
      }
   }

   @Test
   public void build_StatusOK() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.OK().build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNull("Must be null!", responseEntity.getBody());
      assertEquals("Status must be OK",
         HttpStatus.OK.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_StatusCREATED() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.CREATED().build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNull("Must be null!", responseEntity.getBody());
      assertEquals("Stautus must be CREATED",
         HttpStatus.CREATED.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_StatusUNPROCESSABLE_ENTITY() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.UNPROCESSABLE_ENTITY().build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNull("Must be null!", responseEntity.getBody());
      assertEquals("Status must be UNPROCESSABLE_ENTITY",
         HttpStatus.UNPROCESSABLE_ENTITY.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_StatusBAD_REQUEST() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.BAD_REQUEST().build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNull("Must be null!", responseEntity.getBody());
      assertEquals("Status must be BAD_REQUEST",
         HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_StatusNOT_FOUND() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.NOT_FOUND().build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNull("Must be null!", responseEntity.getBody());
      assertEquals("Status must be NOT_FOUND",
         HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_StatusINTERNAL_SERVER_ERROR() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.INTERNAL_SERVER_ERROR().build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNull("Must be null!", responseEntity.getBody());
      assertEquals("Status must be NOT_FOUND",
          HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_bodyNullEntity() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      @SuppressWarnings("unchecked")
      ResponseEntity responseEntity = builder.body(null).build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNull("Must be null!", responseEntity.getBody());
      assertEquals("Status must be NOT_FOUND",
         HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_bodyEmptyEntityList() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.body(new ArrayList<>()).build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertTrue("Must be empty!", ((List)responseEntity.getBody()).isEmpty());
      assertEquals("Status must be NOT_FOUND",
         HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_bodyFoundDefaultsToOKStatus() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.body(new Object()).build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNotNull("Must not be null!", responseEntity.getBody());
      assertEquals("Status must be OK",
         HttpStatus.OK.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_bodyFoundChangeStatusDefault() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.body(new Object()).CREATED().build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNotNull("Must not be null!", responseEntity.getBody());
      assertEquals("Status must be CREATED",
         HttpStatus.CREATED.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_bodyFoundDontChangeStatusToOneSetBeforeBody() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.CREATED().body(new Object()).build();
      assertNotNull("Can't be null!", responseEntity);
      assertTrue("Must be empty!", responseEntity.getHeaders().isEmpty());
      assertNotNull("Must not be null!", responseEntity.getBody());
      assertEquals("Status must be OK",
         HttpStatus.OK.value(), responseEntity.getStatusCode().value());
   }

   @Test
   public void build_FullCreation() {
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      ResponseEntity responseEntity = builder.body(new Object()).key("Location").value("/uri/1").build();
      assertNotNull("Can't be null!", responseEntity);
      assertFalse("Must not be empty!", responseEntity.getHeaders().isEmpty());
      assertNotNull("Must not be null!", responseEntity.getBody());
      assertEquals("Status must be OK",
         HttpStatus.OK.value(), responseEntity.getStatusCode().value());
   }   
}
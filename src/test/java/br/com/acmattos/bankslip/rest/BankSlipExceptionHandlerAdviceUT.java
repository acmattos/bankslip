package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.util.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.Assert.*;

/**
 * BankSlipExceptionHandlerAdvice Unit Tests.
 * @author acmattos
 */
public class BankSlipExceptionHandlerAdviceUT extends UnitTest {
   private BankSlipExceptionHandlerAdvice advice;
   @Before
   public void setUp() throws Exception {
      this.advice = new BankSlipExceptionHandlerAdvice();
   }
   
   @Test
   public void handleHttpMessageNotReadable_dueDateInvalidFormat422() throws Exception {
      HttpMessageNotReadableException e =
         new HttpMessageNotReadableException("NewBankSlipDTO[\"dueDate\"]");
      
      ResponseEntity responseEntity =
         this.advice.handleHttpMessageNotReadable(e);
      
      assertNotNull("responseEntity can't be null", responseEntity);
      assertNotNull("HttpStatus can't be null", responseEntity.getStatusCode());
      assertNotNull("responseEntity can't be null", responseEntity.getHeaders());
      assertTrue("dueDate exists!", responseEntity.getHeaders().containsKey("dueDate"));
      assertNotNull("duedate can't be null", responseEntity.getHeaders().get("dueDate"));
      assertEquals("Must be equal!",
         "Date format accepted: (yyyy-MM-dd)", responseEntity.getHeaders().get("dueDate").get(0));
      assertNotNull("Body can't be null", responseEntity.getBody());
      assertEquals("Must be 422", HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
      assertEquals("Must have body",
         "422 : Invalid bankslip provided. Check HEADERS for more information!", responseEntity.getBody());
   }
   
   @Test
   public void handleHttpMessageNotReadable_BodyIsMissing() throws Exception {
      HttpMessageNotReadableException e =
         new HttpMessageNotReadableException("Required request body is missing");
   
      ResponseEntity responseEntity =
         this.advice.handleHttpMessageNotReadable(e);
   
      assertNotNull("responseEntity can't be null", responseEntity);
      assertNotNull("HttpStatus can't be null", responseEntity.getStatusCode());
      assertNotNull("Body can't be null", responseEntity.getBody());
      assertEquals("Must be 400", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
      assertEquals("Must have body",
         "400 : Bankslip not provided in the request body", responseEntity.getBody());
   }
   
   @Test
   public void handleMethodArgumentTypeMismatchException() throws Exception {
      MethodArgumentTypeMismatchException e =
         new MethodArgumentTypeMismatchException(null, null, null, null, null);
   
      ResponseEntity responseEntity =
         this.advice.handleMethodArgumentTypeMismatchException(e);
   
      assertNotNull("responseEntity can't be null", responseEntity);
      assertNotNull("HttpStatus can't be null", responseEntity.getStatusCode());
      assertNotNull("Body can't be null", responseEntity.getBody());
      assertEquals("Must be 400", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
      assertEquals("Must have body",
         "400 : Invalid id provided - it must be a valid UUID", responseEntity.getBody());
   }
   
}
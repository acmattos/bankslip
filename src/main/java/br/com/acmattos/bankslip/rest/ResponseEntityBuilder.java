package br.com.acmattos.bankslip.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Builds Response Entities sent by Rest Controllers.
 *
 * Examples:
 * ResponseEntity responseEntity =
 *   ResponseEntityBuilder.builder()
 *      .body(new Object())
 *      .key("Location")
 *      .value("/uri/1")
 *      .build();
 *
 * ResponseEntity responseEntity =
 *   ResponseEntityBuilder.builder()
 *      .body(null)
 *      .build();
 *
 * @author acmattos
 */
class ResponseEntityBuilder<T> {
   /** Response Entity Headers. */
   private MultiValueMap<String, String> headers;
   /** Response Entity Status. */
   private HttpStatus httpStatus;
   /** Header key. */
   private String key;
   /** Response Entity Body. */
   private T body;

   private ResponseEntityBuilder() {}

   /**
    * Builds an instance of this class.
    * @param <T> Type to be sent in body's response.
    * @return An instance of this builder.
    */
   public static <T> ResponseEntityBuilder<T> builder() {
      return new ResponseEntityBuilder<>();
   }

   /**
    * A valid (not null && not empty) header key.
    * @param key Header key.
    * @return An instance of this builder.
    */
   public ResponseEntityBuilder<T> key(String key) {
      Assert.hasLength(key , "A header KEY must not be null or empty!");
      if(null == headers){
         this.headers = new LinkedMultiValueMap<>();
      }
      this.key = key;
      return this;
   }

   /**
    * A valid (not null && not empty) header value for a previous configured
    * key.
    * If no key was set before, an exception is raised.
    *
    * @param value Header value.
    * @return An instance of this builder.
    */
   public ResponseEntityBuilder<T> value(String value) {
      Assert.notNull(this.key , "A header KEY must be set first!");
      Assert.hasLength(value , "A header VALUE must not be null or empty!");
      this.headers.add(this.key , value);
      this.key = null;
      return this;
   }

   /**
    * HTTP Status 200.
    * @return An instance of this builder.
    */
   public ResponseEntityBuilder<T> OK() {
      this.httpStatus = HttpStatus.OK;
      return this;
   }

   /**
    * HTTP Status 201.
    * @return An instance of this builder.
    */
   public ResponseEntityBuilder<T> CREATED() {
      this.httpStatus = HttpStatus.CREATED;
      return this;
   }

   /**
    * HTTP Status 400.
    * @return An instance of this builder.
    */
   public ResponseEntityBuilder<T> BAD_REQUEST() {
      this.httpStatus = HttpStatus.BAD_REQUEST;
      return this;
   }

   /**
    * HTTP Status 404.
    * @return An instance of this builder.
    */
   public ResponseEntityBuilder<T> NOT_FOUND() {
      this.httpStatus = HttpStatus.NOT_FOUND;
      return this;
   }

   /**
    * HTTP Status 500.
    * @return An instance of this builder.
    */
   public ResponseEntityBuilder<T> INTERNAL_SERVER_ERROR() {
      this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      return this;
   }

   /**
    * HTTP Status 422.
    * @return An instance of this builder.
    */
   public ResponseEntityBuilder<T> UNPROCESSABLE_ENTITY() {
      this.httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
      return this;
   }

   /**
    * A entity T to be sent as response body. If it's null, the HTTP Status is
    * automatically configured as 404, otherwise 200.
    *
    * @param body A entity T to be sent as response body.
    * @return An instance of this builder.
    */
   public ResponseEntityBuilder<T> body(T body) {
      if((null == body) || (body instanceof Iterable
         && !((Iterable) body).iterator().hasNext())){
         this.httpStatus = HttpStatus.NOT_FOUND;
      } else {
         this.httpStatus = HttpStatus.OK;
      }
      this.body = body;
      return this;
   }

   /**
    * Builds a ResponseEntity instance.
    * @return A ResponseEntity built by this builder.
    */
   public ResponseEntity build() {
      return new ResponseEntity<>(this.body, this.headers, this.httpStatus);
   }
}
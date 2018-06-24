package br.com.acmattos.bankslip;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Bootstraps Bank Slip Application.
 *
 * @author acmattos
 */
@SpringBootApplication
public class BankSlipApplication extends SpringBootServletInitializer {
    /**
     * Allows this application to run properly.
     *
     * @param args not used.
     * @throws Exception in case of general failure.
     */
   public static void main(String[] args) throws Exception {
      new SpringApplicationBuilder(BankSlipApplication.class).run(args);
   }
}

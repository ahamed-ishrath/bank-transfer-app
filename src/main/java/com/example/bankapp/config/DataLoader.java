package com.example.bankapp.config;

import com.example.bankapp.model.Customer;
import com.example.bankapp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final CustomerRepository customerRepository; 
 
    @Override 
    public void run(String... args) { 
 
        if (customerRepository.count() == 0) { 
 
            Customer kamal = new Customer( 
                    "100001",
                    "Kamal", 
                    "kamal@gmail.com", 
                    new BigDecimal("50000.00") 
            ); 
 
            Customer nimali = new Customer( 
                    "100002", 
                    "Nimali", 
                    "nimali@gmail.com", 
                    new BigDecimal("25000.00") 
            ); 
 
            Customer saman = new Customer( 
                    "100003", 
                    "Saman", 
                    "saman@gmail.com", 
                    new BigDecimal("10000.00") 
            ); 
 
            customerRepository.saveAll( 
                    List.of(kamal, nimali, saman) 
            ); 
 
            System.out.println("Sample customers inserted!"); 
        } 
    } 
    
}


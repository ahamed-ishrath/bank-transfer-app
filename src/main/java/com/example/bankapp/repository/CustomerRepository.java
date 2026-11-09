package com.example.bankapp.repository;

import com.example.bankapp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

 
public interface CustomerRepository extends JpaRepository<Customer, Long> {
 
    // 1. Find customer using account number 
    @Query("SELECT c FROM Customer c WHERE c.accountNumber = :accountNumber") 
    Optional<Customer> getByAccountNumber( 
            @Param("accountNumber") String accountNumber); 
 
    // 2. Count accounts 
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.accountNumber = :accountNumber") 
    long countAccounts(@Param("accountNumber") String accountNumber); 
 
    // 3. Count emails 
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.email = :email") 
    long countEmails(@Param("email") String email); 
 
    // 4. Get all customers ordered by name 
    @Query("SELECT c FROM Customer c ORDER BY c.fullName ASC") 
    List<Customer> findAllOrdered(); 
 
    // 5. Search customers by name 
    @Query("SELECT c FROM Customer c WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))") 
    List<Customer> searchByName(@Param("keyword") String keyword); 
 
    // 6. Calculate total balance 
    @Query("SELECT SUM(c.balance) FROM Customer c") 
    BigDecimal getTotalBalance(); 
 
    // 7. Native SQL query 
    @Query(value = "SELECT COUNT(*) FROM customers", nativeQuery = true) 
    long countCustomersNative(); 
 
    // 8. Debit account safely 
    @Modifying(clearAutomatically = true, flushAutomatically = true) 
    @Transactional 
    @Query(""" 
        UPDATE Customer c 
        SET c.balance = c.balance - :amount WHERE c.accountNumber = :accountNumber 
        AND c.balance >= :amount""") 
    int debit( 
            @Param("accountNumber") String accountNumber, 
            @Param("amount") BigDecimal amount); 
 
    // 9. Credit account 
    @Modifying(clearAutomatically = true, flushAutomatically = true) 
    @Transactional 
    @Query(""" 
        UPDATE Customer c 
        SET c.balance = c.balance + :amount 
        WHERE c.accountNumber = :accountNumber 
        """) 
    int credit( 
            @Param("accountNumber") String accountNumber, 
            @Param("amount") BigDecimal amount); 
} 

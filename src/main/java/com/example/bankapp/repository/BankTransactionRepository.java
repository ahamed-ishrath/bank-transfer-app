package com.example.bankapp.repository; 
import com.example.bankapp.model.BankTransaction; 
import com.example.bankapp.model.TransactionStatus; 
import java.math.BigDecimal; 
import java.util.List; 
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param; 
 
public interface BankTransactionRepository 
        extends JpaRepository<BankTransaction, Long> { 
         
        // 1. All transactions, latest first 
    @Query("SELECT t FROM BankTransaction t ORDER BY t.createdAt DESC") 
    List<BankTransaction> findAllLatest(); 
 
        // 2. Account transaction history 
    @Query(""" 
        SELECT t FROM BankTransaction t 
        WHERE t.fromAccount = :account 
        OR t.toAccount = :account 
        ORDER BY t.createdAt DESC 
        """) 
    List<BankTransaction> findHistory(@Param("account") String account); 
 
        // 3. Filter transactions by status 
    @Query(""" 
        SELECT t FROM BankTransaction t 
        WHERE t.status = :status 
        ORDER BY t.createdAt DESC 
        """) 
    List<BankTransaction> findByStatusLatest( 
            @Param("status") TransactionStatus status); 
 
        // 4. Count transactions by status 
    @Query("SELECT COUNT(t) FROM BankTransaction t WHERE t.status = :status") 
    long countByStatus( 
            @Param("status") TransactionStatus status); 
 
} 
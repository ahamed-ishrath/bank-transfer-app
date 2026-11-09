package com.example.bankapp.model; 
 
import java.math.BigDecimal; 
import java.time.LocalDateTime; 
import jakarta.persistence.Column; 
import jakarta.persistence.Entity; 
import jakarta.persistence.EnumType; 
import jakarta.persistence.Enumerated; 
import jakarta.persistence.Id; 
import jakarta.persistence.PrePersist; 
import jakarta.persistence.Table; 
import lombok.Getter; 
import lombok.NoArgsConstructor; 
import lombok.Setter; 
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
 
@Entity  
@Table(name = "bank_transactions")   
@Getter  
@Setter  
@NoArgsConstructor  
public class BankTransaction { 
    @Id  
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
 
    @Column(name = "from_account", length = 20) 
    private String fromAccount; 
 
    @Column(name = "to_account", length = 20) 
    private String toAccount; 
 
    @Column(nullable = false, precision = 14, scale = 2) 
    private BigDecimal amount; 
 
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20) 
    private TransactionStatus status; 
 
    @Column(length = 255) 
    private String description; 
 
    @Column(name = "created_at", nullable = false) 
    private LocalDateTime createdAt; 
 
    @PrePersist 
    public void onCreate() { 
        if (createdAt == null) { 
            createdAt = LocalDateTime.now(); 
        } 
    } 
 
    public BankTransaction(String fromAccount, String toAccount, BigDecimal 
amount, TransactionStatus status, String description) { 
        this.fromAccount = fromAccount; 
        this.toAccount = toAccount; 
        this.amount = amount; 
        this.status = status; 
        this.description = description; 
    } 
} 
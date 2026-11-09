package com.example.bankapp.model; 
 
import java.math.BigDecimal; 
import jakarta.persistence.Column; 
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Id; 
import jakarta.persistence.Table; 
import jakarta.validation.constraints.DecimalMin; 
import jakarta.validation.constraints.Digits; 
import jakarta.validation.constraints.Email; 
import jakarta.validation.constraints.NotBlank; 
import jakarta.validation.constraints.NotNull; 
import jakarta.validation.constraints.Size; 
import lombok.Getter; 
import lombok.NoArgsConstructor; 
import lombok.Setter; 
 
@Entity  
@Table(name = "customers") 
@Getter  
@Setter  
@NoArgsConstructor 
public class Customer { 
 
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private Long id; 
 
    @Column(name = "account_number", unique = true, nullable = false, length = 20) 
    @NotBlank(message = "Account number is required") 
    private String accountNumber; 
 
    @Column(name = "full_name", nullable = false, length = 100) 
    @NotBlank(message = "Customer name is required") 
    @Size(max = 100) 
    private String fullName; 
 
    @Column(nullable = false, unique = true, length = 120) 
    @NotBlank(message = "Email is required") 
    @Email(message = "Enter a valid email") 
    private String email; 
 
    @Column(nullable = false, precision = 14, scale = 2) 
    @NotNull(message = "Balance is required") 
    @DecimalMin(value = "0.00", message = "Balance cannot be negative") 
    @Digits(integer = 12, fraction = 2) 
    private BigDecimal balance; 
 
        public Customer(String accountNumber, String fullName, String email, 
BigDecimal balance) { 
        this.accountNumber = accountNumber; 
        this.fullName = fullName; 
        this.email = email; 
        this.balance = balance; 
    } 
}  

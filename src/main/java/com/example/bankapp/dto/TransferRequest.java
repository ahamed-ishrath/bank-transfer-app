package com.example.bankapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** Holds the data typed into the "Transfer Money" form. */
@Getter
@Setter
public class TransferRequest {
    @NotBlank(message = "Sender account is required") 
    private String fromAccount; 
@NotBlank(message = "Receiver account is required") 
    private String toAccount; 
 
    @NotNull(message = "Amount is required") 
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero") 
    @Digits(integer = 12, fraction = 2, 
            message = "Maximum 2 decimal places allowed") 
    private BigDecimal amount; 
 
    private boolean simulateFailure;  
}
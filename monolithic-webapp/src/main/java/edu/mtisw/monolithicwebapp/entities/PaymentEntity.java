package edu.mtisw.monolithicwebapp.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name= "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String rut;
    private double amount;
    private status status;
    private LocalDate paymentDate;
    private double interestRate;
    private LocalDate lastInterestCalculationDate;
    private LocalDate lastDiscountByMarkCalculationDate;

    @Column(name = "base_discount", nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double baseDiscountValue;

    @Column(name = "mark_discount", nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double markDiscountValue;
    private double interestValue;

    private double totalAmount;

    public enum status{
        PENDIENTE, PAGADO, ATRASADO
    }

}

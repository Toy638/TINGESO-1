



package edu.mtisw.monolithicwebapp.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String rut;

    private String name;
    private String lastname;
    private String school;
    private Schooltype school_type;
    private LocalDate birthdate;


    private int graduation_year;

    @Column(name = "quantity_of_payments", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int quantity_of_payments = 0;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int exams_taken = 0;


    @Column(nullable = true, columnDefinition = "DOUBLE DEFAULT 0.0")
    private int exam_mean_mark = 0;

    @Column(nullable = true, columnDefinition = "DOUBLE DEFAULT 0.0")
    //quizá estos sean atributos derivables
    private double total_amount_to_pay = 0.0;

    @Column(nullable = true, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double amount_paid = 0.0;

    @Column(nullable = true, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double amount_to_pay = 0.0;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private payment_method payment_method;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int payments_taken = 0;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int pending_payments = 0;


    private LocalDate last_payment_date;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int pending_amount = 0;


    public enum payment_method{
        NA, Contado, Cuotas
    }
    public enum Schooltype{
        Municipal, Privado, Subvencionado
    }




}

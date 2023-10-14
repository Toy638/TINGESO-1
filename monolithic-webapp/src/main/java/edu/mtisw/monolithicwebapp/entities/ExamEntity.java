package edu.mtisw.monolithicwebapp.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(name = "exam")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class ExamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    int id;
    String rut;
    String date;
    int mark;

}

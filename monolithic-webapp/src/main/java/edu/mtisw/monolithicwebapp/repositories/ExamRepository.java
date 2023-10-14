package edu.mtisw.monolithicwebapp.repositories;

import edu.mtisw.monolithicwebapp.entities.ExamEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ExamRepository extends CrudRepository<ExamEntity, Long> {

    public ArrayList<ExamEntity> findByRut(String rut);



}

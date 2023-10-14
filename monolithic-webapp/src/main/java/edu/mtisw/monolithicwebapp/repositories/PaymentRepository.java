package edu.mtisw.monolithicwebapp.repositories;

import edu.mtisw.monolithicwebapp.entities.PaymentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface PaymentRepository extends CrudRepository<PaymentEntity, Long> {

    public ArrayList<PaymentEntity> findByRut(String rut);

}

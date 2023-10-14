package edu.mtisw.monolithicwebapp.controllers;

import edu.mtisw.monolithicwebapp.entities.PaymentEntity;
import edu.mtisw.monolithicwebapp.entities.StudentEntity;
import edu.mtisw.monolithicwebapp.services.ExamService;
import edu.mtisw.monolithicwebapp.services.PaymentService;
import edu.mtisw.monolithicwebapp.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class PaymentController {

    @Autowired
    PaymentService paymentService;
    @Autowired
    StudentService studentService;



    @GetMapping("/payments/{id}")
    public String listarPagosEstudiante(@PathVariable  Long id, Model myModel){

        Optional<StudentEntity> student = studentService.getById(id);
        if(student.isPresent()){
            StudentEntity student_no_null = student.get();
            ArrayList<PaymentEntity> payments = paymentService.getByRut(student_no_null.getRut());
            if(!payments.isEmpty()){
                myModel.addAttribute("payments", payments);

            }
        }
        ArrayList<PaymentEntity> payments = paymentService.getByRut(student.get().getRut());
        myModel.addAttribute("payments", payments);

        return "payments";
    }


    @GetMapping("/payments/")
    public String listar(Model myModel){
        ArrayList<PaymentEntity> payments = paymentService.getPayments();
        myModel.addAttribute("payments", payments);
        return "payments";
    }


    //TODO: permitir que el servicio genere los pagos, en este controlador, solo se le asocia la cantidad de cuotas que el estudiante desea
    @GetMapping("/payments/new/{id}")
    public String paymentDetail(@PathVariable Long id, Model myModel){

        Optional<StudentEntity> student = studentService.getById(id);
        int maxPayments = studentService.calcMaxPayments(student.get().getSchool_type().name());
        studentService.updateQuantityOfPayments(id, maxPayments);
        myModel.addAttribute("student", student.get());
        myModel.addAttribute("maxPayments", maxPayments);
        return "generate_payments";

    }

    @GetMapping("/payments/generatePayments/{id}")
    public String generatePayments(@PathVariable Long id, @RequestParam("cantidadCuotas") int cantidadCuotas){

        paymentService.processPayments(id, cantidadCuotas);
        return "redirect:/payments/"+ id;

    }

    @GetMapping("/payments/pay/{id}")
    public String markAsPaid(@PathVariable Long id){
        PaymentEntity payment = paymentService.getById(id).get();
        paymentService.markAsPaid(payment.getRut());
        return "redirect:/students/";
    }

    @GetMapping("/payments/pay/calc/{id}")
    public String calcPayments(@PathVariable Long id, Model myModel){

        StudentEntity student = studentService.getById(id).get();
        paymentService.applyDiscountByMark(student.getRut());
        paymentService.applyInterest(student.getRut());
        ArrayList<PaymentEntity> payments = paymentService.getByRut(student.getRut());
        myModel.addAttribute("payments", payments);
        return "pay";
    }
}

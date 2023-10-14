package edu.mtisw.monolithicwebapp.controllers;


import edu.mtisw.monolithicwebapp.entities.StudentEntity;
import edu.mtisw.monolithicwebapp.entities.UsuarioEntity;
import edu.mtisw.monolithicwebapp.services.PaymentService;
import edu.mtisw.monolithicwebapp.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.GeneratedValue;
import java.util.ArrayList;

@Controller
public class StudentController {

    @Autowired
    StudentService studentService;

    @Autowired
    PaymentService paymentService;

    @GetMapping({"/students/","/"})
    public String listar(Model myModel){
        ArrayList<StudentEntity> students = studentService.getStudents();
        myModel.addAttribute("students", students);
        return "index";
    }

    @GetMapping("/students/form/new")
    public String studentCreateForm(Model myModel){
        StudentEntity student = new StudentEntity();
        myModel.addAttribute("student", student);
        return "create_student";
    }

    @PostMapping("/student/form/new/submit")
    public String save(@ModelAttribute("estudiante") StudentEntity student){
        studentService.saveStudent(student);
        return "redirect:/students/";
    }

    @GetMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return "redirect:/students/";
    }

    @GetMapping("/student/report/{id}")
    public String studentReports(@PathVariable Long id, Model myModel){
        StudentEntity student = paymentService.report(id);
        myModel.addAttribute("student", student);
        return "student_reports";
    }

}

package edu.mtisw.monolithicwebapp.services;


import edu.mtisw.monolithicwebapp.entities.ExamEntity;
import edu.mtisw.monolithicwebapp.entities.PaymentEntity;
import edu.mtisw.monolithicwebapp.entities.StudentEntity;
import edu.mtisw.monolithicwebapp.entities.UsuarioEntity;
import edu.mtisw.monolithicwebapp.repositories.StudentRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ExamService examService;



    public ArrayList<StudentEntity> getStudents() {
        return (ArrayList<StudentEntity>) studentRepository.findAll();
    }

    public StudentEntity saveStudent(StudentEntity student){
        return studentRepository.save(student);
    }

    public Optional<StudentEntity> getById(Long id){
        return studentRepository.findById(id);
    }

    public boolean deleteStudent(Long id){
        try {
            studentRepository.deleteById(id);
            return true;
        }catch (Exception err){
            return false;
        }

    }


    public void updateQuantityOfPayments(long id, int quantity_of_payments){

        Optional<StudentEntity> student = studentRepository.findById(id);
        if(student.isEmpty()){
            return;
        }
        StudentEntity student_No_Null = student.get();
        student_No_Null.setQuantity_of_payments(quantity_of_payments);
        studentRepository.save(student_No_Null);
    }



    public int calcMaxPayments(String typeOfSchool) {
        int maxPayments = 0;
        if (typeOfSchool.equals("Municipal")) {
            maxPayments = 10;
        } else if (typeOfSchool.equals("Subvencionado")) {
            maxPayments = 7;
        } else if (typeOfSchool.equals("Privado")) {
            maxPayments = 5;
        }
        return maxPayments;
    }

    public StudentEntity getByRut(String rut){
        return studentRepository.findByRut(rut);
    }

    public StudentEntity calcMeanMark(String Rut){

        StudentEntity student = studentRepository.findByRut(Rut);
        ArrayList<ExamEntity> marks = examService.getByRut(Rut);

        double sum = 0;
        for(int i = 0; i < marks.size(); i++){
            sum += marks.get(i).getMark();
        }
        double mean = sum/marks.size();
        student.setExam_mean_mark((int) mean);
        studentRepository.save(student);
        return student;
    }


}

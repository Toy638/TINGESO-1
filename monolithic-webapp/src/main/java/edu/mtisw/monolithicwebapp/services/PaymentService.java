package edu.mtisw.monolithicwebapp.services;


import edu.mtisw.monolithicwebapp.entities.ExamEntity;
import edu.mtisw.monolithicwebapp.entities.PaymentEntity;
import edu.mtisw.monolithicwebapp.entities.StudentEntity;
import edu.mtisw.monolithicwebapp.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    StudentService studentService;

    @Autowired
    ExamService examService;

    public ArrayList<PaymentEntity> getPayments() {
        return (ArrayList<PaymentEntity>) paymentRepository.findAll();
    }

    public PaymentEntity savePayment(PaymentEntity payment){
        return paymentRepository.save(payment);
    }

    public ArrayList<PaymentEntity> getByRut(String rut){
        return paymentRepository.findByRut(rut);
    }

    public Optional<PaymentEntity> getById(Long id) { return paymentRepository.findById(id); }

    public boolean deletePayment(Long id){
        try {
            paymentRepository.deleteById(id);
            return true;
        }catch (Exception err){
            return false;
        }
    }




    public boolean isUnpaid(PaymentEntity payment, LocalDate expirationDate, LocalDate today){
        return ( (payment.getStatus() != PaymentEntity.status.PAGADO) && expirationDate.isBefore(today) );
    }


    public double interestByMonths(int unpaidMonths){
        double interest = 0;
        if(unpaidMonths == 1){
            interest = 0.03;
        } else if (unpaidMonths == 2) {
            interest =  0.06;
        }
        else if (unpaidMonths == 3) {
            interest =  0.09;
        }
        else if (unpaidMonths > 3) {
            interest = 0.15;
        }
        return interest;
    }

    public double discountBySchoolType(StudentEntity.Schooltype schoolType){

        if(schoolType == StudentEntity.Schooltype.Municipal){
            return 0.2;
        } else if (schoolType == StudentEntity.Schooltype.Subvencionado){
            return 0.1;
        } else {
            return 0;
        }
    }

    // analizar el refactorizar y utilizar una variable para evitar rl return del else (se ve feo)
    public double discountByPaymentMethod(int quantifyOfPayments){
        if (quantifyOfPayments == 1){
            return 0.5;
        } else {
            return 0;
        }
    }

    public double discountByYearsOfGraduation(int yearsOfGraduation){
        if(yearsOfGraduation < 1){
            return 0.15;
        } else if (yearsOfGraduation == 1){
            return 0.08;
        } else if (yearsOfGraduation == 2){
            return 0.04;
        } else {
            return 0;
        }
    }

    public double discountByMark(int meanMark){
        double discount = 0;
        if(meanMark >= 950 && meanMark <= 1000 ){
            discount = 0.1;
        } else if (meanMark >= 900 && meanMark <= 949 ) {
            discount = 0.05;
        } else if (meanMark >= 850 && meanMark <= 899) {
            discount = 0.02;
        }
        return discount;
    }



    public double calcPercentage(double amount, double interest){ return (amount * interest); }


    public boolean oneMonthSinceLastCalc(LocalDate today, LocalDate lastCalc){
        return (today.getMonthValue() - lastCalc.getMonthValue()) >= 1;
    }


    public int countAndMarkUnpaidMonths(ArrayList<PaymentEntity> payments, LocalDate today){

        int unpaidMonths = 0;
        if(!payments.isEmpty()){

             for (PaymentEntity payment:
                 payments) {
                if(isUnpaid(payment, payment.getPaymentDate(), today) || payment.getStatus() == PaymentEntity.status.ATRASADO){
                    unpaidMonths++;
                    payment.setStatus(PaymentEntity.status.ATRASADO);
                }
            }
            paymentRepository.saveAll(payments);
        }
        return unpaidMonths;
    }


    public ArrayList<PaymentEntity> applyInterest(String rut){

        ArrayList<PaymentEntity> payments = paymentRepository.findByRut(rut);

        int unpaidMonths = countAndMarkUnpaidMonths(payments, LocalDate.now());
        double percInterest = interestByMonths(unpaidMonths);
        double mountOfInterest = 0;

        for (PaymentEntity payment :
                payments) {
            if (payment.getStatus() == PaymentEntity.status.ATRASADO) {
                mountOfInterest = calcPercentage(payment.getAmount(), percInterest);
                payment.setInterestValue(mountOfInterest);
                payment.setTotalAmount(payment.getTotalAmount() + mountOfInterest);
            }
        }

        return null;

    }


    public ArrayList<PaymentEntity> applyDiscountByMark(String rut) {

        ArrayList<PaymentEntity> payments = paymentRepository.findByRut(rut);
        ArrayList<ExamEntity> exams = examService.getByRut(rut);

        double mean = 0;

        for (ExamEntity exam : exams) {
            mean += exam.getMark();
        }
        mean = mean / exams.size();

        for(PaymentEntity payment: payments){
            if(payment.getStatus() != PaymentEntity.status.PAGADO ){

                double percDiscountByMark = discountByMark((int) mean);
                double mountOfDiscountByMark = calcPercentage(payment.getAmount(), percDiscountByMark);
                System.out.println(mountOfDiscountByMark);
                payment.setInterestRate(percDiscountByMark);
                payment.setMarkDiscountValue(mountOfDiscountByMark);
                payment.setLastDiscountByMarkCalculationDate(LocalDate.now());
                payment.setTotalAmount(payment.getAmount() - (payment.getBaseDiscountValue() + payment.getMarkDiscountValue()));
                paymentRepository.save(payment);

            }
        }
        System.out.println(mean);
        paymentRepository.saveAll(payments);
        StudentEntity student = studentService.getByRut(rut);
        student.setExam_mean_mark((int) mean);
        studentService.saveStudent(student);
        return null;
    }





    //permite calcular y redondear a 10 el valor de cada uno de los pagos
    public double calcAmountOfPayment(double amount, int numberOfPayments){
        return Math.round((amount / numberOfPayments) / 10) * 10;
    }

    public void generateEnrollment(String rut, double amount, LocalDate today){

        LocalDate tempToday = LocalDate.now();
        PaymentEntity matriculaPayment = new PaymentEntity();
        matriculaPayment.setAmount(amount);
        matriculaPayment.setRut(rut);
        matriculaPayment.setPaymentDate(today);
        matriculaPayment.setLastInterestCalculationDate(today.plusYears(10));
        matriculaPayment.setLastDiscountByMarkCalculationDate(tempToday.plusYears(10));
        matriculaPayment.setStatus(PaymentEntity.status.PAGADO);
        paymentRepository.save(matriculaPayment);

    }

    public void generatePayments(double PaymentAmount,double baseDiscount, int numberOfPayments, String rut, LocalDate today){

        if (numberOfPayments == 1){
            PaymentEntity payment = new PaymentEntity();
            payment.setAmount(PaymentAmount);
            payment.setRut(rut);
            payment.setPaymentDate(today);
            payment.setLastDiscountByMarkCalculationDate(today.plusYears(10));
            payment.setLastInterestCalculationDate(today.plusYears(10));
            payment.setStatus(PaymentEntity.status.PAGADO);
            paymentRepository.save(payment);
        } else {
            LocalDate paymentDate = today.withDayOfMonth(10);

             for (int i = 0; i < numberOfPayments; i++) {

                paymentDate = paymentDate.plusMonths(1);
                PaymentEntity payment = new PaymentEntity();
                payment.setAmount(PaymentAmount);
                payment.setRut(rut);
                payment.setPaymentDate(paymentDate);
                payment.setLastDiscountByMarkCalculationDate(LocalDate.now().minusMonths(2));
                payment.setLastInterestCalculationDate(LocalDate.now().minusMonths(2));
                payment.setStatus(PaymentEntity.status.PENDIENTE);
                payment.setBaseDiscountValue(baseDiscount);
                payment.setTotalAmount(PaymentAmount - (payment.getBaseDiscountValue() + payment.getMarkDiscountValue()));
                paymentRepository.save(payment);
            }
        }

    }

    public void processPayments(Long id_student, int cantidadCuotas) {

        double mount = 1500000;
        double percentBaseDiscount = 0;
        Optional<StudentEntity> student = studentService.getById(id_student);
        if (student.isEmpty() || !paymentRepository.findByRut(student.get().getRut()).isEmpty()) {
            return; // Retorna si el estudiante no existe
        }

        StudentEntity student_No_Null = student.get();
        student_No_Null.setQuantity_of_payments(cantidadCuotas);
        int quantifyOfPayments = student_No_Null.getQuantity_of_payments();

        if (quantifyOfPayments <= 0) {
            return; // Retorna si la cantidad de pagos es menor o igual a cero
        }

        percentBaseDiscount += discountBySchoolType(student_No_Null.getSchool_type());
        percentBaseDiscount += discountByPaymentMethod(quantifyOfPayments);
        percentBaseDiscount += discountByYearsOfGraduation(Year.now().getValue() - student_No_Null.getGraduation_year());

        double paymentPrice = calcAmountOfPayment(mount, quantifyOfPayments);
        double baseDiscountValue = calcPercentage(paymentPrice, percentBaseDiscount);

        LocalDate today = LocalDate.now();
        generateEnrollment(student_No_Null.getRut(), 70000, today);
        generatePayments(paymentPrice, baseDiscountValue, quantifyOfPayments, student_No_Null.getRut(), today);
        student_No_Null.setLast_payment_date(today);
        studentService.saveStudent(student_No_Null);
    }




    public void markAsPaid(String rut) {

        ArrayList<PaymentEntity> payments = paymentRepository.findByRut(rut);
        LocalDate today = LocalDate.now();

        for (PaymentEntity payment :
                payments) {
            if (payment.getPaymentDate().isBefore(today) || payment.getPaymentDate().getMonthValue() == today.getMonthValue()){
                payment.setStatus(PaymentEntity.status.PAGADO);

            }


        /*
        if(today.getDayOfMonth() <= 10 && today.getDayOfMonth() >= 5){
            Optional<PaymentEntity> payment = paymentRepository.findById(id);
            if(payment.isPresent()){

                StudentEntity student = studentService.getByRut(payment.get().getRut());
                student.setPayments_taken(student.getPayments_taken() + 1);
                PaymentEntity payment_No_Null = payment.get();
                payment_No_Null.setStatus(PaymentEntity.status.PAGADO);
                paymentRepository.save(payment_No_Null);
                studentService.saveStudent(student);

            }

        }
         */

        }
        paymentRepository.saveAll(payments);

    }


    public StudentEntity report( Long id){

        Optional<StudentEntity> student = studentService.getById(id);

        if(student.isPresent()){
            StudentEntity student_no_null = student.get();
            ArrayList<PaymentEntity> payments = paymentRepository.findByRut(student.get().getRut());
            ArrayList<ExamEntity> exams = examService.getByRut(student.get().getRut());
            if( !payments.isEmpty()  && !exams.isEmpty()){

                int totalPaymentsPaid = 0;
                int pendingPayments = 0;
                double totalAmountPaid = 0;
                double totalAmountToPay = 0;
                LocalDate lastPaymentDate = payments.get(payments.size()-1).getPaymentDate();
                LocalDate today = LocalDate.now();
                int unpaidMonths = countAndMarkUnpaidMonths(payments, today);
                double mean = 0;


                for (PaymentEntity payment :
                        payments) {
                    if (payment.getStatus().equals(PaymentEntity.status.PAGADO)) {

                        totalPaymentsPaid++;
                        totalAmountPaid += payment.getAmount();
                        totalAmountToPay += payment.getTotalAmount();

                    } else if (payment.getStatus().equals(PaymentEntity.status.PENDIENTE)) {

                        pendingPayments++;
                    }
                }


                for (ExamEntity exam:
                        exams){
                    mean += exam.getMark();
                }
                mean /= exams.size();


                student_no_null.setExams_taken(exams.size());
                student_no_null.setExam_mean_mark((int)mean);
                student_no_null.setAmount_to_pay(1500000);

                student_no_null.setPayments_taken(totalPaymentsPaid);
                student_no_null.setPending_payments(pendingPayments);
                student_no_null.setAmount_paid(totalAmountPaid);
                student_no_null.setPending_amount((int) (student_no_null.getAmount_to_pay() - totalAmountPaid));

            }
            studentService.saveStudent(student_no_null);
            return  student_no_null;
        }

        return null;
    }





}

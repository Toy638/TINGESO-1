package edu.mtisw.monolithicwebapp;

import edu.mtisw.monolithicwebapp.entities.ExamEntity;
import edu.mtisw.monolithicwebapp.entities.PaymentEntity;
import edu.mtisw.monolithicwebapp.entities.StudentEntity;

import edu.mtisw.monolithicwebapp.repositories.ExamRepository;
import edu.mtisw.monolithicwebapp.repositories.PaymentRepository;
import edu.mtisw.monolithicwebapp.services.ExamService;
import edu.mtisw.monolithicwebapp.services.PaymentService;
import edu.mtisw.monolithicwebapp.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class PaymentServiceTest {


    @InjectMocks
    private PaymentService paymentService;

    @InjectMocks
    private ExamService examService;

    @InjectMocks
    private StudentService studentService;


    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ExamRepository examServiceMock;



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testIsUnpaid_PaymentIsPaidAndExpirationDateBeforeToday_ReturnsFalse() {
        PaymentEntity payment = new PaymentEntity();
        payment.setStatus(PaymentEntity.status.PAGADO);
        LocalDate expirationDate = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        boolean result = paymentService.isUnpaid(payment, expirationDate, today);

        assertFalse(result);
    }


    @Test
    public void testIsUnpaid_PaymentIsPaidAndExpirationDateAfterToday_ReturnsFalse() {
        PaymentEntity payment = new PaymentEntity();
        payment.setStatus(PaymentEntity.status.PENDIENTE);
        LocalDate expirationDate = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();

        boolean result = paymentService.isUnpaid(payment, expirationDate, today);

        assertTrue(result);
    }




    @Test
    public void testInterestByMonths_UnpaidMonthsIs0_Returns0() {
        double result = paymentService.interestByMonths(0);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    public void testInterestByMonths_UnpaidMonthsIs1_Returns0_03() {
        double result = paymentService.interestByMonths(1);
        assertEquals(0.03, result, 0.001); // Utilizamos delta para manejar valores flotantes
    }

    @Test
    public void testInterestByMonths_UnpaidMonthsIs2_Returns0_06() {
        double result = paymentService.interestByMonths(2);
        assertEquals(0.06, result, 0.001);
    }

    @Test
    public void testInterestByMonths_UnpaidMonthsIs3_Returns0_09() {
        double result = paymentService.interestByMonths(3);
        assertEquals(0.09, result, 0.001);
    }

    @Test
    public void testInterestByMonths_UnpaidMonthsIsGreaterThan3_Returns0_15() {
        double result = paymentService.interestByMonths(4);
        assertEquals(0.15, result, 0.001);
    }


    @Test
    public void testDiscountBySchoolTypeMunicipalReturnsCorrectDiscount() {
        StudentEntity.Schooltype schoolType = StudentEntity.Schooltype.Municipal;
        double expectedDiscount = 0.2;

        double actualDiscount = paymentService.discountBySchoolType(schoolType);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountBySchoolTypeSubvencionadoReturnsCorrectDiscount() {
        StudentEntity.Schooltype schoolType = StudentEntity.Schooltype.Subvencionado;
        double expectedDiscount = 0.1;

        double actualDiscount = paymentService.discountBySchoolType(schoolType);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountBySchoolTypePrivadoReturnsCorrectDiscount() {
        StudentEntity.Schooltype schoolType = StudentEntity.Schooltype.Privado;
        double expectedDiscount = 0;

        double actualDiscount = paymentService.discountBySchoolType(schoolType);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByPaymentMethod_OnePayment_ReturnsDiscount() {
        int quantifyOfPayments = 1;
        double expectedDiscount = 0.5;

        double actualDiscount = paymentService.discountByPaymentMethod(quantifyOfPayments);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByPaymentMethod_MoreThanOnePayment_ReturnsZeroDiscount() {
        int quantifyOfPayments = 2; // Cambia a la cantidad que desees
        double expectedDiscount = 0.0;

        double actualDiscount = paymentService.discountByPaymentMethod(quantifyOfPayments);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByYearsOfGraduation_LessThanOneYear_ReturnsDiscount() {
        int yearsOfGraduation = 0; // Cambia a menos de 1 año si lo deseas
        double expectedDiscount = 0.15;

        double actualDiscount = paymentService.discountByYearsOfGraduation(yearsOfGraduation);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByYearsOfGraduation_OneYear_ReturnsDiscount() {
        int yearsOfGraduation = 1;
        double expectedDiscount = 0.08;

        double actualDiscount = paymentService.discountByYearsOfGraduation(yearsOfGraduation);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByYearsOfGraduation_TwoYears_ReturnsDiscount() {
        int yearsOfGraduation = 2;
        double expectedDiscount = 0.04;

        double actualDiscount = paymentService.discountByYearsOfGraduation(yearsOfGraduation);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByYearsOfGraduation_MoreThanTwoYears_ReturnsZeroDiscount() {
        int yearsOfGraduation = 3; // Cambia a más de 2 años si lo deseas
        double expectedDiscount = 0.0;

        double actualDiscount = paymentService.discountByYearsOfGraduation(yearsOfGraduation);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByMark_MeanMarkInRange1_ReturnsDiscount1() {
        int meanMark = 975; // Cambia el valor dentro del rango
        double expectedDiscount = 0.1;

        double actualDiscount = paymentService.discountByMark(meanMark);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByMark_MeanMarkInRange2_ReturnsDiscount2() {
        int meanMark = 925; // Cambia el valor dentro del rango
        double expectedDiscount = 0.05;

        double actualDiscount = paymentService.discountByMark(meanMark);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByMark_MeanMarkInRange3_ReturnsDiscount3() {
        int meanMark = 875; // Cambia el valor dentro del rango
        double expectedDiscount = 0.02;

        double actualDiscount = paymentService.discountByMark(meanMark);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testDiscountByMark_MeanMarkOutOfRange_ReturnsZeroDiscount() {
        int meanMark = 800; // Cambia el valor fuera del rango
        double expectedDiscount = 0.0;

        double actualDiscount = paymentService.discountByMark(meanMark);

        assertEquals(expectedDiscount, actualDiscount, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testCalcPercentage_PositiveAmountAndPositiveInterest_ReturnsCorrectResult() {
        double amount = 1000.0;
        double interest = 0.1;
        double expectedPercentage = 100.0;

        double actualPercentage = paymentService.calcPercentage(amount, interest);

        assertEquals(expectedPercentage, actualPercentage, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testCalcPercentage_NegativeAmountAndPositiveInterest_ReturnsCorrectResult() {
        double amount = -500.0;
        double interest = 0.05;
        double expectedPercentage = -25.0;

        double actualPercentage = paymentService.calcPercentage(amount, interest);

        assertEquals(expectedPercentage, actualPercentage, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testCalcPercentage_ZeroAmountAndPositiveInterest_ReturnsZero() {
        double amount = 0.0;
        double interest = 0.1;
        double expectedPercentage = 0.0;

        double actualPercentage = paymentService.calcPercentage(amount, interest);

        assertEquals(expectedPercentage, actualPercentage, 0.001); // Ajusta la precisión según tus necesidades
    }

    @Test
    public void testCalcPercentage_PositiveAmountAndZeroInterest_ReturnsZero() {
        double amount = 750.0;
        double interest = 0.0;
        double expectedPercentage = 0.0;

        double actualPercentage = paymentService.calcPercentage(amount, interest);

        assertEquals(expectedPercentage, actualPercentage, 0.001); // Ajusta la precisión según tus necesidades
    }


    @Test
    public void testOneMonthSinceLastCalc_TodayAndLastCalcAreInDifferentMonths_ReturnsTrue() {
        LocalDate today = LocalDate.of(2023, 9, 15);
        LocalDate lastCalc = LocalDate.of(2023, 8, 20);

        boolean result = paymentService.oneMonthSinceLastCalc(today, lastCalc);

        assertTrue(result);
    }


    @Test
    public void testOneMonthSinceLastCalc_TodayAndLastCalcAreInSameMonth_ReturnsFalse() {
        LocalDate today = LocalDate.of(2023, 9, 15);
        LocalDate lastCalc = LocalDate.of(2023, 9, 5);

        boolean result = paymentService.oneMonthSinceLastCalc(today, lastCalc);

        assertFalse(result);
    }

    @Test
    public void testOneMonthSinceLastCalc_TodayIsBeforeLastCalcInSameMonth_ReturnsFalse() {
        LocalDate today = LocalDate.of(2023, 9, 5);
        LocalDate lastCalc = LocalDate.of(2023, 9, 15);

        boolean result = paymentService.oneMonthSinceLastCalc(today, lastCalc);

        assertFalse(result);
    }


    @Test
    public void testOneMonthSinceLastCalc_TodayIsAfterLastCalcInDifferentMonths_ReturnsTrue() {
        LocalDate today = LocalDate.of(2023, 10, 5);
        LocalDate lastCalc = LocalDate.of(2023, 9, 15);

        boolean result = paymentService.oneMonthSinceLastCalc(today, lastCalc);

        assertTrue(result);
    }


    @Test
    public void testCountAndMarkUnpaidMonths_EmptyPaymentsList_ReturnsZero() {
        ArrayList<PaymentEntity> payments = new ArrayList<>();
        LocalDate today = LocalDate.now();

        int result = paymentService.countAndMarkUnpaidMonths(payments, today);

        assertEquals(0, result);
    }


    @Test
    public void testCountAndMarkUnpaidMonths_AllPaymentsArePaid_ReturnsZero() {
        PaymentEntity payment1 = new PaymentEntity();
        payment1.setStatus(PaymentEntity.status.PAGADO);
        PaymentEntity payment2 = new PaymentEntity();
        payment2.setStatus(PaymentEntity.status.PAGADO);
        ArrayList<PaymentEntity> payments = new ArrayList<>(Arrays.asList(payment1, payment2));
        LocalDate today = LocalDate.now();

        int result = paymentService.countAndMarkUnpaidMonths(payments, today);

        assertEquals(0, result);
    }


    @Test
    public void testCountAndMarkUnpaidMonths_SomePaymentsAreUnpaid_ReturnsCount() {
        PaymentEntity payment1 = new PaymentEntity();
        payment1.setStatus(PaymentEntity.status.PENDIENTE);
        payment1.setPaymentDate(LocalDate.now().minusMonths(1)); // Fecha anterior a 'today'

        PaymentEntity payment2 = new PaymentEntity();
        payment2.setStatus(PaymentEntity.status.PAGADO);
        payment2.setPaymentDate(LocalDate.now().minusMonths(1)); // Fecha anterior a 'today'


        PaymentEntity payment3 = new PaymentEntity();
        payment3.setStatus(PaymentEntity.status.ATRASADO);
        payment3.setPaymentDate(LocalDate.now().minusMonths(2)); // Fecha anterior a 'today'

        ArrayList<PaymentEntity> payments = new ArrayList<>(Arrays.asList(payment1, payment2, payment3));
        LocalDate today = LocalDate.now();

        int result = paymentService.countAndMarkUnpaidMonths(payments, today);

        assertEquals(2, result); // Dos pagos están atrasados
    }


    @Test
    public void testGetPayments_NoPayments_ReturnsEmptyList() {
        when(paymentRepository.findAll()).thenReturn(new ArrayList<>());

        ArrayList<PaymentEntity> payments = paymentService.getPayments();

        assertEquals(0, payments.size());
    }

    @Test
    public void testGetPayments_SomePayments_ReturnsList() {
        PaymentEntity payment1 = new PaymentEntity();
        PaymentEntity payment2 = new PaymentEntity();
        ArrayList<PaymentEntity> mockPayments = new ArrayList<>(Arrays.asList(payment1, payment2));

        when(paymentRepository.findAll()).thenReturn(mockPayments);

        ArrayList<PaymentEntity> payments = paymentService.getPayments();

        assertEquals(2, payments.size());
    }

    @Test

    public void testGetPaymentsByRut() {


        String rut = "123456789";
        PaymentEntity payment1 = new PaymentEntity();
        payment1.setRut(rut);
        PaymentEntity payment2 = new PaymentEntity();
        payment2.setRut(rut);
        ArrayList<PaymentEntity> expectedPayments = new ArrayList<>(Arrays.asList(payment1, payment2));

        when(paymentRepository.findByRut(rut)).thenReturn(expectedPayments);

        ArrayList<PaymentEntity> result = paymentService.getByRut(rut);

        assertEquals(expectedPayments, result);
    }


    @Test
    public void testGetPaymentById() {

        Long paymentId = 1L;
        PaymentEntity expectedPayment = new PaymentEntity();
        expectedPayment.setId(paymentId);


        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(expectedPayment));

        // Llama a la función que se va a probar
        Optional<PaymentEntity> result = paymentService.getById(paymentId);

        // Verifica que la función devuelva el pago esperado
        assertTrue(result.isPresent());  // Asegura que se devolvió un valor presente
        assertEquals(expectedPayment, result.get());
    }

    @Test
    public void testGetPaymentById_NonExistentId() {

        Long nonExistentId = 999L;


        when(paymentRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        Optional<PaymentEntity> result = paymentService.getById(nonExistentId);


        assertFalse(result.isPresent());
    }


    @Test
    public void testDeletePayment_Success() {
        Long paymentId = 1L;

        doNothing().when(paymentRepository).deleteById(paymentId);

        boolean result = paymentService.deletePayment(paymentId);

        assertTrue(result);

        verify(paymentRepository).deleteById(paymentId);
    }


    @Test
    public void testSavePayment_Success() {
        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L); // Establece un ID simulado

        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentEntity result = paymentService.savePayment(payment);

        assertNotNull(result);

        verify(paymentRepository).save(payment);
    }

    @Test
    public void testApplyInterest_NoPayments() {
        // Simula que no se encontraron pagos para el rut proporcionado
        when(paymentRepository.findByRut(eq("123456789"))).thenReturn(new ArrayList<>());

        ArrayList<PaymentEntity> result = paymentService.applyInterest("123456789");

        // Verifica que el resultado sea una lista vacía
        assertTrue(result.isEmpty());
    }


}

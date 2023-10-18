package edu.mtisw.monolithicwebapp;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Optional;

import edu.mtisw.monolithicwebapp.entities.ExamEntity;
import edu.mtisw.monolithicwebapp.repositories.ExamRepository;
import edu.mtisw.monolithicwebapp.services.ExamService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExamServiceTest {


    @InjectMocks
    private ExamService examService;

    @Mock
    private ExamRepository examRepository;

    @Test
    public void testGetExams() {
        // Crea una lista de exámenes simulados
        ArrayList<ExamEntity> exams = new ArrayList<>();
        exams.add(new ExamEntity());
        exams.add(new ExamEntity());

        // Simula que el repositorio devuelve la lista de exámenes
        when(examRepository.findAll()).thenReturn(exams);

        // Llama al método para obtener exámenes y verifica que la lista no esté vacía
        ArrayList<ExamEntity> result = examService.getExams();
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    public void testSaveExam() {
        // Crea un examen simulado
        ExamEntity exam = new ExamEntity();
        exam.setMark(1000);

        // Simula el comportamiento de guardar un examen en el repositorio
        when(examRepository.save(exam)).thenReturn(exam);

        // Llama al método para guardar el examen y verifica que el resultado sea el mismo examen
        ExamEntity result = examService.saveExam(exam);
        assertEquals(exam, result);
    }

    @Test
    public void testGetExamsByRut() {
        // Define un rut de ejemplo y crea una lista de exámenes simulados
        String rut = "123456789";
        ArrayList<ExamEntity> exams = new ArrayList<>();
        ExamEntity exam = new ExamEntity();
        exam.setRut(rut);

        exams.add(exam);
        exams.add(exam);

        // Simula la búsqueda de exámenes por rut
        when(examRepository.findByRut(rut)).thenReturn(exams);

        // Llama al método para obtener exámenes por rut y verifica que la lista no esté vacía
        ArrayList<ExamEntity> result = examService.getByRut(rut);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    public void testDeleteExam_Success() {

        // Simula que se puede eliminar un examen con éxito
        when(examRepository.existsById(1L)).thenReturn(true);

        boolean result = examService.deleteExam(1L);

        assertTrue(result);

        // Verifica que se llamó al repositorio para eliminar el examen
        Mockito.verify(examRepository).deleteById(1L);
    }


    @Test
    public void testGetExamById() {
        // Crea un examen simulado
        ExamEntity exam = new ExamEntity();

        // Simula la búsqueda de un examen por su ID
        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

        // Llama al método para obtener un examen por ID y verifica que el resultado sea el mismo examen
        Optional<ExamEntity> result = examService.getById(1L);
        assertTrue(result.isPresent());
        assertEquals(exam, result.get());
    }

    @Test
    public void testMeanMark() {
        // Define un rut de ejemplo
        String rut = "123456789";

        // Crea una lista de exámenes simulados con diferentes calificaciones
        ArrayList<ExamEntity> exams = new ArrayList<>();

        exams.add(createExam(rut, 90)); // Calificación: 90
        exams.add(createExam(rut, 75)); // Calificación: 75
        exams.add(createExam(rut, 85)); // Calificación: 85

        // Simula la obtención de exámenes por rut
        when(examRepository.findByRut(rut)).thenReturn(exams);

        // Llama al método para calcular la calificación promedio
        double result = examService.mean_Mark(rut);

        // Verifica que el resultado sea el promedio correcto (83.33 con dos decimales)
        assertEquals(83.33, result, 0.01);
    }

    // Método de utilidad para crear un examen simulado con una calificación
    private ExamEntity createExam(String rut, int mark) {
        ExamEntity exam = new ExamEntity();
        exam.setRut(rut);
        exam.setMark(mark);
        return exam;
    }

}

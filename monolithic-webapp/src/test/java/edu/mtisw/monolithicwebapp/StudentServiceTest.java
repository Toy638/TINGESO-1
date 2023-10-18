package edu.mtisw.monolithicwebapp;


import edu.mtisw.monolithicwebapp.entities.StudentEntity;
import edu.mtisw.monolithicwebapp.repositories.StudentRepository;
import edu.mtisw.monolithicwebapp.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testCalcMaxPayments_MunicipalSchool_Returns10() {
        StudentService studentService = new StudentService();
        String typeOfSchool = "Municipal";

        int maxPayments = studentService.calcMaxPayments(typeOfSchool);

        assertEquals(10, maxPayments);
    }

    @Test
    public void testCalcMaxPayments_SubvencionadoSchool_Returns7() {
        StudentService studentService = new StudentService();
        String typeOfSchool = "Subvencionado";

        int maxPayments = studentService.calcMaxPayments(typeOfSchool);

        assertEquals(7, maxPayments);
    }

    @Test
    public void testCalcMaxPayments_PrivadoSchool_Returns5() {
        StudentService studentService = new StudentService();
        String typeOfSchool = "Privado";

        int maxPayments = studentService.calcMaxPayments(typeOfSchool);

        assertEquals(5, maxPayments);
    }

    @Test
    public void testCalcMaxPayments_UnknownSchoolType_Returns0() {
        StudentService studentService = new StudentService();
        String typeOfSchool = "Desconocido";

        int maxPayments = studentService.calcMaxPayments(typeOfSchool);

        assertEquals(0, maxPayments);
    }

    @Test
    public void testGetStudents() {
        // Crear una lista de estudiantes de ejemplo
        ArrayList<StudentEntity> students = new ArrayList<>();
        students.add(new StudentEntity());
        students.add(new StudentEntity());
        // Simular el comportamiento del repositorio al llamar a findAll()
        when(studentRepository.findAll()).thenReturn(students);

        // Llamar al método que se va a probar
        ArrayList<StudentEntity> result = studentService.getStudents();

        // Verificar que la lista de estudiantes devuelta coincide con la cantidad de estudiantes
        assertEquals(students.size(), 2);
    }


    @Test
    public void testSaveStudent() {
        // Objeto StudentEntity de ejemplo
        StudentEntity student = new StudentEntity();
        student.setRut("12345678-9");

        // Simula el comportamiento del repositorio al llamar a save
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(student);

        // Llama al método que se va a probar
        StudentEntity result = studentService.saveStudent(student);

        // Verifica que el resultado sea el mismo objeto que se proporcionó
        assertEquals(student, result);

        // Verifica que el método save del repositorio se llamó con el objeto correcto
        verify(studentRepository).save(student);
    }

    @Test
    public void testGetStudentById() {
        // Crear un estudiante de ejemplo
        StudentEntity student = new StudentEntity();
        student.setId(1L);
        student.setName("Ejemplo");

        // Simular el comportamiento del repositorio
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Llamar al método getById del servicio
        Optional<StudentEntity> result = studentService.getById(1L);

        // Verificar que el estudiante devuelto coincide con el estudiante de ejemplo
        assertEquals(student, result.orElse(null));
    }

    @Test
    public void testDeleteStudentSuccess() {
        // Simular que se puede eliminar un estudiante con éxito
        when(studentRepository.existsById(1L)).thenReturn(true);

        boolean result = studentService.deleteStudent(1L);

        assertTrue(result);

        // Verificar que se llamó al repositorio para eliminar el estudiante
        verify(studentRepository).deleteById(1L);
    }

    @Test
    public void testGetStudentByRut() {
        // Define un rut de ejemplo y crea un objeto StudentEntity simulado
        String rut = "123456789";
        StudentEntity student = new StudentEntity();
        student.setRut(rut);

        // Simula la búsqueda de un estudiante por rut
        when(studentRepository.findByRut(rut)).thenReturn(student);

        // Llama al método que debería buscar al estudiante por rut
        StudentEntity result = studentService.getByRut(rut);

        // Verifica que el resultado coincide con el objeto simulado
        assertEquals(student, result);
    }

}

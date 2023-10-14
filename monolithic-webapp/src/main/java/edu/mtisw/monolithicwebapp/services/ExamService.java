package edu.mtisw.monolithicwebapp.services;


import edu.mtisw.monolithicwebapp.entities.ExamEntity;
import edu.mtisw.monolithicwebapp.repositories.ExamRepository;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ExamService {


    @Autowired
    ExamRepository examRepository;

    public ArrayList<ExamEntity> getExams() {
        return (ArrayList<ExamEntity>) examRepository.findAll();
    }

    public ExamEntity saveExam(ExamEntity exam){
        return examRepository.save(exam);
    }

    public ArrayList<ExamEntity> getByRut(String rut){
        return examRepository.findByRut(rut);
    }

    public boolean deleteExam(Long id){
        try {
            examRepository.deleteById(id);
            return true;
        }catch (Exception err){
            return false;
        }
    }

    public Optional<ExamEntity> getById(Long id) {
        return examRepository.findById(id);
    }


    private final Logger logg = LoggerFactory.getLogger(ExamService.class);


    @Generated
    public String saveFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if(filename != null){
            if(!file.isEmpty()){
                try{
                    byte [] bytes = file.getBytes();
                    Path path  = Paths.get(file.getOriginalFilename());
                    Files.write(path, bytes);
                    logg.info("Archivo guardado");
                }
                catch (IOException e){
                    logg.error("ERROR", e);
                }
            }
            return "Archivo guardado con exito!";
        }
        else{
            return "No se pudo guardar el archivo";
        }
    }

    public void saveExamDB(String rut, String date, String examMeanMark){
        int mark = Integer.parseInt(examMeanMark);
        ExamEntity newExam = new ExamEntity();
        newExam.setRut(rut);
        newExam.setDate(date);
        newExam.setMark(mark);
        saveExam(newExam);

    }



    @Generated
    public void leerCsv(String direccion){
        String texto = "";
        BufferedReader bf = null;
        //examRepository.deleteAll();
        try{
            bf = new BufferedReader(new FileReader(direccion));
            String temp = "";
            String bfRead;
            int count = 1;
            while((bfRead = bf.readLine()) != null){
                if (count == 1){
                    count = 0;
                }
                else{
                    saveExamDB(bfRead.split(";")[0], bfRead.split(";")[1], bfRead.split(";")[2]);
                    temp = temp + "\n" + bfRead;
                }
            }
            texto = temp;
            System.out.println("Archivo leido exitosamente");
        }catch(Exception e){
            System.err.println("No se encontro el archivo");
        }finally{
            if(bf != null){
                try{
                    bf.close();
                }catch(IOException e){
                    logg.error("ERROR", e);
                }
            }
        }
    }

    public double mean_Mark(String rut){
        ArrayList<ExamEntity> exams = getByRut(rut);
        double mean = 0;
        for (ExamEntity exam : exams) {
            mean += exam.getMark();
        }
        mean = mean / exams.size();
        return mean;

    }


}

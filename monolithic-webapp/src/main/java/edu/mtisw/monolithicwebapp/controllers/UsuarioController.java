package edu.mtisw.monolithicwebapp.controllers;

import edu.mtisw.monolithicwebapp.entities.UsuarioEntity;
import edu.mtisw.monolithicwebapp.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping
public class UsuarioController {
    @Autowired
	UsuarioService usuarioService;

    @GetMapping("/listar")
	public String listar(Model model) {
    	ArrayList<UsuarioEntity>usuarios=usuarioService.obtenerUsuarios();
    	model.addAttribute("usuarios",usuarios);
		return "index";
	}


	@PostMapping("/usuario/nuevo")
	public UsuarioEntity guardar(Model modelo){
		UsuarioEntity estudiante = new UsuarioEntity();
		modelo.addAttribute("estudiante", estudiante);
		return "crear_estudiante"


		return usuarioService.guardarUsuario(usuarioEntityNuevo);
	}




}
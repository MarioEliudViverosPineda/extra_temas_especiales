package servicio.extraordinario.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import servicio.extraordinario.model.PersonaEntity;
import servicio.extraordinario.service.UsuarioService;

@Controller
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class IniciarSecionController {

    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "usuario/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "usuario/registro";
    }

    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String contrasena,
                        Model model) {
        try {
            usuarioService.login(correo, contrasena);
            return "redirect:/archivos";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "usuario/login";
        }
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute PersonaEntity usuario, Model model) {
        try {
            usuarioService.registrar(usuario);
            return "redirect:/usuario/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "usuario/registro";
        }
    }

}

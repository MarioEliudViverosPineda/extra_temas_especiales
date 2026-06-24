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

    /**
     * Servicio que permite ir a la vista de loging.
     *
     * @return
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "usuario/loging";
    }

    /**
     * Servicio que permite ingresar al registro.
     *
     * @return
     */
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "usuario/registro";
    }

    /**
     * Servicio que permite loggear con el usuario registrado.
     *
     * @param correo
     * @param contrasena
     * @param model
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String contrasena,
                        Model model) {
        try {
            usuarioService.login(correo, contrasena);
            return "redirect:/archivos";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "usuario/loging";
        }
    }

    /**
     * Servicio que permite regitrar al usuario.
     *
     * @param usuario
     * @param model
     * @return
     */
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

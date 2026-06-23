package servicio.extraordinario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/productos")
public class IniciarSecionController {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // plantilla Thymeleaf para login
    }


}

package servicio.extraordinario.service;

import servicio.extraordinario.model.PersonaEntity;

public interface UsuarioService {
    PersonaEntity registrar(PersonaEntity usuario);
    PersonaEntity login(String correo, String contrasena);
}

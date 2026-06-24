package servicio.extraordinario.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import servicio.extraordinario.model.PersonaEntity;
import servicio.extraordinario.repository.UsuarioRepository;

@Service
@Slf4j
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public PersonaEntity registrar(PersonaEntity usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public PersonaEntity login(String correo, String contrasena) {
        return usuarioRepository.findByCorreo(correo)
                .filter(u -> u.getContrasena().equals(contrasena))
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas."));
    }
}

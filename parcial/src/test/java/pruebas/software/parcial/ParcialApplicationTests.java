package pruebas.software.parcial;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pruebas.software.parcial.repository.UsuarioRepository;

@SpringBootTest
class ParcialApplicationTests {

    @MockBean
    UsuarioRepository usuarioRepository;

    @Test
    void contextLoads() { }
}
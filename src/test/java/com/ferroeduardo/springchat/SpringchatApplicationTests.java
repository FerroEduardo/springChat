package com.ferroeduardo.springchat;

import com.ferroeduardo.springchat.Chat.ChatMessage;
import com.ferroeduardo.springchat.Usuario.Usuario;
import com.ferroeduardo.springchat.Repository.ChatMessageRepository;
import com.ferroeduardo.springchat.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration
class SpringchatApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void init() {
        passwordEncoder = new BCryptPasswordEncoder();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
        assertNotNull(chatMessageRepository);
        assertNotNull(usuarioRepository);
        assertNotNull(mockMvc);
        assertNotNull(passwordEncoder);
    }

    @Test
    public void testarBancoDeDadosUsuarios() {
        /*
         1->Save a User directly to the Database
         2->Make some assertions
         3->Delete the saved User from the Database
         */
        try {
            String usuario = "username";
            String senha = passwordEncoder.encode("password");
            Date data = new Date();
            String ip = "192.168.1.1";
            String roles = "ROLE_USER";
            boolean ativada = true;
            Usuario usuarioObj = new Usuario(usuario, senha, data, ip, roles, ativada);
            assertEquals(usuarioObj.getUsuario(), usuario);
            assertEquals(usuarioObj.getSenha(), senha);
            assertEquals(usuarioObj.getData(), data);
            assertEquals(usuarioObj.getIp(), ip);
            assertEquals(usuarioObj.getRoles(), roles);
            assertEquals(usuarioObj.isAtivada(), ativada);

            Usuario usuarioResultadoSave = usuarioRepository.save(usuarioObj);

            Long usuarioId = usuarioResultadoSave.getId();
            assertNotNull(usuarioId);
            assertEquals(usuarioResultadoSave, usuarioObj);

            Usuario usuarioResultadoFind = usuarioRepository.findById(usuarioId).orElse(null);
            assertNotNull(usuarioResultadoFind);
            assertEquals(usuarioResultadoFind, usuarioResultadoSave);
            assertEquals(usuarioResultadoFind, usuarioObj);

            usuarioRepository.deleteById(usuarioId);
            Usuario usuarioResultadoDeleted = usuarioRepository.findById(usuarioId).orElse(null);
            assertNull(usuarioResultadoDeleted);

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testarBancoDeDadosMensagens() {
        /*
         1->Save a Message directly to the Database
         2->Make some assertions
         3->Delete the saved Message from the Database
         */
        try {
            String author = "author";
            String message = "message";
            Date date = new Date();
            String channel = "channel";
            String ip = "192.168.1.1";

            ChatMessage chatMessage = new ChatMessage(author, message, date, channel, ip);
            assertEquals(chatMessage.getAuthor(), author);
            assertEquals(chatMessage.getMessage(), message);
            assertEquals(chatMessage.getDate(), date);
            assertEquals(chatMessage.getChannel(), channel);
            assertEquals(chatMessage.getIp(), ip);

            ChatMessage chatMessageSave = chatMessageRepository.save(chatMessage);
            Long messageId = chatMessageSave.getId();
            assertNotNull(messageId);
            assertEquals(chatMessageSave, chatMessage);

            ChatMessage chatMessageFind = chatMessageRepository.findById(messageId).orElse(null);
            assertNotNull(chatMessageFind);
            assertEquals(chatMessageFind, chatMessage);
            assertEquals(chatMessageFind, chatMessageSave);

            chatMessageRepository.deleteById(messageId);
            ChatMessage chatMessageDeleted = chatMessageRepository.findById(messageId).orElse(null);
            assertNull(chatMessageDeleted);

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void loginPage() {
        /*
         1->Save a account directly to the Database
         2->Check if "/login" contains a UNIQUE string
         3->Make POST request, simulating a login form using the created account
         4->Delete the account that is created
         5->Re-checks if was deleted from the Database
         */
        Usuario usuario = getDefaultUser(false);
        String password = usuario.getSenha();
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario usuarioSaved = usuarioRepository.save(usuario);
        try {
            mockMvc.perform(get("/login"))
//                    .andDo(print())
                    .andExpect(content().string(containsString("Acessar springChat")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> credentials = new LinkedMultiValueMap<>();
            credentials.add("username", usuario.getUsuario());
            credentials.add("password", password);

            mockMvc.perform(post("/login").params(credentials).with(csrf()))
//                    .andDo(print())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/chat"));

        } catch (Exception e) {
            fail(e);
        } finally {
            // Guarantee that it is deleted from the database
            try {
                // If empty(not in the database), returns null
                // If returns something(in the database), return the object and then deleted
                if (usuarioRepository.findByUsuario(usuario.getUsuario()).orElse(null) != null) {
                    throw new IllegalStateException("The user is still in the database");
                }
            } catch (IllegalStateException e) {
//                e.printStackTrace();
                Usuario usuarioFind = usuarioRepository.findByUsuario(usuario.getUsuario()).orElse(null);
                assertNotNull(usuarioFind);
                long usuarioId = usuarioFind.getId();
                usuarioRepository.deleteById(usuarioId);
            }
        }
    }

    @Test
    public void signupPage() {
        /*
         1->Check if "/signup" contains a UNIQUE string
         2->Make POST request, simulating a signup form
         3->Delete the account that is created
         4->Re-checks if was deleted
         */
        Usuario usuario = getDefaultUser(false);
        try {
            mockMvc.perform(get("/signup"))
//                    .andDo(print())
                    .andExpect(content().string(containsString("Criar conta no springChat")))
                    .andExpect(status().isOk());


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> credentials = new LinkedMultiValueMap<>();
            // Está usuario/senha pq é atributo da classe(th:field)
            credentials.add("usuario", usuario.getUsuario());
            credentials.add("senha", usuario.getSenha());

            mockMvc.perform(post("/signup").params(credentials).with(csrf()))
//                    .andDo(print())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/signup?success"));

            Usuario usuarioFind = usuarioRepository.findByUsuario(usuario.getUsuario()).orElse(null);
            assertNotNull(usuarioFind);
            long usuarioId = usuarioFind.getId();
            usuarioRepository.deleteById(usuarioId);
        } catch (Exception e) {
            fail(e);
        } finally {
            // Guarantee that it is deleted from the database
            try {
                // If empty(not in the database), returns null
                // If returns something(in the database), return the object and then deleted
                if (usuarioRepository.findByUsuario(usuario.getUsuario()).orElse(null) != null) {
                    throw new IllegalStateException("The user is still in the database");
                }
            } catch (IllegalStateException e) {
//                e.printStackTrace();
                Usuario usuarioFind = usuarioRepository.findByUsuario(usuario.getUsuario()).orElse(null);
                assertNotNull(usuarioFind);
                long usuarioId = usuarioFind.getId();
                usuarioRepository.deleteById(usuarioId);
            }
        }
    }

    private Usuario getDefaultUser(boolean encoded) {
        String usuario = "username";
        String senha = encoded ? passwordEncoder.encode("password") : "password";
        Date data = new Date();
        String ip = "192.168.1.1";
        String roles = "ROLE_USER";
        boolean ativada = true;
        return new Usuario(usuario, senha, data, ip, roles, ativada);
    }
}

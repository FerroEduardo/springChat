package com.ferroeduardo.springchat.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferroeduardo.springchat.Chat.ChatMessageSafeData;
import com.ferroeduardo.springchat.Usuario.UserRole;
import com.ferroeduardo.springchat.Usuario.Usuario;
import com.ferroeduardo.springchat.Usuario.UsuarioSafeData;
import com.ferroeduardo.springchat.Repository.ChatMessageRepository;
import com.ferroeduardo.springchat.Repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private static final int PAGEABLE_SIZE = 20;

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final ChatMessageRepository messageRepository;

    public AdminController(PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, ChatMessageRepository messageRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/")
    public String getIndexRedirect(HttpServletRequest request) {
        return "redirect:/admin/panel";
    }

    // Single template to multiple purposes
    @GetMapping(
            value={
                    "/panel",
                    "/panel/usuarios",
                    "/panel/alterarpermissoes",
                    "/panel/mensagens",
                    "/panel/mensagens/todas",
                    "/panel/mensagens/id",
                    "/panel/mensagens/conteudo",
                    "/panel/mensagens/autor",
                    "/panel/alterarativado"
                }
            )
    public String getPanelAlterarPermissoes(Model model, HttpServletRequest request, @RequestParam(required = false) Integer page) {
        return "panelAdmin";
    }

    @PostMapping(value="/panel/alterarpermissoes")
    public String postAlterarPermissoes(HttpServletRequest request, @RequestBody String payload, Authentication authentication) {
        Optional<String> username;
        Optional<UserRole> userRole;
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                String roleOption = request.getParameter("roleOption").replace("%2C",",");
                username = Optional.of(request.getParameter("username"));
                userRole = Optional.ofNullable(UserRole.fromString(roleOption));
                if (userRole.isPresent() && username.isPresent() ) {
                    int changes = usuarioRepository.changeUserRole(userRole.orElseThrow().getRole(), username.orElseThrow());
                    logger.debug(String.format("User \"%s\" changed %s account status to \"%s\"", authentication.getName(), username.orElseThrow(), userRole.orElseThrow().getRole()));
                    return "redirect:/admin/panel/alterarpermissoes?success";
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "redirect:/admin/panel/alterarpermissoes?error";
        }
        return "redirect:/admin/panel/alterarpermissoes?error";
    }

    @PostMapping(value="/panel/alterarativado")
    public String postAlterarEstadoConta(HttpServletRequest request, @RequestBody String payload, Authentication authentication) {
        Optional<String> username;
        Optional<Boolean> estadoAtivo;
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                username = Optional.of(request.getParameter("username"));
                estadoAtivo = Optional.of(Boolean.parseBoolean(request.getParameter("activeOption")));
                if (username.isPresent() && estadoAtivo.isPresent()) {
                    int changes = usuarioRepository.changeUserActivity(estadoAtivo.orElseThrow(), username.orElseThrow());
                    logger.debug(String.format("User \"%s\" changed %s account status to \"%s\"", authentication.getName(), username.orElseThrow(), estadoAtivo.orElseThrow()));
                    return "redirect:/admin/panel/alterarativado?success";
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "redirect:/admin/panel/alterarativado?error";
        }
        return "redirect:/admin/panel/alterarativado?error";
    }

    @GetMapping(value="/findallusers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UsuarioSafeData> findAllUsers(@RequestParam(required = false, defaultValue = "0", name = "page") Integer page) {
        return usuarioRepository.findAllSafeDataPageable(PageRequest.of(page, PAGEABLE_SIZE));
    }

    @GetMapping(value="/findallmessages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ChatMessageSafeData> findAllMessages(@RequestParam(required = false, defaultValue = "0", name = "page") Integer page) {
        return messageRepository.findAllSafeDataPageable(PageRequest.of(page, PAGEABLE_SIZE));
    }

    @GetMapping(value="/findmessagebyid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ChatMessageSafeData findMessageById(@RequestParam(required = true, name = "id") Long id) {
        return messageRepository.findByIdSafe(id).orElseThrow();
    }

    @GetMapping(value="/findmessagebyauthor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ChatMessageSafeData> findMessageByAuthor(@RequestParam(required = true, name = "author") String author,
                                                         @RequestParam(required = false, defaultValue = "0", name = "page") Integer page) {
        return messageRepository.findByAuthorPageable(author, PageRequest.of(page, PAGEABLE_SIZE));
    }

    @GetMapping(value="/findmessagebycontent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ChatMessageSafeData> findMessageByAuthorAndMessageLike(@RequestParam(required = true, name = "content") String content,
                                                         @RequestParam(required = false, defaultValue = "0", name = "page") Integer page) {
        return messageRepository.findByMessageContains(content, PageRequest.of(page, PAGEABLE_SIZE));
    }

    @GetMapping(value = "/pageableSize", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String pageableSize() {
        try {
            Map<String, Long> payload = new HashMap<>();
            payload.put("pageableSize", (long) PAGEABLE_SIZE);
            return new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            logger.trace(e.getMessage(), e);
            return null;
        }
    }

    @GetMapping(value = "/userexists", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String checkIfUserExists(@RequestParam(required = true, name = "username") String username, Authentication authentication) throws JsonProcessingException {
        Map<String, String> payload = new HashMap<>();
        payload.put("username", username);
        try {
            Usuario usuario = usuarioRepository.findByUsuario(username).orElseThrow();
            payload.put("exists", Boolean.toString(true));
            payload.put("same", Boolean.toString(authentication.getName().equals(usuario.getUsuario())));
            return new ObjectMapper().writeValueAsString(payload);
        } catch (Exception e) {
            logger.trace(e.getMessage(), e);
            payload.put("exists", Boolean.toString(false));
            return new ObjectMapper().writeValueAsString(payload);
        }
    }
}

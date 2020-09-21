package com.ferroeduardo.springchat.Controller;

import com.ferroeduardo.springchat.Usuario.Usuario;
import com.ferroeduardo.springchat.Repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public LoginController(PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/login")
    public String getLogin(Model model, HttpServletRequest request) {
        return "login";
    }

    @GetMapping("/signup")
    public String getSignup(Model model, HttpServletRequest request) {
        model.addAttribute("user", new Usuario());
        return "signup";
    }

    @PostMapping("/signup")
    public String postSignup(Model model, HttpServletRequest request, @ModelAttribute(value = "user") Usuario usuario, Authentication authentication) {
        usuario.setData(new Date());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setAtivada(true);
        usuario.setIp(request.getRemoteAddr());
        usuario.setRoles("ROLE_USER");
        try {
            usuarioRepository.save(usuario);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "redirect:/signup?error";
        }
        logger.debug(String.format("User \"%s\" created a account", usuario.getUsuario()));
        return "redirect:/signup?success";
    }

    @GetMapping("/trocarsenha")
    public String getChangePassword() {
        return "changePassword";
    }

    @PostMapping("/trocarsenha")
    public String postChangePassword(HttpServletRequest request) {
        Optional<Usuario> usuario = Optional.empty();
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String username;
                if (principal instanceof UserDetails) {
                    username = ((UserDetails) principal).getUsername();
                } else {
                    username = principal.toString();
                }
                String senhaAntiga = request.getParameter("passwordOld");
                usuario = usuarioRepository.findByUsuario(username);
                usuario.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
                if (passwordEncoder.matches(senhaAntiga, usuario.get().getSenha())) {
                    usuarioRepository.changeUserPassword(passwordEncoder.encode(request.getParameter("passwordNew")), username);
                } else {
                    throw new BadCredentialsException("The inserted password is different from the stored at the database");
                }
            }
        } catch (BadCredentialsException e) {
            logger.error(e.getMessage());
            return "redirect:/trocarsenha/?wrong";
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "redirect:/trocarsenha/?error";
        }
        logger.debug(String.format("User \"%s\" changed the password", usuario.orElseThrow()));
        return "redirect:/trocarsenha/?success";
    }

}

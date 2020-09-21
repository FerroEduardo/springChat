package com.ferroeduardo.springchat.Config;

import com.ferroeduardo.springchat.Usuario.Usuario;
import com.ferroeduardo.springchat.Repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public MyUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByUsuario(username);

        usuario.orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));

        return usuario.map(MyUserDetails::new).get();
    }
}

package com.ferroeduardo.springchat.Repository;

import com.ferroeduardo.springchat.Usuario.Usuario;
import com.ferroeduardo.springchat.Usuario.UsuarioSafeData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<UsuarioSafeData> findFirst20ByOrderById();

    Optional<Usuario> findByUsuario(String usuario);

    @Transactional
    @Modifying
    @Query("UPDATE Usuario usuario SET usuario.roles=?1 WHERE usuario.usuario=?2")
    int changeUserRole(String role, String usuario);

    @Transactional
    @Modifying
    @Query("UPDATE Usuario usuario SET usuario.ativada=?1 WHERE usuario.usuario=?2")
    int changeUserActivity(boolean ativado, String usuario);

    @Transactional
    @Modifying
    @Query("UPDATE Usuario usuario SET usuario.senha=?1 WHERE usuario.usuario=?2")
    int changeUserPassword(String senha, String usuario);

    @Query(value="SELECT u.id as id, u.usuario as usuario, u.ativada as ativada, u.data as data, u.roles as roles FROM Usuario u")
    List<UsuarioSafeData> findAllSafeData();

    @Query(value="SELECT u.id as id, u.usuario as usuario, u.ativada as ativada, u.data as data, u.roles as roles FROM Usuario u ORDER BY u.id")
    List<UsuarioSafeData> findAllSafeDataPageable(Pageable pageable);
}

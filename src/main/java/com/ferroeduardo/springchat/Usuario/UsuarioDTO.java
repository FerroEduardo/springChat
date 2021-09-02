package com.ferroeduardo.springchat.Usuario;

import java.util.Date;

public interface UsuarioDTO {
    Long getId();

    String getUsuario();

    Date getData();

    boolean isAtivada();

    String getRoles();
}

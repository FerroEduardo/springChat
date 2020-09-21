package com.ferroeduardo.springchat.Chat;

import com.ferroeduardo.springchat.Usuario.UsuarioSocketInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatChannel {

    private String nome;
    private List<UsuarioSocketInfo> usuariosSocketInfos = new ArrayList<>();

    public ChatChannel(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public List<UsuarioSocketInfo> getUsuariosSocketInfos() {
        return usuariosSocketInfos;
    }

    public boolean adicionarUsuario(UsuarioSocketInfo usuarioSocketInfo) {
        for (UsuarioSocketInfo user : usuariosSocketInfos) {
            if (user.equals(usuarioSocketInfo)) {
                return false;
            }
        }
        usuariosSocketInfos.add(usuarioSocketInfo);
        return true;
    }

    public boolean removerUsuario(UsuarioSocketInfo usuarioSocketInfoRm) {
        try {
            for (UsuarioSocketInfo usuarioSocketInfo : usuariosSocketInfos) {
                if (usuarioSocketInfo.getSessionId().equals(usuarioSocketInfoRm.getSessionId())) {
                    usuariosSocketInfos.remove(usuarioSocketInfo);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public String obterNomeUsuario(String id) {
        for (UsuarioSocketInfo usuarioSocketInfo : usuariosSocketInfos) {
            if (usuarioSocketInfo.getSessionId().equals(id)) {
                return usuarioSocketInfo.getNome();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannel that = (ChatChannel) o;
        return nome.equals(that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        return "ChatChannel{" +
                "nome='" + nome + '\'' +
                ", usuarios=" + usuariosSocketInfos +
                '}';
    }
}

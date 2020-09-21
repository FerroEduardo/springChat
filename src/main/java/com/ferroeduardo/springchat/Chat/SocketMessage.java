package com.ferroeduardo.springchat.Chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ferroeduardo.springchat.Usuario.UsuarioSocketInfo;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocketMessage {
    private SocketMessageType tipo;
    private ChatMessage chatMessage;
    private List<UsuarioSocketInfo> usuarios;
    private String usuario;

    public SocketMessage(SocketMessageType tipo, ChatMessage chatMessage) {
        this.tipo = tipo;
        this.chatMessage = chatMessage;
    }

    public SocketMessage(SocketMessageType tipo, List<UsuarioSocketInfo> usuarioSocketInfos) {
        this.tipo = tipo;
        this.usuarios = usuarioSocketInfos;
    }

    public SocketMessage(SocketMessageType tipo, String usuario) {
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public SocketMessageType getTipo() {
        return tipo;
    }

    public void setTipo(SocketMessageType tipo) {
        this.tipo = tipo;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public List<UsuarioSocketInfo> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSocketInfo> usuarioSocketInfos) {
        this.usuarios = usuarioSocketInfos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "SocketMessage{" +
                "tipo=" + tipo +
                ", chatMessage=" + chatMessage +
                ", usuarios=" + usuarios +
                ", usuario='" + usuario + '\'' +
                '}';
    }
}

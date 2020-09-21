package com.ferroeduardo.springchat.Usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.Objects;

public class UsuarioSocketInfo {
    private String nome;
    private String canal;
    @JsonIgnore
    private String sessionId;
    @JsonIgnore
    private String ip;
    @JsonIgnore
    private Date dataCriado;

    public UsuarioSocketInfo(String nome, String canal, String sessionId, String ip, Date dataCriado) {
        this.nome = nome;
        this.canal = canal;
        this.sessionId = sessionId;
        this.ip = ip;
        this.dataCriado = dataCriado;
    }

    public UsuarioSocketInfo(String nome, String canal, String sessionId, String ip) {
        this.nome = nome;
        this.canal = canal;
        this.sessionId = sessionId;
        this.ip = ip;
    }

    public UsuarioSocketInfo(String nome, String canal, String sessionId) {
        this.nome = nome;
        this.canal = canal;
        this.sessionId = sessionId;
    }

    public UsuarioSocketInfo(String nome, String sessionId) {
        this.nome = nome;
        this.sessionId = sessionId;
    }

    public UsuarioSocketInfo(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioSocketInfo usuarioSocketInfo = (UsuarioSocketInfo) o;
        return nome.equals(usuarioSocketInfo.nome) &&
                canal.equals(usuarioSocketInfo.canal) &&
                sessionId.equals(usuarioSocketInfo.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, canal, sessionId);
    }

    @Override
    public String toString() {
        return "UsuarioSocketInfo{" +
                "nome='" + nome + '\'' +
                ", canal='" + canal + '\'' +
                ", id='" + sessionId + '\'' +
                '}';
    }
}

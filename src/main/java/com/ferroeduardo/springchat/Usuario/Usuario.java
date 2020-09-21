package com.ferroeduardo.springchat.Usuario;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "usuarios_springchat")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "usuario", nullable = false, length = 20)
    private String usuario;

    @Column(name = "senha", nullable = false, length = 40)
    private String senha;

    @Column(name = "data_criado", nullable = false)
    private Date data;

    @Column(name = "ip_criado", nullable = false, length = 21)
    private String ip;

    @Column(name = "roles", nullable = false, length = 100)
    private String roles;

    @Column(name = "conta_ativada", nullable = false)
    private boolean ativada;

    public Usuario(String usuario, Date data, String ip) {
        this.usuario = usuario;
        this.data = data;
        this.ip = ip;
    }

    public Usuario(String usuario, String senha,
                   Date data, String ip, String roles, boolean ativada) {
        this.usuario = usuario;
        this.senha = senha;
        this.data = data;
        this.ip = ip;
        this.roles = roles;
        this.ativada = ativada;
    }

    public Usuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String nome) {
        this.usuario = nome;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String role) {
        this.roles = role;
    }

    public boolean isAtivada() {
        return ativada;
    }

    public void setAtivada(boolean ativada) {
        this.ativada = ativada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario1 = (Usuario) o;
        return usuario.equals(usuario1.usuario) &&
                senha.equals(usuario1.senha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, senha);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", usuario='" + usuario + '\'' +
                ", senha='" + senha + '\'' +
                ", data=" + data +
                ", ip='" + ip + '\'' +
                ", roles='" + roles + '\'' +
                ", ativada=" + ativada +
                '}';
    }
}

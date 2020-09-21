package com.ferroeduardo.springchat.Chat;

public enum SocketMessageType {
    USERSONLINE("USERSONLINE"),
    CHATMESSAGE("CHATMESSAGE"),
    REMOVEUSER("REMOVEUSER");

    private String tipo;
    SocketMessageType(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}

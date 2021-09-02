package com.ferroeduardo.springchat.Chat;

import java.util.Date;

public interface ChatMessageDTO {

    long getId();

    String getAuthor();

    Date getDate();

    String getMessage();

    String getChannel();
}

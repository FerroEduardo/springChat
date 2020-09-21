package com.ferroeduardo.springchat.Controller;

import com.ferroeduardo.springchat.Chat.ChatChannel;
import com.ferroeduardo.springchat.Chat.ChatMessage;
import com.ferroeduardo.springchat.Chat.SocketMessage;
import com.ferroeduardo.springchat.Chat.SocketMessageType;
import com.ferroeduardo.springchat.Usuario.UsuarioSocketInfo;
import com.ferroeduardo.springchat.Repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Controller
public class ChatMessageController {

    private final ChatMessageRepository messageRepository;
    private final SimpMessagingTemplate template;

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);

    private static List<UsuarioSocketInfo> usersOnline = new ArrayList<>();
    private final Map<String, ChatChannel> canaisMap = new HashMap<>();

    public ChatMessageController(ChatMessageRepository messageRepository, SimpMessagingTemplate template) {
        this.messageRepository = messageRepository;
        this.template = template;
    }

    @MessageMapping("/message/{canalId}")
    @SendTo("/topic/message/{canalId}")
    public SocketMessage sentMessage(ChatMessage message, @DestinationVariable String canalId) {
        int maxLength = 300;
        int msgLength = message.getMessage().length();
        String msgFormated = msgLength > maxLength ? message.getMessage().substring(0, maxLength) : message.getMessage().substring(0, msgLength);
        logger.debug(String.format("User \"%s\" sent message to channel %s", message.getAuthor(), canalId));
        String userIp = null;
        for (UsuarioSocketInfo usuarioSocketInfo : usersOnline) {
            if (usuarioSocketInfo.getNome().equals(message.getAuthor())) {
                userIp = usuarioSocketInfo.getIp();
            }
        }
        ChatMessage msgObj = new ChatMessage(message.getAuthor(), msgFormated, message.getDate(), canalId, userIp);
        messageRepository.save(msgObj);
        if (message.getColor() != null) {
            msgObj.setColor(message.getColor());
        }
        SocketMessage socketMessage = new SocketMessage(SocketMessageType.CHATMESSAGE, msgObj);
        return socketMessage;
    }

    @MessageMapping("/message/{canalId}/userList")
    @SendTo("/topic/message/{canalId}")
    public void sendOnlineUsersList(@DestinationVariable String canalId) {
        ChatChannel canal = canaisMap.get(canalId);
        if (canal != null) {
            SocketMessage socketMessage = new SocketMessage(SocketMessageType.USERSONLINE, canal.getUsuariosSocketInfos());
            template.convertAndSend("/topic/message/" + canalId, socketMessage);
        } else {
            logger.error(String.format("Error trying to get user list from channel \"%s\"", canalId));
        }
    }

    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event) {
        Map<String, LinkedList<String>> nativeHeaders = (Map<String, LinkedList<String>>) event.getMessage().getHeaders().get("nativeHeaders");
        ConcurrentHashMap simpSessionAttributes = (ConcurrentHashMap) event.getMessage().getHeaders().get("simpSessionAttributes");
        String usuarioNome = nativeHeaders.get("usuario").get(0);
        String canalNome = nativeHeaders.get("canal").get(0);
        String id = event.getMessage().getHeaders().get("simpSessionId").toString();
        String ip = simpSessionAttributes.get("ip").toString().substring(1);
        UsuarioSocketInfo usuarioSocketInfo = new UsuarioSocketInfo(usuarioNome, canalNome, id, ip);
        usersOnline.add(usuarioSocketInfo);
        logger.debug("Novo websocket conectado, id=" + id);
    }

    @EventListener(SessionSubscribeEvent.class)
    public void handleWebsocketSubscribeListener(SessionSubscribeEvent event) {
        Map<String, LinkedList<String>> nativeHeaders = (Map<String, LinkedList<String>>) event.getMessage().getHeaders().get("nativeHeaders");
        String id = event.getMessage().getHeaders().get("simpSessionId").toString();
        String canalNome = nativeHeaders.get("id").get(0);
        UsuarioSocketInfo usuarioSocketInfo = null;
        for (UsuarioSocketInfo user : usersOnline) {
            if (user.getSessionId().equals(id)) {
                usuarioSocketInfo = user;
            }
        }
        if (usuarioSocketInfo == null) {
            logger.error(String.format("Error trying to subscribe user \"%s\" to the channel \"%s\"", id, canalNome));
            return;
        }
        ChatChannel canal = new ChatChannel(canalNome);
        ChatChannel retornoPut = canaisMap.putIfAbsent(canalNome, canal);
        if (canal.equals(retornoPut)) {
            // There wasn't, so add it
            if (retornoPut.adicionarUsuario(usuarioSocketInfo)) {
                logger.debug(String.format("User \"%s\" was inserted to the channel %s", id, canalNome));
                sendOnlineUsersList(canalNome);
            } else {
                logger.error(String.format("Error trying to insert user \"%s\" to the channel \"%s\"", id, canalNome));
            }
        } else {
            ChatChannel canalJaExistente = canaisMap.get(canalNome);
            if (canalJaExistente.adicionarUsuario(usuarioSocketInfo)) {
                logger.debug(String.format("User \"%s\" was inserted to the channel %s", id, canalNome));
                sendOnlineUsersList(canalNome);
            } else {
                logger.error(String.format("Error trying to insert user \"%s\" to the channel \"%s\"", id, canalNome));
            }
        }

    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketUnsubscribeListener(SessionDisconnectEvent event) {
        String id = event.getMessage().getHeaders().get("simpSessionId").toString();
        for (UsuarioSocketInfo usuarioSocketInfo : usersOnline) {
            if (usuarioSocketInfo.getSessionId().equals(id)) {
                usersOnline.remove(usuarioSocketInfo);
                break;
            }
        }
        logger.debug(String.format("Websocket \"%s\" was disconnected", id));
    }

    @EventListener(SessionUnsubscribeEvent.class)
    public void handleWebsocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        Map<String, LinkedList<String>> nativeHeaders = (Map<String, LinkedList<String>>) event.getMessage().getHeaders().get("nativeHeaders");
        String canal = nativeHeaders.get("id").get(0);
        String id = event.getMessage().getHeaders().get("simpSessionId").toString();
        ChatChannel chatChannel = canaisMap.get(canal);
        String username = chatChannel.obterNomeUsuario(id);
        if (chatChannel.removerUsuario(new UsuarioSocketInfo(id))) {
            logger.debug(String.format("User \"%s\" unsubscribed and was removed from the channel \"%s\"", id, canal));
        } else {
            logger.error(String.format("Error trying to unsubcribe and remove user \"%s\" from the channel \"%s\"", id, canal));
        }
        // Socket to remove the user from the online users list
        SocketMessage socketMessage = new SocketMessage(SocketMessageType.REMOVEUSER, username);
        template.convertAndSend("/topic/message/" + canal, socketMessage);
    }

    @GetMapping("/chat")
    public String pageChat(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("channelId", "Chat");
        return "chat";
    }
}
package com.ferroeduardo.springchat.Repository;

import com.ferroeduardo.springchat.Chat.ChatMessage;
import com.ferroeduardo.springchat.Chat.ChatMessageSafeData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAll();

    List<ChatMessageSafeData> findByMessageLikeAndAuthor(String author, String message);

    List<ChatMessageSafeData> findByMessageContains(String message, Pageable pageable);

    List<ChatMessageSafeData> findByAuthor(String author);

    @Query(value="SELECT m.id as id, m.author as author, m.date as date, m.message as message, m.channel as channel FROM ChatMessage m WHERE m.author = ?1 ORDER BY m.id")
    List<ChatMessageSafeData> findByAuthorPageable(String author, Pageable pageable);

    @Query(value="SELECT m.id as id, m.author as author, m.date as date, m.message as message, m.channel as channel FROM ChatMessage m WHERE m.id = ?1")
    Optional<ChatMessageSafeData> findByIdSafe(Long id);

    @Query(value="SELECT m.id as id, m.author as author, m.date as date, m.message as message, m.channel as channel FROM ChatMessage m ORDER BY m.id")
    List<ChatMessageSafeData> findAllSafeData();

    @Query(value="SELECT m.id as id, m.author as author, m.date as date, m.message as message, m.channel as channel FROM ChatMessage m ORDER BY m.id")
    List<ChatMessageSafeData> findAllSafeDataPageable(Pageable pageable);
}

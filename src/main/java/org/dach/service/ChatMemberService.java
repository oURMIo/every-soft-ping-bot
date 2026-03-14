package org.dach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import org.dach.domain.ChatMember;
import org.dach.repository.ChatMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pengrad.telegrambot.model.User;

@ApplicationScoped
public class ChatMemberService {

    private static final Logger logger = LoggerFactory.getLogger(ChatMemberService.class);

    private final ChatMemberRepository repository;

    public ChatMemberService(ChatMemberRepository repository) {
        this.repository = repository;
    }

    private static final int MAX_FIELD_LENGTH = 150;

    @Transactional
    public void registerMember(long chatId, User user) {
        if (user == null || user.isBot()) {
            return;
        }

        String firstName = truncate(user.firstName(), MAX_FIELD_LENGTH);
        String lastName = truncate(user.lastName(), MAX_FIELD_LENGTH);
        String username = truncate(user.username(), MAX_FIELD_LENGTH);

        ChatMember.ChatMemberId id = new ChatMember.ChatMemberId(chatId, user.id());
        ChatMember existing = repository.findById(id);

        if (existing != null) {
            existing.setFirstName(firstName);
            existing.setLastName(lastName);
            existing.setUsername(username);
            existing.setUpdatedAt(LocalDateTime.now());
        } else {
            ChatMember member = new ChatMember(chatId, user.id(), firstName, lastName, username);
            repository.persist(member);
            logger.info("Registered new member username={} in chat '{}'", user.username(), chatId);
        }
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    public List<ChatMember> getMembers(long chatId) {
        return repository.findByChatId(chatId);
    }
}
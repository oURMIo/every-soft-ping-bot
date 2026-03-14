package org.dach.repository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.dach.domain.ChatMember;

@ApplicationScoped
public class ChatMemberRepository implements PanacheRepositoryBase<ChatMember, ChatMember.ChatMemberId> {

    public List<ChatMember> findByChatId(long chatId) {
        return list("id.chatId", chatId);
    }
}
package org.dach.service;

import java.util.List;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.dach.domain.ChatMember;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ChatMemberServiceTest {

    @Inject
    ChatMemberService chatMemberService;

    @Test
    void getMembers_emptyChat_returnsEmptyList() {
        List<ChatMember> members = chatMemberService.getMembers(-999L);
        assertTrue(members.isEmpty());
    }

    @Test
    void registerMember_nullUser_doesNotThrow() {
        chatMemberService.registerMember(1L, null);
    }
}
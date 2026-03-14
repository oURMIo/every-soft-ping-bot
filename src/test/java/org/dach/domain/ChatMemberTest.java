package org.dach.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ChatMemberTest {

    @Test
    void toMentionHtml_withUsername() {
        ChatMember member = new ChatMember(1L, 100L, "John", "Doe", "johndoe");
        assertEquals("@johndoe", member.toMentionHtml());
    }

    @Test
    void toMentionHtml_withoutUsername_firstNameOnly() {
        ChatMember member = new ChatMember(1L, 100L, "John", null, null);
        assertEquals("<a href=\"tg://user?id=100\">John</a>", member.toMentionHtml());
    }

    @Test
    void toMentionHtml_withoutUsername_fullName() {
        ChatMember member = new ChatMember(1L, 100L, "John", "Doe", null);
        assertEquals("<a href=\"tg://user?id=100\">John Doe</a>", member.toMentionHtml());
    }

    @Test
    void toMentionHtml_escapesHtml() {
        ChatMember member = new ChatMember(1L, 100L, "<b>John</b>", null, null);
        assertEquals("<a href=\"tg://user?id=100\">&lt;b&gt;John&lt;/b&gt;</a>", member.toMentionHtml());
    }

    @Test
    void chatMemberId_equality() {
        ChatMember.ChatMemberId id1 = new ChatMember.ChatMemberId(1L, 100L);
        ChatMember.ChatMemberId id2 = new ChatMember.ChatMemberId(1L, 100L);
        ChatMember.ChatMemberId id3 = new ChatMember.ChatMemberId(1L, 200L);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1, id3);
    }
}
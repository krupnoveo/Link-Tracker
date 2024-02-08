package edu.java.bot.printerToChat;

import com.pengrad.telegrambot.request.SendMessage;

public interface ChatResponser {
    void sendMessage(SendMessage message);
}

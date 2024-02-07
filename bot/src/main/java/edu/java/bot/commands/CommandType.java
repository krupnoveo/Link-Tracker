package edu.java.bot.commands;

public enum CommandType {
    START("/start"),
    HELP("/help"),
    TRACK("/track"),
    UNTRACK("/untrack"),
    LIST("/list");

    private String commandName;

    CommandType(String commandName) {
        this.commandName = commandName;
    }
}

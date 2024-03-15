package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clientService.DefaultBotService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandHandlerTest {
    private Update update;
    private CommandHandler handler;
    private Message message;

    @BeforeEach
    public void init() {
        DefaultBotService botService = Mockito.mock(DefaultBotService.class);
        Properties properties = Mockito.mock(Properties.class);
        update = Mockito.mock(Update.class);
        handler = new StartCommand(properties, botService);
        message = Mockito.mock(Message.class);
    }
    @Test
    @SneakyThrows
    @DisplayName("Тест CommandHandler.isSupportsUpdate(). Должен вернуть истину при подходящих данных")
    public void isSupportsUpdate_whenUpdateHasNecessaryData_shouldReturnTrue() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(update.message().text()).thenReturn("some text");

        assertThat(handler.isSupportsUpdate(update)).isTrue();
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест CommandHandler.isSupportsUpdate(). Должен вернуть ложь при неподходящих данных")
    public void isSupportsUpdate_whenUpdateNotHaveNecessaryData_shouldReturnFalse() {
        Mockito.when(update.message()).thenReturn(message);

        assertThat(handler.isSupportsUpdate(update)).isFalse();
    }
}

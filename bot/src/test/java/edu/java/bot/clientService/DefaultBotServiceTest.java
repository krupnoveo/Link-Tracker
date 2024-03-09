package edu.java.bot.clientService;

import edu.java.bot.api.httpClient.ScrapperClient;
import edu.java.bot.api.dto.request.AddLinkRequest;
import edu.java.bot.api.dto.request.RemoveLinkRequest;
import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.models.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class DefaultBotServiceTest {
    @Test
    @DisplayName("Тест DefaultBotService.registerUser()")
    public void registerUser_shouldReturnCorrectGenericResponse() {
        ScrapperClient client = Mockito.mock(ScrapperClient.class);
        DefaultBotService botService = new DefaultBotService(client);
        GenericResponse<Void> response = new GenericResponse<>(null, null);
        User user = new User(1L);
        Mockito.when(client.registerChat(user)).thenReturn(response);

        GenericResponse<Void> expected = new GenericResponse<>(null, null);
        GenericResponse<Void> actual = botService.registerUser(user);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест DefaultBotService.addLinkToDatabase()")
    public void addLinkToDatabase_shouldReturnCorrectGenericResponse() {
        ScrapperClient client = Mockito.mock(ScrapperClient.class);
        DefaultBotService botService = new DefaultBotService(client);
        GenericResponse<AddLinkToDatabaseResponse> response = new GenericResponse<>(new AddLinkToDatabaseResponse(2L, new URI("")), null);
        long chatId = 1L;
        Mockito.when(client.addLinkToTracking(chatId, new AddLinkRequest(new URI("")))).thenReturn(response);

        GenericResponse<AddLinkToDatabaseResponse> expected = new GenericResponse<>(new AddLinkToDatabaseResponse(2L, new URI("")), null);
        GenericResponse<AddLinkToDatabaseResponse> actual = botService.addLinkToDatabase("", chatId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест DefaultBotService.removeLinkFromDatabase()")
    public void removeLinkFromDatabase_shouldReturnCorrectGenericResponse() {
        ScrapperClient client = Mockito.mock(ScrapperClient.class);
        DefaultBotService botService = new DefaultBotService(client);
        GenericResponse<RemoveLinkFromDatabaseResponse> response = new GenericResponse<>(new RemoveLinkFromDatabaseResponse(2L, new URI("")), null);
        long chatId = 1L;
        Mockito.when(client.removeLinkFromTracking(chatId, new RemoveLinkRequest(2L))).thenReturn(response);

        GenericResponse<RemoveLinkFromDatabaseResponse> expected = new GenericResponse<>(new RemoveLinkFromDatabaseResponse(2L, new URI("")), null);
        GenericResponse<RemoveLinkFromDatabaseResponse> actual = botService.removeLinkFromDatabase(2L, chatId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Тест DefaultBotService.listLinksFromDatabase()")
    public void listLinksFromDatabase_shouldReturnCorrectGenericResponse() {
        ScrapperClient client = Mockito.mock(ScrapperClient.class);
        DefaultBotService botService = new DefaultBotService(client);
        GenericResponse<ListLinksResponse> response = new GenericResponse<>(new ListLinksResponse(List.of()), null);
        long chatId = 1L;
        Mockito.when(client.listLinks(chatId)).thenReturn(response);

        GenericResponse<ListLinksResponse> expected = new GenericResponse<>(new ListLinksResponse(List.of()), null);
        GenericResponse<ListLinksResponse> actual = botService.listLinksFromDatabase(chatId);

        assertThat(actual).isEqualTo(expected);
    }

}

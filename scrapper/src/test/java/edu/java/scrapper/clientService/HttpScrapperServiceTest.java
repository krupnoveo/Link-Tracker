package edu.java.scrapper.clientService;

import edu.java.api.client.http.BotClient;
import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;
import edu.java.clientService.http.HttpScrapperService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpScrapperServiceTest {

    @Test
    @SneakyThrows
    @DisplayName("Тест HttpScrapperService.updateLinks()")
    public void updateLinks_shouldReturnCorrectGenericResponse() {
        BotClient botClient = Mockito.mock(BotClient.class);
        LinkUpdate update = new LinkUpdate(
            0L,
            new URI(""),
            "",
            List.of()
        );
        GenericResponse<Void> response = new GenericResponse<>(null, null);
        Mockito.when(botClient.sendNotification(List.of(update))).thenReturn(response);

        HttpScrapperService defaultScrapperService = new HttpScrapperService(botClient);
        GenericResponse<Void> actual = defaultScrapperService.notifyChats(List.of(update));
        GenericResponse<Void> expected = new GenericResponse<>(null , null);
        assertThat(actual).isEqualTo(expected);
    }

}

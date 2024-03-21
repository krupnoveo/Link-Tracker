package edu.java.scrapper.clientService;

import edu.java.api.httpClient.BotClient;
import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;
import edu.java.httpClientService.DefaultScrapperService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class DefaultScrapperServiceTest {

    @Test
    @SneakyThrows
    @DisplayName("Тест DefaultScrapperService.updateLinks()")
    public void updateLinks_shouldReturnCorrectGenericResponse() {
        BotClient botClient = Mockito.mock(BotClient.class);
        LinkUpdate update = new LinkUpdate(
            0L,
            new URI(""),
            "",
            List.of()
        );
        GenericResponse<Void> response = new GenericResponse<>(null, null);
        Mockito.when(botClient.notifyChats(List.of(update))).thenReturn(response);

        DefaultScrapperService defaultScrapperService = new DefaultScrapperService(botClient);
        GenericResponse<Void> actual = defaultScrapperService.notifyChats(List.of(update));
        GenericResponse<Void> expected = new GenericResponse<>(null , null);
        assertThat(actual).isEqualTo(expected);
    }

}

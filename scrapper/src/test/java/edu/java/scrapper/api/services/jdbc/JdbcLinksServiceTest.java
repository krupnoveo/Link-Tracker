package edu.java.scrapper.api.services.jdbc;

import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import edu.java.api.services.jdbc.JdbcLinksService;
import edu.java.clients.holder.ClientsHolder;
import edu.java.domain.ChatsToLinksRepository;
import edu.java.domain.LinksRepository;
import edu.java.models.LinkData;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class JdbcLinksServiceTest {
    @Mock
    private LinksRepository linksRepository;
    @Mock
    private ChatsToLinksRepository chatsToLinksRepository;
    @Mock
    private ClientsHolder clientsHolder;
    @InjectMocks
    private JdbcLinksService service;

    @Test
    @SneakyThrows
    public void getTrackedLinks_shouldReturnCorrectAnswer() {
        List<LinkResponse> links = List.of(
            new LinkResponse(1L, new URI("1")),
            new LinkResponse(2L, new URI("2"))
        );
        Mockito.when(chatsToLinksRepository.findAllByChatId(1L)).thenReturn(
            links
        );
        ListLinksResponse actual = service.getTrackedLinks(1L);
        ListLinksResponse expected = new ListLinksResponse(links);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    public void addLinkToTracking_shouldReturnCorrectAnswer() {
        Mockito.mockStatic(OffsetDateTime.class, Mockito.CALLS_REAL_METHODS);
        Mockito.when(OffsetDateTime.now()).thenReturn(OffsetDateTime.MIN);
        long chatId = 1L;
        AddLinkRequest addLinkRequest = new AddLinkRequest(new URI("https://ya.ru"));
        URI uri = addLinkRequest.link();
        Mockito.when(clientsHolder.checkURl(uri, null)).thenReturn(
            List.of(new LinkData(uri.toURL(), OffsetDateTime.MIN, "", Map.of()))
        );
        Mockito.when(linksRepository.add(addLinkRequest.link(), OffsetDateTime.MIN, OffsetDateTime.MIN)).thenReturn(1L);

        LinkResponse actual = service.addLinkToTracking(chatId, addLinkRequest);

        Mockito.verify(chatsToLinksRepository).add(chatId, 1L, uri);

        LinkResponse expected = new LinkResponse(1L, uri);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    public void removeLinkFromTracking_whenLinkIsNotTrackedByAnybodyElse_shouldReturnCorrectAnswer() {
        long chatId = 1L;
        long urlId = 1L;
        URI uri = new URI("https://ya.ru");
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(urlId);
        Mockito.when(linksRepository.getUriById(urlId)).thenReturn(uri);
        Mockito.when(chatsToLinksRepository.isLinkTrackedByAnybody(urlId)).thenReturn(true);

        LinkResponse actual = service.removeLinkFromTracking(chatId, removeLinkRequest);
        LinkResponse expected = new LinkResponse(urlId, uri);

        Mockito.verify(chatsToLinksRepository).remove(chatId, urlId, uri);
        Mockito.verify(linksRepository, Mockito.times(0)).remove(urlId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    public void removeLinkFromTracking_whenLinkIsTrackedBySomeoneElse_shouldReturnCorrectAnswer() {
        long chatId = 1L;
        long urlId = 1L;
        URI uri = new URI("https://ya.ru");
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(urlId);
        Mockito.when(linksRepository.getUriById(urlId)).thenReturn(uri);
        Mockito.when(chatsToLinksRepository.isLinkTrackedByAnybody(urlId)).thenReturn(false);

        LinkResponse actual = service.removeLinkFromTracking(chatId, removeLinkRequest);
        LinkResponse expected = new LinkResponse(urlId, uri);

        Mockito.verify(chatsToLinksRepository).remove(chatId, urlId, uri);
        Mockito.verify(linksRepository).remove(urlId);

        assertThat(actual).isEqualTo(expected);
    }
}

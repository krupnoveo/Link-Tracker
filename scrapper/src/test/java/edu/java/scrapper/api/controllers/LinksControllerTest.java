package edu.java.scrapper.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.api.controllers.LinksController;
import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import edu.java.api.services.LinksService;
import java.net.URI;
import java.util.List;
import edu.java.api.services.jooq.JooqLinksService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(LinksController.class)
public class LinksControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("jooqLinksService")
    private LinksService service;

    @Autowired
    private ObjectMapper objectMapper;
    private static final String PATH_FOR_LINKS_CONTROLLER = "/links";
    private static final String REQUEST_HEADER_NAME = "Tg-Chat-Id";

    @Test
    @SneakyThrows
    @DisplayName("Тест LinksController.getTrackedLinks(). Должен вернуть статус 200")
    public void getTrackedLinks_whenRequestIsCorrect_shouldReturnCorrectResponse() {
        Mockito.when(service.getTrackedLinks(1L)).thenReturn(new ListLinksResponse(List.of(
            new LinkResponse(0L, new URI(""))
        )));
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .get(PATH_FOR_LINKS_CONTROLLER)
                .header(REQUEST_HEADER_NAME, 1L)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        Mockito.verify(service).getTrackedLinks(1L);

        ListLinksResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), ListLinksResponse.class);
        ListLinksResponse expected = new ListLinksResponse(List.of(
            new LinkResponse(0L, new URI(""))
        ));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест LinksController.getTrackedLinks(). Должен вернуть статус 400")
    public void getTrackedLinks_whenHeaderIsMissing_shouldReturnBadRequest() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get(PATH_FOR_LINKS_CONTROLLER)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест LinksController.addLinkToTracking(). Должен вернуть статус 200")
    public void addLinkToTracking_whenRequestIsCorrect_shouldReturnCorrectResponse() {
        URI uri = new URI("");
        AddLinkRequest request = new AddLinkRequest(uri);
        Mockito.when(service.addLinkToTracking(1L, request)).thenReturn(
            new LinkResponse(0L, uri)
        );
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .post(PATH_FOR_LINKS_CONTROLLER)
                .header(REQUEST_HEADER_NAME, 1L)
                .content("""
                    {
                      "link": ""
                    }
                    """)
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        Mockito.verify(service).addLinkToTracking(1L, request);

        LinkResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), LinkResponse.class);
        LinkResponse expected = new LinkResponse(0L, uri);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест LinksController.addLinkToTracking(). Должен вернуть статус 400")
    public void addLinkToTracking_whenHeaderOrContentAreMissing_shouldReturnBadRequest() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(PATH_FOR_LINKS_CONTROLLER)
                .header(REQUEST_HEADER_NAME, 1L)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(PATH_FOR_LINKS_CONTROLLER)
                .content("""
                    {
                      "link": ""
                    }
                    """)
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест LinksController.removeLinkFromTracking(). Должен вернуть статус 200")
    public void removeLinkFromTracking_whenRequestIsCorrect_shouldReturnCorrectResponse() {
        URI uri = new URI("");
        RemoveLinkRequest request = new RemoveLinkRequest(0L);
        Mockito.when(service.removeLinkFromTracking(1L, request)).thenReturn(
            new LinkResponse(0L, uri)
        );
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .delete(PATH_FOR_LINKS_CONTROLLER)
                .header(REQUEST_HEADER_NAME, 1L)
                .content("""
                    {
                      "link": ""
                    }
                    """)
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        Mockito.verify(service).removeLinkFromTracking(1L, request);

        LinkResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), LinkResponse.class);
        LinkResponse expected = new LinkResponse(0L, uri);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест LinksController.removeLinkFromTracking(). Должен вернуть статус 400")
    public void removeLinkFromTracking_whenHeaderOrContentAreMissing_shouldReturnBadRequest() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete(PATH_FOR_LINKS_CONTROLLER)
                .header(REQUEST_HEADER_NAME, 1L)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(
            MockMvcRequestBuilders
                .delete(PATH_FOR_LINKS_CONTROLLER)
                .content("""
                    {
                      "link": ""
                    }
                    """)
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

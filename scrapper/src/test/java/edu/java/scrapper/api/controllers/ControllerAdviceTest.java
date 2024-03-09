package edu.java.scrapper.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.api.controllers.ChatController;
import edu.java.api.controllers.LinksController;
import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.ApiErrorResponse;
import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.api.exceptions.IncorrectRequestParametersException;
import edu.java.api.exceptions.InvalidUrlFormatException;
import edu.java.api.exceptions.LinkAlreadyTrackedException;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.api.exceptions.UnsupportedUrlHostException;
import edu.java.api.services.jdbc.JdbcChatService;
import edu.java.api.services.jdbc.JdbcLinksService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(controllers = {ChatController.class, LinksController.class})
public class ControllerAdviceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JdbcChatService jdbcChatService;

    @MockBean
    private JdbcLinksService jdbcLinksService;
    @Test
    @SneakyThrows
    @DisplayName("Тест ControllerAdvice.incorrectRequest. Должен поймать выброшенное исключение при попытке удалить чат с отрицательным айди")
    public void deleteChat_whenRequestedIdIsIncorrect_shouldReturnCorrectErrorResponse() {
        Mockito.doThrow(IncorrectRequestParametersException.class).when(jdbcChatService).deleteChat(-1L);
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/tg-chat/-1")
        ).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        ApiErrorResponse
            errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo("400");
        assertThat(errorResponse.exceptionName()).isEqualTo(IncorrectRequestParametersException.class.getName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ControllerAdvice.chatDoesNotExist. Должен поймать выброшенное исключение при попытке удалить несуществующий чат")
    public void deleteChat_whenChatNotFound_shouldReturnCorrectErrorResponse() {
        Mockito.doThrow(ChatDoesNotExistException.class).when(jdbcChatService).deleteChat(1L);
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/tg-chat/1")
        ).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        ApiErrorResponse
            errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo("404");
        assertThat(errorResponse.exceptionName()).isEqualTo(ChatDoesNotExistException.class.getName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ControllerAdvice.chatAlreadyRegistered. Должен поймать выброшенное исключение при попытке зарегистрировать уже существующий чат")
    public void registerChat_whenChatAlreadyRegistered_shouldReturnCorrectErrorResponse() {
        Mockito.doThrow(ChatAlreadyRegisteredException.class).when(jdbcChatService).registerChat(1L);
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/tg-chat/1")
        ).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        ApiErrorResponse
            errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo("409");
        assertThat(errorResponse.exceptionName()).isEqualTo(ChatAlreadyRegisteredException.class.getName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ControllerAdvice.linkNotFound. Должен поймать выброшенное исключение при попытке удалить несуществующую ссылку")
    public void removeLinkFromTracking_whenLinkNotFound_shouldReturnCorrectErrorResponse() {
        RemoveLinkRequest request = new RemoveLinkRequest(0L);
        Mockito.doThrow(LinkNotFoundException.class).when(jdbcLinksService).removeLinkFromTracking(1L, request);
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/links")
                .header("Tg-Chat-Id", 1L)
                .content("""
                    {
                        "linkId": 0
                    }
                    """)
                .contentType("application/json")
        ).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        ApiErrorResponse
            errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo("404");
        assertThat(errorResponse.exceptionName()).isEqualTo(LinkNotFoundException.class.getName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ControllerAdvice.linkAlreadyTracked. Должен поймать выброшенное исключение при попытке добавить уже существующую ссылку")
    public void addLinkToTracking_whenLinkAlreadyTracked_shouldReturnCorrectErrorResponse() {
        AddLinkRequest request = new AddLinkRequest(new URI(""));
        Mockito.doThrow(LinkAlreadyTrackedException.class).when(jdbcLinksService).addLinkToTracking(1L, request);
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/links")
                .header("Tg-Chat-Id", 1L)
                .content("""
                    {
                        "link": ""
                    }
                    """)
                .contentType("application/json")
        ).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        ApiErrorResponse
            errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo("409");
        assertThat(errorResponse.exceptionName()).isEqualTo(LinkAlreadyTrackedException.class.getName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ControllerAdvice.invalidUrlFormat. Должен поймать выброшенное исключение при попытке добавить ссылку неправильного формата")
    public void addLinkToTracking_whenLinkHasInvalidFormat_shouldReturnCorrectErrorResponse() {
        AddLinkRequest request = new AddLinkRequest(new URI(""));
        Mockito.doThrow(InvalidUrlFormatException.class).when(jdbcLinksService).addLinkToTracking(1L, request);
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/links")
                .header("Tg-Chat-Id", 1L)
                .content("""
                    {
                        "link": ""
                    }
                    """)
                .contentType("application/json")
        ).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        ApiErrorResponse
            errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo("406");
        assertThat(errorResponse.exceptionName()).isEqualTo(InvalidUrlFormatException.class.getName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ControllerAdvice.invalidUrlFormat. Должен поймать выброшенное исключение при попытке добавить ссылку неправильного формата")
    public void addLinkToTracking_whenLinkHostIsNotSupported_shouldReturnCorrectErrorResponse() {
        AddLinkRequest request = new AddLinkRequest(new URI(""));
        Mockito.doThrow(UnsupportedUrlHostException.class).when(jdbcLinksService).addLinkToTracking(1L, request);
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/links")
                .header("Tg-Chat-Id", 1L)
                .content("""
                    {
                        "link": ""
                    }
                    """)
                .contentType("application/json")
        ).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        ApiErrorResponse
            errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo("406");
        assertThat(errorResponse.exceptionName()).isEqualTo(UnsupportedUrlHostException.class.getName());
    }

}

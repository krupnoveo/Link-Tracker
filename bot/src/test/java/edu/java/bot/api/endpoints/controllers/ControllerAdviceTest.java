package edu.java.bot.api.endpoints.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.api.dto.request.LinkUpdate;
import edu.java.bot.api.dto.response.ApiErrorResponse;
import edu.java.bot.api.endpoints.controllers.UpdateLinkController;
import edu.java.bot.api.exceptions.IncorrectRequestParametersException;
import edu.java.bot.api.service.LinkUpdatesService;
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
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(UpdateLinkController.class)
public class ControllerAdviceTest {
    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LinkUpdatesService service;
    @Test
    @SneakyThrows
    @DisplayName("Тест ControllerAdvice.incorrectRequest. Должен поймать выброшенное исключение при попытке уведомить пользователей")
    public void updateLinks_whenLinkUpdateFailed_shouldThrowError() {
        LinkUpdate linkUpdate = new LinkUpdate(1, new URI("https://ya.ru"), "some text", List.of(0L));
        Mockito.doThrow(IncorrectRequestParametersException.class).when(service).notifyUsers(List.of(linkUpdate));
        var result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/updates")
                .content("""
                    [
                        {
                            "urlId": 1,
                            "url": "https://ya.ru",
                            "description": "some text",
                            "chatIds": [
                                0
                            ]
                        }
                    ]
                    """
                )
                .contentType("application/json")
        ).andReturn();

        ApiErrorResponse
            errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo("400");
        assertThat(errorResponse.exceptionName()).isEqualTo(IncorrectRequestParametersException.class.getName());
    }
}

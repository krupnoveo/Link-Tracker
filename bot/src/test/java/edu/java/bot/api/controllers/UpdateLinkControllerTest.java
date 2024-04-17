package edu.java.bot.api.controllers;

import edu.java.bot.api.dto.request.LinkUpdate;
import edu.java.bot.api.service.LinkUpdatesService;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UpdateLinkController.class)
public class UpdateLinkControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkUpdatesService service;

    @Test
    @SneakyThrows
    @DisplayName("Тест UpdateLinkController.updateLinks(). Должен вернуть статус 200")
    public void updateLinks_whenDataIsCorrect_shouldNotThrowError() {
        mockMvc.perform(
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
                    """)
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service).notifyUsers(List.of(
            new LinkUpdate(1, new URI("https://ya.ru"), "some text", List.of(0L))
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест UpdateLinkController.updateLinks(). Должен вернуть статус 400 при отсутствии тела запроса")
    public void updateLinks_whenDataIsNotCorrect_shouldThrowBadRequest() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/updates")
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

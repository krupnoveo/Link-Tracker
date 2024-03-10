package edu.java.scrapper.api.controllers;

import edu.java.api.controllers.ChatController;
import edu.java.api.services.ChatService;
import edu.java.api.services.jdbc.JdbcChatService;
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

@WebMvcTest(ChatController.class)
public class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService service;
    private static final String PATH_FOR_CHAT_CONTROLLER = "/tg-chat";

    @Test
    @SneakyThrows
    @DisplayName("Тест ChatController.registerChat(). Должен вернуть статус 200")
    public void registerChat_whenRequestIsCorrect_shouldReturnOk() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(PATH_FOR_CHAT_CONTROLLER + "/1")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service).registerChat(1L);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ChatController.registerChat(). Должен вернуть статус 400 при неправильном типе параметра в пути запроса")
    public void registerChat_whenRequestIsNotCorrect_shouldReturnBadRequest() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(PATH_FOR_CHAT_CONTROLLER + "/id")
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ChatController.deleteChat(). Должен вернуть статус 200")
    public void deleteChat_whenRequestIsCorrect_shouldReturnOk() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete(PATH_FOR_CHAT_CONTROLLER + "/1")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service).deleteChat(1L);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ChatController.deleteChat(). Должен вернуть статус 400 при неправильном типе параметра в пути запроса")
    public void deleteChat_whenRequestIsNotCorrect_shouldReturnBadRequest() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(PATH_FOR_CHAT_CONTROLLER + "/id")
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

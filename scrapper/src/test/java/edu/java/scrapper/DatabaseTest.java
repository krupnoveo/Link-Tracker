package edu.java.scrapper;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseTest extends IntegrationEnvironment {
    @Test
    @SneakyThrows
    public void chatTableShouldBeAdded() {
        Connection connection = POSTGRES.createConnection("");
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM chat");
        ResultSet resultSet = statement.executeQuery();
        assertThat(resultSet.getMetaData().getColumnName(1)).isEqualTo("chat_id");
    }

    @Test
    @SneakyThrows
    public void linkTableShouldBeAdded() {
        Connection connection = POSTGRES.createConnection("");
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM link");
        ResultSet resultSet = statement.executeQuery();
        assertThat(resultSet.getMetaData().getColumnName(1)).isEqualTo("id");
        assertThat(resultSet.getMetaData().getColumnName(2)).isEqualTo("url");
        assertThat(resultSet.getMetaData().getColumnName(3)).isEqualTo("updated_at");
        assertThat(resultSet.getMetaData().getColumnName(4)).isEqualTo("checked_at");
    }

    @Test
    @SneakyThrows
    public void chatToLinkTableShouldBeAdded() {
        Connection connection = POSTGRES.createConnection("");
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM chat_to_link");
        ResultSet resultSet = statement.executeQuery();
        assertThat(resultSet.getMetaData().getColumnName(1)).isEqualTo("chat_id");
        assertThat(resultSet.getMetaData().getColumnName(2)).isEqualTo("link_id");
    }
}

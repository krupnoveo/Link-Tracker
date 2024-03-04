create table if not exists chat_to_link
(
    chat_id BIGINT REFERENCES chat(chat_id),
    link_id BIGINT REFERENCES link(id)
)

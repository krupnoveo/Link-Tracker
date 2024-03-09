create table if not exists link
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    url TEXT UNIQUE,
    updated_at TIMESTAMP WITH TIME ZONE,
    checked_at TIMESTAMP WITH TIME ZONE
)

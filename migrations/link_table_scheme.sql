create table if not exists link
(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    url TEXT,
    updated_at TIMESTAMP WITH TIME ZONE,
    checked_at TIMESTAMP WITH TIME ZONE,
        PRIMARY KEY (id),
        UNIQUE (url)
)

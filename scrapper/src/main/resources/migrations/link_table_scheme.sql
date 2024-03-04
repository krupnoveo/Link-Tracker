create table if not exists link
(
    id BIGSERIAL PRIMARY KEY,
    url TEXT UNIQUE,
    updated_at TIMESTAMP WITH TIME ZONE,
    checked_at TIMESTAMP WITH TIME ZONE
)

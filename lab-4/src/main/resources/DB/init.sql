CREATE TABLE IF NOT EXISTS hits (
                                    id BIGSERIAL PRIMARY KEY,
                                    x  DOUBLE PRECISION NOT NULL,
                                    y  DOUBLE PRECISION NOT NULL,
                                    r  DOUBLE PRECISION NOT NULL,
                                    hit BOOLEAN NOT NULL,
                                    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );
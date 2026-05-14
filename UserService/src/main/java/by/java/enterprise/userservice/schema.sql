CREATE TABLE "user"
(
    id UUID PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    phone VARCHAR(12) NOT NULL,
    role TEXT CHECK ( role IN ('CUSTOMER', 'ADMIN', 'SUPPORT') ) NOT NULL,
    registered_at TIMESTAMP NOT NULL
)
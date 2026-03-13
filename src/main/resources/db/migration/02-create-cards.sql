--liquibase formatted sql
--changeset Sergey:2
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'card'
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'
CREATE TABLE card (
    id BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(255) NOT NULL,
    owner_id BIGINT NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    CONSTRAINT fk_cards_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);
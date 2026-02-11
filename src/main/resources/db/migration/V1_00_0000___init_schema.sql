CREATE TABLE IF NOT EXISTS rest_response
(
    id              BIGSERIAL PRIMARY KEY,
    header_key      VARCHAR,
    header_value    VARCHAR,
    method          VARCHAR NOT NULL,
    path            VARCHAR NOT NULL,
    response_status VARCHAR NOT NULL,
    response_body   VARCHAR NOT NULL,
    delay_in_sec    BIGINT NOT NULL DEFAULT 0,
    delete_after    TIMESTAMP WITH TIME ZONE
);

CREAT INDEX IF NOT EXISTS idx_rest_response_method_path ON rest_response(method, path);

CREATE TABLE IF NOT EXISTS mq_response
(
    id                  BIGSERIAL PRIMARY KEY,
    header_key          VARCHAR,
    header_value        VARCHAR,
    queue               VARCHAR NOT NULL,
    response_body       VARCHAR NOT NULL,
    payload_type        VARCHAR NOT NULL,
    matching_expression VARCHAR NOT NULL,
    delay_in_sec        BIGINT NOT NULL DEFAULT 0,
    delete_after        TIMESTAMP WITH TIME ZONE
);
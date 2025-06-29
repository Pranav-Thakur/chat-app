CREATE TABLE publisher (
    --id UUID PRIMARY KEY, for later or postgre
                           id BINARY(16) PRIMARY KEY,
                           company_name VARCHAR(255),
                           info TEXT,
                           status VARCHAR(50),
                           created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           version INT
);

CREATE INDEX idx_publisher_company_name ON publisher(company_name);

CREATE TABLE app_user (
    --id UUID PRIMARY KEY, for later or postgre
                          id BINARY(16) PRIMARY KEY,
                          name VARCHAR(255),
                          info TEXT,
                          status VARCHAR(50),
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          VERSION INT
);

CREATE TABLE topic (
    --id UUID PRIMARY KEY, for later or postgre
                       id BINARY(16) PRIMARY KEY,
                       publisher_id BINARY(16),
                       topic_name VARCHAR(255),
                       topic_desc VARCHAR(255),
                       status VARCHAR(50),
                       created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       VERSION INT,
                       FOREIGN KEY (publisher_id) REFERENCES publisher (id)
);

CREATE INDEX idx_topic_name ON topic(topic_name);


CREATE TABLE subscriber (
    --id UUID PRIMARY KEY, for later or postgre
                            id BINARY(16) PRIMARY KEY,
                            user_id BINARY(16),
                            topic_id BINARY(16),
                            offset_time TIMESTAMP,
                            status VARCHAR(50),
                            created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            version INT,
                            FOREIGN KEY (user_id) REFERENCES app_user(id),
                            FOREIGN KEY (topic_id) REFERENCES topic(id)
);

-- ALTER TABLE `pubsub_db`.`subscriber` CHANGE `last_message_delivered_date` `offset_time` timestamp NULL;
CREATE INDEX idx_subscriber_topic_id ON subscriber(topic_id);
CREATE INDEX idx_subscriber_user_id ON subscriber(user_id);

CREATE TABLE message (
     --id UUID PRIMARY KEY, for later or postgre
                         id BINARY(16) PRIMARY KEY,
                         data TEXT,
                         topic_id BINARY(16),
                         status VARCHAR(50),
                         created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         version INT,
                         FOREIGN KEY (topic_id) REFERENCES topic(id)
);

CREATE INDEX idx_message_topic_id ON message(topic_id);

CREATE TABLE transaction (
    --id UUID PRIMARY KEY, for later or postgre
                             id BINARY(16) PRIMARY KEY,
                             message_id BINARY(16),
                             subscriber_id BINARY(16),
                             status VARCHAR(50),
                             created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             version INT,
                             FOREIGN KEY (message_id) REFERENCES message(id),
                             FOREIGN KEY (subscriber_id) REFERENCES subscriber(id)
);

CREATE INDEX idx_message_id ON transaction(message_id);
CREATE INDEX idx_subscriber_id ON transaction(subscriber_id);
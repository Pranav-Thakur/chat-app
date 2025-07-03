create table app_user (
                          user_id BINARY(16) not null,
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          version integer not null,
                          about varchar(255),
                          email varchar(255) not null,
                          info TEXT,
                          name varchar(255) not null,
                          phone varchar(255) not null unique,
                          status varchar(255),
                          primary key (user_id)
) engine=InnoDB;

create index idx_chatapp_user_phone on chatapp_user (phone);
create index idx_chatapp_user_email on chatapp_user (email);

create table login_user (
                            session_id BINARY(16) not null,
                            created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            version integer not null,
                            agent varchar(255),
                            info TEXT,
                            did varchar(255) not null,
                            otp varchar(255),
                            phone varchar(255) not null,
                            status varchar(255),
                            primary key (session_id)
) engine=InnoDB;

create index idx_login_user_phone on login_user (phone);
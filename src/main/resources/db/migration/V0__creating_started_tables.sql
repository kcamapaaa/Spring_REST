CREATE TABLE IF NOT EXISTS files
(
    id            int AUTO_INCREMENT,
    file_name     varchar(100) NOT NULL,
    location      varchar(300) NOT NULL,
    creation_date timestamp    NOT NULL,
    updated       timestamp    NOT NULL,
    status        varchar(20)  NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id            int AUTO_INCREMENT,
    username      varchar(150) NOT NULL UNIQUE,
    password      varchar(200) NOT NULL,
    creation_date timestamp    NOT NULL DEFAULT NOW(),
    updated       timestamp    NOT NULL DEFAULT NOW(),
    status        varchar(20)  NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id            int AUTO_INCREMENT,
    user_id       int         NOT NULL,
    file_id       int         NOT NULL,
    action        varchar(50) NOT NULL,
    creation_date timestamp   NOT NULL DEFAULT NOW(),
    updated       timestamp   NOT NULL DEFAULT NOW(),
    status        varchar(20) NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (id),
    FOREIGN KEY (file_id) REFERENCES files (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles
(
    id            int AUTO_INCREMENT,
    name          varchar(50) NOT NULL UNIQUE,
    creation_date timestamp   NOT NULL DEFAULT NOW(),
    updated       timestamp   NOT NULL DEFAULT NOW(),
    status        varchar(20) NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_role
(
    user_id int NOT NULL,
    role_id int NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE ON UPDATE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE RESTRICT
);
-- noinspection SqlNoDataSourceInspectionForFile

DELETE
FROM articles;

ALTER TABLE articles AUTO_INCREMENT = 1;

INSERT INTO articles (title, body)
VALUES ('タイトルです1', '1本文です。')
     , ('タイトルです2', '2本文です。')
     , ('タイトルです3', '3本文です。')
;

DELETE
FROM users;

-- password is "password" for all users
INSERT INTO users(username, password, enabled)
VALUES ('user1', '$2a$10$jeD56WgR/b1/6uT4Ex.e3uGRcXjuVxGWvsVfMdChOJm/yX68agIF.', true)
     , ('user2', '$2a$10$5ssPPLpTmr55IO1LyTemC.sU319RHZ6Vz8rwiYHxTIUhgwQtJDusK', true)
     , ('user3', '$2a$10$8fZBKIsMBQiXZ5lfAwom3OEFgtYr0BMl1QBKOxldb2fNb2/j/vEJe', true)
;
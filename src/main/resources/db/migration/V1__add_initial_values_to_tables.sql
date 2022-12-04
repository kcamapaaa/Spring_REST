insert into roles (name, creation_date, updated) values ('ROLE_USER', NOW(), NOW());
insert into roles (name, creation_date, updated) values ('ROLE_MODERATOR', NOW(), NOW());
insert into roles (name, creation_date, updated) values ('ROLE_ADMIN', NOW(), NOW());

insert into users(username, password, creation_date, updated) value ('user', '$2a$04$cEYU5m32SuQYyFL.P.GD8e2Y9UhqvpkzNWVGRMgMhnBTB2uP1.YIG', NOW(), NOW());
insert into users(username, password, creation_date, updated) value ('moderator', '$2a$04$cEYU5m32SuQYyFL.P.GD8e2Y9UhqvpkzNWVGRMgMhnBTB2uP1.YIG', NOW(), NOW());
insert into users(username, password, creation_date, updated) value ('admin', '$2a$04$cEYU5m32SuQYyFL.P.GD8e2Y9UhqvpkzNWVGRMgMhnBTB2uP1.YIG', NOW(), NOW());
insert into users(username, password, creation_date, updated, status) value ('vasya', '$2a$04$cEYU5m32SuQYyFL.P.GD8e2Y9UhqvpkzNWVGRMgMhnBTB2uP1.YIG', NOW(), NOW(), 'DELETED');

insert into user_role(user_id, role_id) values (1, 1);
insert into user_role(user_id, role_id) values (2, 1);
insert into user_role(user_id, role_id) values (2, 2);
insert into user_role(user_id, role_id) values (3, 1);
insert into user_role(user_id, role_id) values (3, 2);
insert into user_role(user_id, role_id) values (3, 3);
create table users
(
    id       uuid not null
        primary key,
    username varchar(100),
    email    varchar(200),
    password varchar(200)
);

create table roles(
                      id UUID primary key,
                      name varchar(50)
);

create table user_roles(
                           user_id UUID references users(id) on delete cascade ,
                           role_id UUID references roles(id) on delete cascade
);

create table conversation (
                              id UUID primary key,
    conv_id varchar,
                              from_user UUID,
                              to_user UUID,
                              content text,
    delivery_status varchar,
    time timestamp,
    last_modified timestamp
);

insert into roles(id, name) values
                                (gen_random_uuid(), 'USER'),
                                (gen_random_uuid(), 'ADMIN'),
                                (gen_random_uuid(), 'MODERATOR');

-- list unread messages:
select u_from.username, u_to.username, m.content
from messages_in_transit m
         inner join users u_from on m.from_user = u_from.id
         inner join users u_to on m.to_user = u_to.id
where m.read = false;
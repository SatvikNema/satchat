create table if not exists users (
    id uuid not null primary key,
    username varchar(100),
    email varchar(200),
    password varchar(200)
);

create table if not exists roles(
    id UUID primary key,
    name varchar(50)
);

create table if not exists user_roles(
    user_id UUID references users(id) on delete cascade,
    role_id UUID references roles(id) on delete cascade
);

create table if not exists conversation (
    id UUID primary key,
    conv_id varchar,
    from_user UUID,
    to_user UUID,
    content text,
    delivery_status varchar,
    time timestamp,
    last_modified timestamp
);

truncate table user_roles cascade;
truncate table roles cascade;
truncate table users cascade;
truncate table conversation;

insert into
    roles(id, name)
values
    (gen_random_uuid(), 'USER'),
    (gen_random_uuid(), 'ADMIN'),
    (gen_random_uuid(), 'MODERATOR');
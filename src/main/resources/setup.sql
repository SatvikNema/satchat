create table "user" (
                        id UUID primary key,
                        username varchar(100)
);

create table conversation (
                              id UUID primary key,
                              from_user UUID,
                              to_user UUID,
                              time timestamp,
                              content text
);

create table messages_in_transit(
                                    id UUID primary key,
                                    from_user UUID,
                                    to_user UUID,
                                    time timestamp,
                                    read bool,
                                    sender_notified bool
);



insert into "user" (id, username) values (gen_random_uuid(), 'Satvik');
insert into "user" (id, username) values (gen_random_uuid(), 'Keshav');
insert into "user" (id, username) values (gen_random_uuid(), 'Rachana');
insert into "user" (id, username) values (gen_random_uuid(), 'Khevna');
insert into "user" (id, username) values (gen_random_uuid(), 'Kartik');

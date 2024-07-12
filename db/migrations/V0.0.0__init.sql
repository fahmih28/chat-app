create table if not exists "user"(
    id varchar(255) primary key,
    email varchar(255) not null,
    password text not null,
    first_name text not null,
    last_name text,
    chat_seq bigint,
    created_at timestamp not null,
    created_by text not null,
    updated_at timestamp,
    updated_by text
);

create table if not exists access_module
(
    id         varchar(255) primary key,
    code       varchar(255) not null,
    qualifier  INT       not null check ( qualifier >= 0),
    created_at timestamp    not null,
    created_by text         not null,
    updated_at timestamp,
    updated_by text
);

create table if not exists role
(
    id         varchar(255) primary key,
    name       text      not null,
    qualifier  bigint[] not null,
    created_at timestamp not null,
    created_by text      not null,
    updated_at timestamp,
    updated_by text
);

create table if not exists role_access_module
(
    id               varchar(255) primary key,
    role_id          varchar(255) not null references role (id),
    access_module_id varchar(255) not null references access_module (id),
    created_at       timestamp    not null,
    created_by       text         not null,
    updated_at       timestamp,
    updated_by       text,
    unique (access_module_id, role_id)
);

create table if not exists user_role
(
    id      varchar(255) primary key,
    user_id varchar(255) not null references "user" (id),
    role_id varchar(255) not null references role (id),
    created_at       timestamp    not null,
    created_by       text         not null,
    updated_at       timestamp,
    updated_by       text,
    unique (user_id, role_id)
);

create index user_email on "user"(email);

create index user_role_user_id on user_role(user_id);

create index access_module_code on access_module(code);


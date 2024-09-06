create table members
(
    id              bigserial
        primary key,
    created_at      timestamp(6),
    deleted_at      timestamp(6),
    modified_at     timestamp(6),
    email           varchar(255),
    image_file_name varchar(255),
    nickname        varchar(255),
    role            varchar(255) not null
        constraint members_role_check
            check ((role)::text = ANY ((ARRAY ['GUEST'::character varying, 'USER'::character varying])::text[]))
);

create table challenge
(
    id                             bigserial
        primary key,
    created_at                     timestamp(6),
    deleted_at                     timestamp(6),
    modified_at                    timestamp(6),
    description                    varchar(255),
    end_date                       timestamp(6) with time zone not null,
    fee_per_absence                integer                     not null,
    is_paid_all                    boolean                     not null,
    number_of_participants         integer                     not null,
    participating_days             smallint                    not null,
    start_date                     timestamp(6) with time zone not null,
    stop_date                      timestamp(6) with time zone,
    title                          varchar(255)                not null,
    total_absence_fee              integer                     not null,
    total_participating_days_count integer                     not null,
    member_id                      bigint
        constraint fkn9x3utodenhttyfqbjwc5mid6
            references members
);

create table challenge_enrollment
(
    id            bigserial
        primary key,
    enrolled_date timestamp(6) with time zone not null,
    failure_count integer                     not null,
    given_up_date timestamp(6) with time zone,
    is_given_up   boolean                     not null,
    success_count integer                     not null,
    total_fee     integer                     not null,
    challenge_id  bigint
        constraint fkht9bwn074v9qio0sv5g9lmj7u
            references challenge,
    member_id     bigint
        constraint fkgh86pa3g6ibj0qs84i0ff9nj9
            references members
);

create table challenge_post
(
    id                      bigserial
        primary key,
    created_at              timestamp(6),
    deleted_at              timestamp(6),
    modified_at             timestamp(6),
    content                 varchar(255),
    is_announcement         boolean not null,
    challenge_id            bigint
        constraint fkjxgfsdku2gicitj7hlc6f72el
            references challenge,
    challenge_enrollment_id bigint
        constraint fkso6gphwlpuu13gmwtp9n9x7u3
            references challenge_enrollment
);

create table challenge_participation_record
(
    id                      bigserial
        primary key,
    created_at              timestamp(6),
    deleted_at              timestamp(6),
    modified_at             timestamp(6),
    challenge_enrollment_id bigint
        constraint fk7fvnqtg2ho35lfkuh89gl9in3
            references challenge_enrollment,
    challenge_post_id       bigint
        constraint uk_t331uq6b60m6ae6iiuy8vdbsx
            unique
        constraint fkreq434dwtr3c120623xcxt92n
            references challenge_post
);

create table post_photo
(
    id                bigserial
        primary key,
    created_at        timestamp(6),
    deleted_at        timestamp(6),
    modified_at       timestamp(6),
    image_file_name   varchar(255),
    view_order        bigint not null,
    challenge_post_id bigint
        constraint fk4r2htmcsln80euahamm7j9can
            references challenge_post
);

create table refresh_token
(
    id            bigserial
        primary key,
    login_ip      varchar(255) not null,
    refresh_token varchar(255) not null,
    member_id     bigint
        constraint uk_dnbbikqdsc2r2cee1afysqfk9
            unique
        constraint fk1la7i8rlx3kxdm10elpuws6vd
            references members
);

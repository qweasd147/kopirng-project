create table search_log
(
    idx          bigint       not null auto_increment,
    `contents`   varchar(200) not null,
    is_collected BOOLEAN,
    created_at   datetime,
    updated_at   datetime,
    primary key (idx)
);

-- CREATE INDEX INDEX_SearchLog_Contents ON search_log (contents);

create table search_word
(
    idx        bigint      not null auto_increment,
    word       varchar(50) not null,
    `count`    bigint      not null,
    created_at datetime,
    updated_at datetime,
    primary key (idx)
);

CREATE INDEX INDEX_SearchWord_Word ON search_word (word);
CREATE INDEX INDEX_SearchWord_Count ON search_word (`count`);
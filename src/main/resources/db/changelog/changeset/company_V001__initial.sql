CREATE TABLE company
(
    inn     varchar(12) PRIMARY KEY,
    name    varchar(256) NOT NULL,
    address varchar(512) NOT NULL,
    kpp     varchar(9)   NOT NULL,
    ogrn    varchar(13)  NOT NULL
);

CREATE TABLE vehicle
(
    vin          varchar(17) PRIMARY KEY,
    release_year int         NOT NULL,
    company_inn  varchar(12) NOT NULL,

    CONSTRAINT fk_company_inn FOREIGN KEY (company_inn) REFERENCES company (inn) ON DELETE CASCADE
);
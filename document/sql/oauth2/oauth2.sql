CREATE TABLE `sys_oauth2_client`
(
    `id`                            varchar(100)  NOT NULL,
    `client_id`                     varchar(100)  NOT NULL,
    `client_id_issued_at`           datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `client_secret`                 varchar(200),
    `client_secret_expires_at`      datetime,
    `client_name`                   varchar(200)  NOT NULL,
    `client_authentication_methods` varchar(1000) NOT NULL,
    `authorization_grant_types`     varchar(1000) NOT NULL,
    `redirect_uris`                 varchar(1000),
    `scopes`                        varchar(1000) NOT NULL,
    `client_settings`               varchar(2000) NOT NULL,
    `token_settings`                varchar(2000) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `sys_oauth2_auth`
(
    `id`                            varchar(100) NOT NULL,
    `registered_client_id`          varchar(100) NOT NULL,
    `principal_name`                varchar(200) NOT NULL,
    `authorization_grant_type`      varchar(100) NOT NULL,
    `authorized_scopes`             varchar(1000),
    `attributes`                    text,
    `state`                         varchar(500),
    `authorization_code_value`      text,
    `authorization_code_issued_at`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `authorization_code_expires_at` datetime,
    `authorization_code_metadata`   text,
    `access_token_value`            text,
    `access_token_issued_at`        datetime,
    `access_token_expires_at`       datetime,
    `access_token_metadata`         text,
    `access_token_type`             varchar(100),
    `access_token_scopes`           varchar(1000),
    `oidc_id_token_value`           text,
    `oidc_id_token_issued_at`       datetime,
    `oidc_id_token_expires_at`      datetime,
    `oidc_id_token_metadata`        text,
    `refresh_token_value`           text,
    `refresh_token_issued_at`       datetime,
    `refresh_token_expires_at`      datetime,
    `refresh_token_metadata`        text,
    PRIMARY KEY (`id`)
);

CREATE TABLE `sys_oauth2_auth_consent`
(
    `registered_client_id` varchar(100)  NOT NULL,
    `principal_name`       varchar(200)  NOT NULL,
    `authorities`          varchar(1000) NOT NULL,
    PRIMARY KEY (`registered_client_id`, `principal_name`)
);
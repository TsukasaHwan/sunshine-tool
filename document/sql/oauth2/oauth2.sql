CREATE TABLE `sys_oauth2_client`
(
    `id`                            varchar(100)  NOT NULL,
    `client_id`                     varchar(100)  NOT NULL,
    `client_id_issued_at`           datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `client_secret`                 varchar(200)  NULL     DEFAULT NULL,
    `client_secret_expires_at`      datetime      NOT NULL DEFAULT '0000-00-00 00:00:00',
    `client_name`                   varchar(200)  NOT NULL,
    `client_authentication_methods` varchar(1000) NOT NULL,
    `authorization_grant_types`     varchar(1000) NOT NULL,
    `redirect_uris`                 varchar(1000) NULL     DEFAULT NULL,
    `scopes`                        varchar(1000) NOT NULL,
    `client_settings`               varchar(2000) NOT NULL,
    `token_settings`                varchar(2000) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `sys_oauth2_auth`
(
    `id`                            varchar(100)  NOT NULL,
    `registered_client_id`          varchar(100)  NOT NULL,
    `principal_name`                varchar(200)  NOT NULL,
    `authorization_grant_type`      varchar(100)  NOT NULL,
    `authorized_scopes`             varchar(1000) NULL     DEFAULT NULL,
    `attributes`                    text          NULL,
    `state`                         varchar(500)  NULL     DEFAULT NULL,
    `authorization_code_value`      text          NULL,
    `authorization_code_issued_at`  datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `authorization_code_expires_at` datetime      NOT NULL DEFAULT '0000-00-00 00:00:00',
    `authorization_code_metadata`   text          NULL,
    `access_token_value`            text          NULL,
    `access_token_issued_at`        datetime      NOT NULL DEFAULT '0000-00-00 00:00:00',
    `access_token_expires_at`       datetime      NOT NULL DEFAULT '0000-00-00 00:00:00',
    `access_token_metadata`         text          NULL,
    `access_token_type`             varchar(100)  NULL     DEFAULT NULL,
    `access_token_scopes`           varchar(1000) NULL     DEFAULT NULL,
    `oidc_id_token_value`           text          NULL,
    `oidc_id_token_issued_at`       datetime      NOT NULL DEFAULT '0000-00-00 00:00:00',
    `oidc_id_token_expires_at`      datetime      NOT NULL DEFAULT '0000-00-00 00:00:00',
    `oidc_id_token_metadata`        text          NULL,
    `refresh_token_value`           text          NULL,
    `refresh_token_issued_at`       datetime      NOT NULL DEFAULT '0000-00-00 00:00:00',
    `refresh_token_expires_at`      datetime      NOT NULL DEFAULT '0000-00-00 00:00:00',
    `refresh_token_metadata`        text          NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `sys_oauth2_auth_consent`
(
    `registered_client_id` varchar(100)  NOT NULL,
    `principal_name`       varchar(200)  NOT NULL,
    `authorities`          varchar(1000) NOT NULL,
    PRIMARY KEY (`registered_client_id`, `principal_name`)
);
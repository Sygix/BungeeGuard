-- Create syntax for TABLE 'bungee_config'
CREATE TABLE `bungee_config` (
  `max_players` INT(11)  NOT NULL,
  `motd`        TINYTEXT NOT NULL,
  `permissions` TEXT     NOT NULL
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Create syntax for TABLE 'bungee_forced_host'
CREATE TABLE `bungee_forced_host` (
  `ip`        VARCHAR(40) NOT NULL DEFAULT '',
  `to_server` VARCHAR(40) NOT NULL DEFAULT ''
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Create syntax for TABLE 'bungee_instances'
CREATE TABLE `bungee_instances` (
  `id`           INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `server_id`    VARCHAR(40)      NOT NULL DEFAULT '',
  `bind_address` VARCHAR(40) DEFAULT '0.0.0.0:25565',
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =3
  DEFAULT CHARSET =utf8;

-- Create syntax for TABLE 'bungee_servers'
CREATE TABLE `bungee_servers` (
  `id`      INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`    VARCHAR(40) DEFAULT NULL,
  `address` VARCHAR(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =5
  DEFAULT CHARSET =utf8;
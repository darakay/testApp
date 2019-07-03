CREATE TABLE  IF NOT EXISTS `user` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`name` varchar(20) NOT NULL,
	`password` varchar(20) NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS  `account` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`sum` double,
	`tariff_name` varchar(50) NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS  `card` (
	`id` bigint NOT NULL UNIQUE,
	`account_id` bigint NOT NULL,
	`user_id` bigint NOT NULL,
	`limit` double NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS  `tariff` (
	`name` varchar(50) NOT NULL,
	`type` varchar(20) NOT NULL,
	`rate` FLOAT NOT NULL UNIQUE,
	PRIMARY KEY (`name`)
);

CREATE TABLE  IF NOT EXISTS `account_user` (
	`user_id` bigint NOT NULL,
	`account_id` bigint NOT NULL,
	`user_status` varchar(20) NOT NULL,
	PRIMARY KEY (`user_id`,`account_id`)
);

ALTER TABLE `account` ADD CONSTRAINT `account_fk0` FOREIGN KEY (`tariff_name`) REFERENCES `tariff`(`name`);

ALTER TABLE `card` ADD CONSTRAINT `card_fk0` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);

ALTER TABLE `card` ADD CONSTRAINT `card_fk1` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `account_user` ADD CONSTRAINT `account_to_user_fk0` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `account_user` ADD CONSTRAINT `account_to_user_fk1` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);


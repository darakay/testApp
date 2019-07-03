CREATE TABLE `user` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`name` varchar(20) NOT NULL,
	`password` varchar(20) NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `account` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`sum` double NOT NULL,
	`tarif_name` varchar(50) NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `card` (
	`id` bigint NOT NULL UNIQUE,
	`account_id` bigint NOT NULL,
	`user_id` bigint NOT NULL AUTO_INCREMENT,
	`limit` double NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `tarifs` (
	`name` varchar(50) NOT NULL,
	`type` varchar(20) NOT NULL,
	`rate` FLOAT NOT NULL UNIQUE,
	PRIMARY KEY (`name`)
);

CREATE TABLE `account_to_user` (
	`user_id` bigint NOT NULL,
	`account_id` bigint NOT NULL,
	`user_status` varchar(20) NOT NULL,
	PRIMARY KEY (`user_id`,`account_id`)
);

ALTER TABLE `account` ADD CONSTRAINT `account_fk0` FOREIGN KEY (`tarif_name`) REFERENCES `tarifs`(`name`);

ALTER TABLE `card` ADD CONSTRAINT `card_fk0` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);

ALTER TABLE `card` ADD CONSTRAINT `card_fk1` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `account_to_user` ADD CONSTRAINT `account_to_user_fk0` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `account_to_user` ADD CONSTRAINT `account_to_user_fk1` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);


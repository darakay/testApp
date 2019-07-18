CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `first_name` varchar(20) NOT NULL,
                        `last_name` varchar(50) NOT NULL,
                        `login` varchar(50) NOT NULL,
                        `password` varchar(20) NOT NULL,
                        `refresh_token` varchar(20),
                        `expires_at` timestamp,
                        PRIMARY KEY (`id`)
);

CREATE TABLE `account` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `sum` double NOT NULL,
                           `tariff_name` varchar(50) NOT NULL,
                           `owner_id` bigint,
                           PRIMARY KEY (`id`)
);

CREATE TABLE `card` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `account_id` bigint NOT NULL,
                        `user_id` bigint NOT NULL,
                        PRIMARY KEY (`id`)
);

CREATE TABLE `tariff` (
                          `name` varchar(50) NOT NULL,
                          `type` varchar(20) NOT NULL,
                          `rate` double NOT NULL UNIQUE,
                          `owner_limit` double NOT NULL,
                          `user_limit` double NOT NULL,
                          PRIMARY KEY (`name`)
);

CREATE TABLE `user_account` (
                                `user_id` bigint NOT NULL,
                                `account_id` bigint NOT NULL,
                                PRIMARY KEY (`user_id`,`account_id`)
);

CREATE TABLE `transaction` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `user_id` bigint NOT NULL,
                               `account_id` bigint NOT NULL,
                               `recipient_id` bigint NOT NULL,
                               `sum` double NOT NULL,
                               `type` varchar(20) NOT NULL,
                               `date` date,
                               `time` time,
                               PRIMARY KEY (`id`)
);

ALTER TABLE `account` ADD CONSTRAINT `account_fk0` FOREIGN KEY (`tariff_name`) REFERENCES `tariff`(`name`);

ALTER TABLE `account` ADD CONSTRAINT `account_fk1` FOREIGN KEY (`owner_id`) REFERENCES `user`(`id`);

ALTER TABLE `card` ADD CONSTRAINT `card_fk0` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);

ALTER TABLE `card` ADD CONSTRAINT `card_fk1` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `user_account` ADD CONSTRAINT `user_account_fk0` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `user_account` ADD CONSTRAINT `user_account_fk1` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);

ALTER TABLE `transaction` ADD CONSTRAINT `transaction_fk0` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `transaction` ADD CONSTRAINT `transaction_fk1` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);

ALTER TABLE `transaction` ADD CONSTRAINT `transaction_fk2` FOREIGN KEY (`recipient_id`) REFERENCES `account`(`id`);

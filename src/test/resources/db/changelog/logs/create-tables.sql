USE test;

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `first_name` varchar(20) NOT NULL,
                        `last_name` varchar(50) NOT NULL,
                        `password` varchar(20) NOT NULL,
                        PRIMARY KEY (`id`)
);

CREATE TABLE `account` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `sum` double NOT NULL,
                           `tarif_id` bigint NOT NULL,
                           PRIMARY KEY (`id`)
);

CREATE TABLE `card` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `account_id` bigint NOT NULL,
                        `user_id` bigint NOT NULL,
                        PRIMARY KEY (`id`)
);

CREATE TABLE `tarif` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `name` varchar(50) NOT NULL,
                         `type` varchar(20) NOT NULL,
                         `rate` double NOT NULL UNIQUE,
                         `owner_limit` double NOT NULL,
                         `user_limit` double NOT NULL,
                         PRIMARY KEY (`id`)
);

CREATE TABLE `account_to_user` (
                                   `user_id` bigint NOT NULL,
                                   `account_id` bigint NOT NULL,
                                   `status` varchar(20) NOT NULL,
                                   PRIMARY KEY (`user_id`,`account_id`)
);

CREATE TABLE `transaction` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `user_id` bigint NOT NULL,
                               `account_id` bigint NOT NULL,
                               `recipient_id` bigint NOT NULL,
                               `sum` double NOT NULL,
                               PRIMARY KEY (`id`)
);

ALTER TABLE `account` ADD CONSTRAINT `account_fk0` FOREIGN KEY (`tarif_id`) REFERENCES `tarif`(`id`);

ALTER TABLE `card` ADD CONSTRAINT `card_fk0` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);

ALTER TABLE `card` ADD CONSTRAINT `card_fk1` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `account_to_user` ADD CONSTRAINT `account_to_user_fk0` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `account_to_user` ADD CONSTRAINT `account_to_user_fk1` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);

ALTER TABLE `transaction` ADD CONSTRAINT `transaction_fk0` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `transaction` ADD CONSTRAINT `transaction_fk1` FOREIGN KEY (`account_id`) REFERENCES `account`(`id`);

ALTER TABLE `transaction` ADD CONSTRAINT `transaction_fk2` FOREIGN KEY (`recipient_id`) REFERENCES `account`(`id`);

DROP TABLE IF EXISTS posts;
CREATE TABLE `posts` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(500) NOT NULL,
    `content` TEXT NOT NULL,
    `create_at` datetime,
    primary key (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARACTER SET utf8 collate utf8_general_ci;
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

ALTER TABLE `excord`.`ec_history` CHANGE COLUMN `change_date` `change_date` DATETIME NOT NULL, ADD COLUMN `slug` VARCHAR(45) NULL DEFAULT NULL
, DROP PRIMARY KEY 
, ADD PRIMARY KEY (`id`, `change_date`) ;

ALTER TABLE `excord`.`ec_requirement` ADD COLUMN `slug` VARCHAR(45) NULL DEFAULT NULL;

ALTER TABLE `excord`.`ec_testcase` ADD COLUMN `slug` VARCHAR(45) NULL DEFAULT NULL;

ALTER TABLE `excord`.`ec_testplan` ADD COLUMN `slug` VARCHAR(45) NULL DEFAULT NULL;

ALTER TABLE `excord`.`ec_testfolder` ADD COLUMN `slug` VARCHAR(45) NULL DEFAULT NULL;

update `excord`.`ec_testfolder` set `slug` = 'root-testfolder-slug' where id = 1;
update `excord`.`ec_testfolder` set `slug` = 'trash-testfolder-slug' where id = 2;
update `excord`.`ec_requirement` set `slug` = 'root-requirement-slug' where id = 1;

ALTER TABLE `excord`.`ec_history` PARTITION BY LIST(MOD(YEAR(`change_date`),10))
(
PARTITION p_00 VALUES IN ( 0 ),
PARTITION p_01 VALUES IN ( 1 ),
PARTITION p_02 VALUES IN ( 2 ),
PARTITION p_03 VALUES IN ( 3 ),
PARTITION p_04 VALUES IN ( 4 ),
PARTITION p_05 VALUES IN ( 5 ),
PARTITION p_06 VALUES IN ( 6 ),
PARTITION p_07 VALUES IN ( 7 ),
PARTITION p_08 VALUES IN ( 8 ),
PARTITION p_09 VALUES IN ( 9 ),
PARTITION p_10 VALUES IN ( 10 )
);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema excord
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema excord
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `excord` DEFAULT CHARACTER SET utf8 ;
USE `excord` ;

-- -----------------------------------------------------
-- Table `excord`.`ec_testfolder`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `excord`.`ec_testfolder` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `name` VARCHAR(90) NOT NULL COMMENT '',
  `parent_id` BIGINT(20) NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  UNIQUE INDEX `testfolder_uq1` (`name` ASC, `parent_id` ASC)  COMMENT '',
  INDEX `testfolder_idx1` (`name` ASC)  COMMENT '',
  INDEX `testfolder_idx2` (`parent_id` ASC)  COMMENT '',
  CONSTRAINT `testfolder_fk1`
    FOREIGN KEY (`parent_id`)
    REFERENCES `excord`.`ec_testfolder` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 18678
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testcase`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `excord`.`ec_testcase` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `name` VARCHAR(90) NOT NULL COMMENT '',
  `description` LONGTEXT NOT NULL COMMENT '',
  `enabled` TINYINT(1) NOT NULL COMMENT '',
  `automated` TINYINT(1) NOT NULL COMMENT '',
  `folder_id` BIGINT NOT NULL COMMENT '',
  `added_version` VARCHAR(45) NULL COMMENT '',
  `deprecated_version` VARCHAR(45) NULL DEFAULT NULL COMMENT '',
  `bug_id` VARCHAR(45) NULL DEFAULT NULL COMMENT '',
  `language` VARCHAR(45) NULL DEFAULT NULL COMMENT '',
  `test_script_file` VARCHAR(90) NULL DEFAULT NULL COMMENT '',
  `method_name` VARCHAR(45) NULL DEFAULT NULL COMMENT '',
  `priority` VARCHAR(10) NOT NULL COMMENT '',
  `product` VARCHAR(45) NULL DEFAULT NULL COMMENT '',
  `feature` VARCHAR(45) NULL DEFAULT NULL COMMENT '',
  `case_type` VARCHAR(45) NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `testcase_idx1` (`name` ASC)  COMMENT '',
  INDEX `testcase_idx2` (`folder_id` ASC)  COMMENT '',
  CONSTRAINT `testcase_fk1`
    FOREIGN KEY (`folder_id`)
    REFERENCES `excord`.`ec_testfolder` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 173584
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testplan`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `excord`.`ec_testplan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `name` VARCHAR(90) NOT NULL COMMENT '',
  `creator` VARCHAR(45) NOT NULL COMMENT '',
  `enabled` TINYINT(1) NOT NULL COMMENT '',
  `schedule` VARCHAR(45) NULL COMMENT '',
  `release_name` VARCHAR(45) NULL COMMENT '',
  `start_date` DATE NOT NULL COMMENT '',
  `end_date` DATE NULL DEFAULT NULL COMMENT '',
  `leader` VARCHAR(45) NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  UNIQUE INDEX `ec_testplan_unq1` (`name` ASC)  COMMENT '')
ENGINE = InnoDB
AUTO_INCREMENT = 675
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testplan_testcase_mapping`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `excord`.`ec_testplan_testcase_mapping` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `testcase_id` BIGINT NOT NULL COMMENT '',
  `testplan_id` BIGINT NOT NULL COMMENT '',
  `assigned_to` VARCHAR(45) NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  UNIQUE INDEX `testplan_testcase_mapping_unq1` (`testcase_id` ASC, `testplan_id` ASC)  COMMENT '',
  INDEX `testplan_testcase_mapping_idx1` (`testcase_id` ASC)  COMMENT '',
  INDEX `testplan_testcase_mapping_idx2` (`testplan_id` ASC)  COMMENT '',
  CONSTRAINT `testplan_testcase_mapping_fk1`
    FOREIGN KEY (`testcase_id`)
    REFERENCES `excord`.`ec_testcase` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `testplan_testcase_mapping_fk2`
    FOREIGN KEY (`testplan_id`)
    REFERENCES `excord`.`ec_testplan` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 138118
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testresult`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `excord`.`ec_testresult` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `testplan_testcase_link_id` BIGINT NOT NULL COMMENT '',
  `timestamp` DATETIME NOT NULL COMMENT '',
  `status` VARCHAR(20) NOT NULL COMMENT '',
  `environment` VARCHAR(90) NULL COMMENT '',
  `tester` VARCHAR(45) NOT NULL COMMENT '',
  `note` LONGTEXT NULL COMMENT '',
  `bug_ticket` VARCHAR(45) NULL DEFAULT NULL COMMENT '',
  `latest` TINYINT(1) NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `testresult_idx1` (`testplan_testcase_link_id` ASC)  COMMENT '',
  CONSTRAINT `testresult_fk1`
    FOREIGN KEY (`testplan_testcase_link_id`)
    REFERENCES `excord`.`ec_testplan_testcase_mapping` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_teststep`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `excord`.`ec_teststep` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `testcase_id` BIGINT NOT NULL COMMENT '',
  `step_number` INT NOT NULL COMMENT '',
  `description` LONGTEXT NOT NULL COMMENT '',
  `expected` LONGTEXT NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `teststep_fk1_idx` (`testcase_id` ASC)  COMMENT '',
  CONSTRAINT `teststep_fk1`
    FOREIGN KEY (`testcase_id`)
    REFERENCES `excord`.`ec_testcase` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testupload`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `excord`.`ec_testupload` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `testcase_id` BIGINT NULL COMMENT '',
  `caption` VARCHAR(90) NULL COMMENT '',
  `file_name` VARCHAR(90) NOT NULL COMMENT '',
  `file` BLOB NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `testupload_fk1_idx` (`testcase_id` ASC)  COMMENT '',
  CONSTRAINT `testupload_fk1`
    FOREIGN KEY (`testcase_id`)
    REFERENCES `excord`.`ec_testcase` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `excord`.`ec_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `change_user` VARCHAR(45) NULL COMMENT '',
  `change_date` DATETIME NULL COMMENT '',
  `change_summary` TEXT NULL COMMENT '',
  `change_ip` VARCHAR(90) NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `history_idx1` (`change_user` ASC)  COMMENT '')
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `excord`.`ec_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `excord`.`ec_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `username` VARCHAR(45) NOT NULL COMMENT '',
  `name` VARCHAR(90) NULL COMMENT '',
  `password` VARCHAR(45) NULL COMMENT '',
  `email` VARCHAR(90) NULL COMMENT '',
  `role` VARCHAR(10) NULL COMMENT '',
  `enabled` TINYINT(1) NOT NULL COMMENT '',
  `created_date` DATE NULL COMMENT '',
  `last_login` DATETIME NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '')
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

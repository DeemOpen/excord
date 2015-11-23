SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `excord` DEFAULT CHARACTER SET utf8 ;
USE `excord` ;

-- -----------------------------------------------------
-- Table `excord`.`ec_testfolder`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `excord`.`ec_testfolder` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(90) NOT NULL ,
  `parent_id` BIGINT(20) NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `testfolder_uq1` (`name` ASC, `parent_id` ASC) ,
  INDEX `testfolder_idx1` (`name` ASC) ,
  INDEX `testfolder_idx2` (`parent_id` ASC) ,
  CONSTRAINT `testfolder_fk1`
    FOREIGN KEY (`parent_id` )
    REFERENCES `excord`.`ec_testfolder` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 18678
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testcase`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `excord`.`ec_testcase` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(90) NOT NULL ,
  `description` LONGTEXT NOT NULL ,
  `enabled` TINYINT(1) NOT NULL ,
  `automated` TINYINT(1) NOT NULL ,
  `folder_id` BIGINT NOT NULL ,
  `added_version` VARCHAR(45) NULL ,
  `deprecated_version` VARCHAR(45) NULL DEFAULT NULL ,
  `bug_id` VARCHAR(45) NULL DEFAULT NULL ,
  `language` VARCHAR(45) NULL DEFAULT NULL ,
  `test_script_file` VARCHAR(90) NULL DEFAULT NULL ,
  `method_name` VARCHAR(45) NULL DEFAULT NULL ,
  `priority` VARCHAR(10) NULL DEFAULT NULL ,
  `product` VARCHAR(45) NULL DEFAULT NULL ,
  `feature` VARCHAR(45) NULL DEFAULT NULL ,
  `case_type` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `testcase_idx1` (`name` ASC) ,
  INDEX `testcase_idx2` (`folder_id` ASC) ,
  CONSTRAINT `testcase_fk1`
    FOREIGN KEY (`folder_id` )
    REFERENCES `excord`.`ec_testfolder` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 173584
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testplan`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `excord`.`ec_testplan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(90) NOT NULL ,
  `creator` VARCHAR(45) NOT NULL ,
  `enabled` TINYINT(1) NOT NULL ,
  `schedule` VARCHAR(45) NULL ,
  `release_name` VARCHAR(45) NULL ,
  `start_date` DATE NOT NULL ,
  `end_date` DATE NULL DEFAULT NULL ,
  `leader` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `ec_testplan_unq1` (`name` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 675
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testplan_testcase_mapping`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `excord`.`ec_testplan_testcase_mapping` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `testcase_id` BIGINT NOT NULL ,
  `testplan_id` BIGINT NOT NULL ,
  `assigned_to` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `testplan_testcase_mapping_unq1` (`testcase_id` ASC, `testplan_id` ASC) ,
  INDEX `testplan_testcase_mapping_idx1` (`testcase_id` ASC) ,
  INDEX `testplan_testcase_mapping_idx2` (`testplan_id` ASC) ,
  CONSTRAINT `testplan_testcase_mapping_fk1`
    FOREIGN KEY (`testcase_id` )
    REFERENCES `excord`.`ec_testcase` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `testplan_testcase_mapping_fk2`
    FOREIGN KEY (`testplan_id` )
    REFERENCES `excord`.`ec_testplan` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 138118
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testresult`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `excord`.`ec_testresult` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `testplan_testcase_link_id` BIGINT NOT NULL ,
  `timestamp` DATETIME NOT NULL ,
  `status` VARCHAR(20) NOT NULL ,
  `environment` VARCHAR(90) NULL ,
  `tester` VARCHAR(45) NOT NULL ,
  `note` LONGTEXT NULL ,
  `bug_ticket` VARCHAR(45) NULL DEFAULT NULL ,
  `latest` TINYINT(1) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `testresult_idx1` (`testplan_testcase_link_id` ASC) ,
  CONSTRAINT `testresult_fk1`
    FOREIGN KEY (`testplan_testcase_link_id` )
    REFERENCES `excord`.`ec_testplan_testcase_mapping` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_teststep`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `excord`.`ec_teststep` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `testcase_id` BIGINT NOT NULL ,
  `step_number` INT NOT NULL ,
  `description` LONGTEXT NOT NULL ,
  `expected` LONGTEXT NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `teststep_fk1_idx` (`testcase_id` ASC) ,
  CONSTRAINT `teststep_fk1`
    FOREIGN KEY (`testcase_id` )
    REFERENCES `excord`.`ec_testcase` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_testupload`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `excord`.`ec_testupload` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `testcase_id` BIGINT NULL ,
  `caption` VARCHAR(90) NULL ,
  `file_name` VARCHAR(90) NOT NULL ,
  `file` BLOB NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `testupload_fk1_idx` (`testcase_id` ASC) ,
  CONSTRAINT `testupload_fk1`
    FOREIGN KEY (`testcase_id` )
    REFERENCES `excord`.`ec_testcase` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `excord`.`ec_history`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `excord`.`ec_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `change_user` VARCHAR(45) NULL ,
  `change_date` DATETIME NULL ,
  `change_summary` TEXT NULL ,
  `change_ip` VARCHAR(90) NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `history_idx1` (`change_user` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `excord`.`ec_user`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `excord`.`ec_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `username` VARCHAR(45) NOT NULL ,
  `name` VARCHAR(90) NULL ,
  `password` VARCHAR(45) NULL ,
  `role` VARCHAR(10) NULL ,
  `enabled` TINYINT(1) NOT NULL ,
  `created_date` DATE NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;

USE `excord` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

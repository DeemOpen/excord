SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE TABLE IF NOT EXISTS `excord`.`ec_requirement` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '',
  `name` VARCHAR(90) NULL,
  `parent_id` BIGINT(20) NULL,
  `priority` VARCHAR(45) NULL,
  `status` VARCHAR(45) NULL,
  `release_name` VARCHAR(90) NULL,
  `product` VARCHAR(90) NULL,
  `coverage` TINYINT(1) NOT NULL,
  `story` TEXT NULL,
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `requirement_idx1` (`parent_id` ASC)  COMMENT '',
  UNIQUE INDEX `requirement_idx2` (`id` ASC, `parent_id` ASC)  COMMENT '',
  CONSTRAINT `requirement_mapping_fk1`
    FOREIGN KEY (`parent_id`)
    REFERENCES `excord`.`ec_requirement` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `excord`.`ec_testcase_requirement_mapping` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '',
  `requirement_id` BIGINT(20) NOT NULL,
  `testcase_id` BIGINT(20) NOT NULL,
  `review` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `tc_req_mapping_fk_idx` (`requirement_id` ASC)  COMMENT '',
  INDEX `tc_req_mapping_fk2_idx` (`testcase_id` ASC)  COMMENT '',
  CONSTRAINT `tc_req_mapping_fk1`
    FOREIGN KEY (`requirement_id`)
    REFERENCES `excord`.`ec_requirement` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `tc_req_mapping_fk2`
    FOREIGN KEY (`testcase_id`)
    REFERENCES `excord`.`ec_testcase` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

INSERT INTO `excord`.`ec_requirement` (`name`, `story`, `priority`, `status`, `release_name`, `product`,`coverage`) VALUES ('ROOT', '', 'P1', 'ACTIVE', '', '','0');

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

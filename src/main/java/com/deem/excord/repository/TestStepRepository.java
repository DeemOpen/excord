package com.deem.excord.repository;

import com.deem.excord.domain.EcTeststep;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TestStepRepository extends CrudRepository<EcTeststep, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from ec_teststep where testcase_id = :testcaseId", nativeQuery = true)
    public Integer deleteTeststepByTestcaseId(@Param("testcaseId") Long testcaseId);
}

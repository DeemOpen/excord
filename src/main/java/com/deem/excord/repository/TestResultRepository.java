package com.deem.excord.repository;

import com.deem.excord.domain.EcTestresult;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TestResultRepository extends CrudRepository<EcTestresult, Long> {

    @Modifying
    @Transactional
    @Query(value = "update ec_testresult set latest = 0 where testplan_testcase_link_id = :testplantestcaseId", nativeQuery = true)
    public Integer updateAllTestRunsLatestFlag(@Param("testplantestcaseId") Long testplantestcaseId);

    @Query(value = "SELECT b.* FROM ec_testplan_testcase_mapping a, ec_testresult b,ec_testcase c where b.testplan_testcase_link_id = a.id and a.testcase_id = c.id and b.latest = 1 and a.testplan_id = :testplanId  and a.testcase_id = :testcaseId", nativeQuery = true)
    public EcTestresult findLatestTestResultsByTestplanIdAndTestcaseId(@Param("testplanId") Long testplanId, @Param("testcaseId") Long testcaseId);

    @Query(value = "SELECT b.* FROM ec_testplan_testcase_mapping a, ec_testresult b,ec_testcase c where b.testplan_testcase_link_id = a.id and a.testcase_id = c.id and a.testplan_id = :testplanId  and a.testcase_id = :testcaseId order by `timestamp` desc", nativeQuery = true)
    public List<EcTestresult> findAllTestResultsByTestplanIdAndTestcaseId(@Param("testplanId") Long testplanId, @Param("testcaseId") Long testcaseId);
    
    @Query(value = "SELECT a.* FROM ec_testresult a, ec_testplan_testcase_mapping b  where a.testplan_testcase_link_id = b.id and b.testplan_id = :testplanId order by `timestamp` desc", nativeQuery = true)
    public List<EcTestresult> findAllTestResultsByTestplanId(@Param("testplanId") Long testplanId);
}

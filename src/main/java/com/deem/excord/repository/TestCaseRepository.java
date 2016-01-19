package com.deem.excord.repository;

import com.deem.excord.domain.EcTestcase;
import com.deem.excord.domain.EcTestfolder;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TestCaseRepository extends CrudRepository<EcTestcase, Long> {

    public List<EcTestcase> findAllByFolderIdOrderByIdAsc(EcTestfolder folderId);

    @Query(value = "select a.* from ec_testcase a, ec_testplan_testcase_mapping b where a.id = b.testcase_id and b.testplan_id = :testPlanId", nativeQuery = true)
    public List<EcTestcase> findAllTestCasesByTestPlanId(@Param("testPlanId") Long testPlanId);

    public EcTestcase findByIdAndFolderId(Long id, EcTestfolder folder);

    @Query(value = "SELECT count(*) FROM ec_testcase where automated = true", nativeQuery = true)
    public Integer automationCnt();

    @Query(value = "SELECT count(*) FROM ec_testcase where enabled = 1", nativeQuery = true)
    public Integer getCountOfActiveTestcases();

}

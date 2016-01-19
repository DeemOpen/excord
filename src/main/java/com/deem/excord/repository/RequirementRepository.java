package com.deem.excord.repository;

import com.deem.excord.domain.EcRequirement;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RequirementRepository extends CrudRepository<EcRequirement, Long> {

    public List<EcRequirement> findAllByParentIdOrderByNameAsc(EcRequirement parentId);

    @Query(value = "select case when count(*) > 0 then 'true' else 'false' end from ec_requirement where parent_id = :reqId", nativeQuery = true)
    public Boolean checkIfHasChildren(@Param("reqId") Long reqId);

    @Query(value = "SELECT a.* FROM ec_requirement a LEFT OUTER JOIN ec_testcase_requirement_mapping  b ON a.id=b.requirement_id where a.status = 'ACTIVE' and a.coverage = 1  and b.requirement_id is null", nativeQuery = true)
    public List<EcRequirement> findAllMissingCoverage();

    public EcRequirement findByParentIdIsNull();

    @Query(value = "SELECT priority,count(*) as cnt FROM ec_requirement where status='ACTIVE' and coverage = 1 group by priority", nativeQuery = true)
    public List<Object[]> getRequirementByPriorityCnt();

    @Query(value = "SELECT status,count(*) as cnt FROM ec_requirement where coverage = 1 group by status", nativeQuery = true)
    public List<Object[]> getRequirementByStatusCnt();

    @Query(value = "SELECT `status`,count(*) FROM ec_testresult where latest = 1 and DATE_FORMAT(`timestamp`,'%m-%Y') = DATE_FORMAT(NOW(),'%m-%Y') group by `status`", nativeQuery = true)
    public List<Object[]> getCurrentMonthRunStatus();

    @Query(value = "SELECT case_type,count(*) FROM ec_testcase group by case_type", nativeQuery = true)
    public List<Object[]> getTestcaseTypeCnt();

    @Query(value = "SELECT 'AUTOMATION',count(*) FROM ec_testresult where TESTER = 'AUTOMATION' and latest = 1 and DATE_FORMAT(`timestamp`,'%m-%Y') = DATE_FORMAT(NOW(),'%m-%Y') UNION ALL SELECT 'MANUAL',count(*) FROM ec_testresult where TESTER != 'AUTOMATION' and latest = 1 and DATE_FORMAT(`timestamp`,'%m-%Y') = DATE_FORMAT(NOW(),'%m-%Y')", nativeQuery = true)
    public List<Object[]> getCurrentMonthTestRunTypeCnt();

    public EcRequirement findByIdAndParentId(Long id, EcRequirement parentRequirement);

    @Query(value = "SELECT count(*) FROM ec_requirement where STATUS = 'ACTIVE' and coverage = 1", nativeQuery = true)
    public Integer getCountOfActiveRequirements();

}

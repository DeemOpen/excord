package com.deem.excord.repository;

import com.deem.excord.domain.EcTestplan;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TestPlanRepository extends CrudRepository<EcTestplan, Long> {

    //All testplans
    public List<EcTestplan> findAllByOrderByIdDesc();

    //All enabled testplans.
    public List<EcTestplan> findByEnabledOrderByIdDesc(Boolean enabled);

    @Query(value = "select testfolder_id,testcase_folder,test_status ,count(*) from (select c.id testplan_testcase_link_id,a.id testcase_id,a.name testcase_name,b.id testfolder_id,b.name testcase_folder,d.status test_status,d.latest from ec_testcase a,ec_testfolder b,ec_testplan_testcase_mapping c LEFT JOIN ec_testresult d ON d.testplan_testcase_link_id = c.id where a.folder_id = b.id and a.id = c.testcase_id  and c.testplan_id = :testplanId) e where e.latest is null or e.latest = 1 group by testcase_folder,test_status", nativeQuery = true)
    public List<Object[]> findMetricsByTestplanId(@Param("testplanId") Long testplanId);

    @Query(value = "select b.testplan_id,a.`status`,count(*) as count from ec_testresult a ,ec_testplan_testcase_mapping b where a.testplan_testcase_link_id = b.id and a.latest =1 and a.`status` = 'NOT_RUN' group by a.`status`,b.testplan_id  UNION ALL select b.testplan_id,a.`status`,count(*) as count from ec_testresult a ,ec_testplan_testcase_mapping b where a.testplan_testcase_link_id = b.id and a.latest =1 and a.`status` = 'PASSED' group by a.`status`,b.testplan_id  UNION ALL select testplan_id,'TOTAL', count(*) count from ec_testplan_testcase_mapping  group by testplan_id UNION ALL  select b.testplan_id,'RUN',count(*) as count from ec_testresult a ,ec_testplan_testcase_mapping b where a.testplan_testcase_link_id = b.id and a.latest =1 and a.status != 'NOT_RUN' group by b.testplan_id", nativeQuery = true)
    public List<Object[]> findMetrics();

    @Query(value = "select status,DATE_FORMAT(`timestamp`,'%m-%Y') as dt,count(*) as cnt from ec_testplan_testcase_mapping b, ec_testresult c where c.testplan_testcase_link_id = b.id and b.testplan_id = :testplanId group by status, dt order by dt asc", nativeQuery = true)
    public List<Object[]> findRunMetricByTestPlanId(@Param("testplanId") Long testplanId);

    @Query(value = "select priority,assigned_to,status,SUM(cnt) from (SELECT b.priority,a.assigned_to,'NOT_RUN' as `status`,count(*) as cnt FROM  ec_testcase b, ec_testplan_testcase_mapping a LEFT JOIN ec_testresult c ON a.id = c.testplan_testcase_link_id where a.testplan_id = :testplanId  and  a.testcase_id = b.id and c.status is null group by a.assigned_to,b.priority UNION ALL SELECT b.priority,a.assigned_to,c.`status`,count(*) as cnt FROM  ec_testcase b, ec_testplan_testcase_mapping a,ec_testresult c where a.id = c.testplan_testcase_link_id and a.testplan_id = :testplanId  and  a.testcase_id = b.id and c.latest = 1 group by a.assigned_to,b.priority,c.status ) as tab1 group by assigned_to,priority,status order by assigned_to,priority,status", nativeQuery = true)
    public List<Object[]> findByPriorityByTester(@Param("testplanId") Long testplanId);

    @Query(value = "SELECT count(*) FROM ec_testplan where enabled = 1", nativeQuery = true)
    public Integer getCountOfActiveTestplan();

}

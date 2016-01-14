package com.deem.excord.repository;

import com.deem.excord.domain.EcRequirement;
import com.deem.excord.domain.EcTestcase;
import com.deem.excord.domain.EcTestcaseRequirementMapping;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TestcaseRequirementRepository extends CrudRepository<EcTestcaseRequirementMapping, Long> {

    public EcTestcaseRequirementMapping findByTestcaseIdAndRequirementId(EcTestcase tc, EcRequirement req);

    @Query(value = "select c.* from ec_testcase a,ec_requirement b,ec_testcase_requirement_mapping c where a.id = c.testcase_id and b.id = c.requirement_id and a.folder_id = :folderId", nativeQuery = true)
    public List<EcTestcaseRequirementMapping> findAllByTestFolderId(@Param("folderId") Long folderId);

    public List<EcTestcaseRequirementMapping> findAllByRequirementId(EcRequirement req);
}
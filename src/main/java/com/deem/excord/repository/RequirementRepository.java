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
    
}
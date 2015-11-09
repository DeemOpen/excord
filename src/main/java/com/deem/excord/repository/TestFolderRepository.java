package com.deem.excord.repository;

import com.deem.excord.domain.EcTestfolder;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TestFolderRepository extends CrudRepository<EcTestfolder, Long> {

    public EcTestfolder findByParentIdIsNull();

    public List<EcTestfolder> findAllByParentIdOrderByNameAsc(EcTestfolder parentId);

    @Query(value = "SELECT case when count(*) > 0 then 'true' else 'false' end FROM ec_testfolder a,ec_testfolder b  where a.parent_id = :nodeId and a.parent_id = b.id and b.parent_id is not null", nativeQuery = true)
    public Boolean checkIfHasChildren(@Param("nodeId") Long nodeId);
}

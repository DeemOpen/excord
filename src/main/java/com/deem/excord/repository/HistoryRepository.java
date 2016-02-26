package com.deem.excord.repository;

import com.deem.excord.domain.EcHistory;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface HistoryRepository extends CrudRepository<EcHistory, Long> {

    @Query(value = "select * from ec_history order by id desc limit 50", nativeQuery = true)
    public List<EcHistory> findTopChanges();

    public List<EcHistory> findByChangeSummaryContainingOrSlugIs(String summaryKey, String slug);
}

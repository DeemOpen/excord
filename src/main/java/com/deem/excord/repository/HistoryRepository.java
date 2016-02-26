package com.deem.excord.repository;

import com.deem.excord.domain.EcHistory;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface HistoryRepository extends CrudRepository<EcHistory, Long> {

    public List<EcHistory> findTop50ByOrderByIdDesc();

    public List<EcHistory> findByChangeSummaryContainingOrSlugIs(String summaryKey, String slug);
}

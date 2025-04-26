package com.allomed.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.allomed.app.domain.DoctorViewHistory;
import com.allomed.app.repository.DoctorViewHistoryRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link DoctorViewHistory} entity.
 */
public interface DoctorViewHistorySearchRepository
    extends ElasticsearchRepository<DoctorViewHistory, Long>, DoctorViewHistorySearchRepositoryInternal {}

interface DoctorViewHistorySearchRepositoryInternal {
    Page<DoctorViewHistory> search(String query, Pageable pageable);

    Page<DoctorViewHistory> search(Query query);

    @Async
    void index(DoctorViewHistory entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DoctorViewHistorySearchRepositoryInternalImpl implements DoctorViewHistorySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DoctorViewHistoryRepository repository;

    DoctorViewHistorySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DoctorViewHistoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<DoctorViewHistory> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<DoctorViewHistory> search(Query query) {
        SearchHits<DoctorViewHistory> searchHits = elasticsearchTemplate.search(query, DoctorViewHistory.class);
        List<DoctorViewHistory> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(DoctorViewHistory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), DoctorViewHistory.class);
    }
}

package com.allomed.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.allomed.app.domain.SymptomSearch;
import com.allomed.app.repository.SymptomSearchRepository;
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
 * Spring Data Elasticsearch repository for the {@link SymptomSearch} entity.
 */
public interface SymptomSearchSearchRepository
    extends ElasticsearchRepository<SymptomSearch, Long>, SymptomSearchSearchRepositoryInternal {}

interface SymptomSearchSearchRepositoryInternal {
    Page<SymptomSearch> search(String query, Pageable pageable);

    Page<SymptomSearch> search(Query query);

    @Async
    void index(SymptomSearch entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SymptomSearchSearchRepositoryInternalImpl implements SymptomSearchSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SymptomSearchRepository repository;

    SymptomSearchSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SymptomSearchRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<SymptomSearch> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<SymptomSearch> search(Query query) {
        SearchHits<SymptomSearch> searchHits = elasticsearchTemplate.search(query, SymptomSearch.class);
        List<SymptomSearch> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(SymptomSearch entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SymptomSearch.class);
    }
}

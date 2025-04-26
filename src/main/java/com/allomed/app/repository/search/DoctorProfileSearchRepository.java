package com.allomed.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.repository.DoctorProfileRepository;
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
 * Spring Data Elasticsearch repository for the {@link DoctorProfile} entity.
 */
public interface DoctorProfileSearchRepository
    extends ElasticsearchRepository<DoctorProfile, String>, DoctorProfileSearchRepositoryInternal {}

interface DoctorProfileSearchRepositoryInternal {
    Page<DoctorProfile> search(String query, Pageable pageable);

    Page<DoctorProfile> search(Query query);

    @Async
    void index(DoctorProfile entity);

    @Async
    void deleteFromIndexById(String id);
}

class DoctorProfileSearchRepositoryInternalImpl implements DoctorProfileSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DoctorProfileRepository repository;

    DoctorProfileSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DoctorProfileRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<DoctorProfile> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<DoctorProfile> search(Query query) {
        SearchHits<DoctorProfile> searchHits = elasticsearchTemplate.search(query, DoctorProfile.class);
        List<DoctorProfile> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(DoctorProfile entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), DoctorProfile.class);
    }
}

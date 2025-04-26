package com.allomed.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.allomed.app.domain.DoctorDocument;
import com.allomed.app.repository.DoctorDocumentRepository;
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
 * Spring Data Elasticsearch repository for the {@link DoctorDocument} entity.
 */
public interface DoctorDocumentSearchRepository
    extends ElasticsearchRepository<DoctorDocument, Long>, DoctorDocumentSearchRepositoryInternal {}

interface DoctorDocumentSearchRepositoryInternal {
    Page<DoctorDocument> search(String query, Pageable pageable);

    Page<DoctorDocument> search(Query query);

    @Async
    void index(DoctorDocument entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DoctorDocumentSearchRepositoryInternalImpl implements DoctorDocumentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DoctorDocumentRepository repository;

    DoctorDocumentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DoctorDocumentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<DoctorDocument> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<DoctorDocument> search(Query query) {
        SearchHits<DoctorDocument> searchHits = elasticsearchTemplate.search(query, DoctorDocument.class);
        List<DoctorDocument> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(DoctorDocument entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), DoctorDocument.class);
    }
}

package com.allomed.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.allomed.app.domain.AppUserProfile;
import com.allomed.app.repository.AppUserProfileRepository;
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
 * Spring Data Elasticsearch repository for the {@link AppUserProfile} entity.
 */
public interface AppUserProfileSearchRepository
    extends ElasticsearchRepository<AppUserProfile, String>, AppUserProfileSearchRepositoryInternal {}

interface AppUserProfileSearchRepositoryInternal {
    Page<AppUserProfile> search(String query, Pageable pageable);

    Page<AppUserProfile> search(Query query);

    @Async
    void index(AppUserProfile entity);

    @Async
    void deleteFromIndexById(String id);
}

class AppUserProfileSearchRepositoryInternalImpl implements AppUserProfileSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AppUserProfileRepository repository;

    AppUserProfileSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AppUserProfileRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<AppUserProfile> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<AppUserProfile> search(Query query) {
        SearchHits<AppUserProfile> searchHits = elasticsearchTemplate.search(query, AppUserProfile.class);
        List<AppUserProfile> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(AppUserProfile entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), AppUserProfile.class);
    }
}

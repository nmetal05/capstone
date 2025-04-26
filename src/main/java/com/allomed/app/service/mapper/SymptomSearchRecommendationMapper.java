package com.allomed.app.service.mapper;

import com.allomed.app.domain.Specialization;
import com.allomed.app.domain.SymptomSearch;
import com.allomed.app.domain.SymptomSearchRecommendation;
import com.allomed.app.service.dto.SpecializationDTO;
import com.allomed.app.service.dto.SymptomSearchDTO;
import com.allomed.app.service.dto.SymptomSearchRecommendationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SymptomSearchRecommendation} and its DTO {@link SymptomSearchRecommendationDTO}.
 */
@Mapper(componentModel = "spring")
public interface SymptomSearchRecommendationMapper extends EntityMapper<SymptomSearchRecommendationDTO, SymptomSearchRecommendation> {
    @Mapping(target = "search", source = "search", qualifiedByName = "symptomSearchId")
    @Mapping(target = "specialization", source = "specialization", qualifiedByName = "specializationId")
    SymptomSearchRecommendationDTO toDto(SymptomSearchRecommendation s);

    @Named("symptomSearchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SymptomSearchDTO toDtoSymptomSearchId(SymptomSearch symptomSearch);

    @Named("specializationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SpecializationDTO toDtoSpecializationId(Specialization specialization);
}

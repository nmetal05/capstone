package com.allomed.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SpecializationCriteriaTest {

    @Test
    void newSpecializationCriteriaHasAllFiltersNullTest() {
        var specializationCriteria = new SpecializationCriteria();
        assertThat(specializationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void specializationCriteriaFluentMethodsCreatesFiltersTest() {
        var specializationCriteria = new SpecializationCriteria();

        setAllFilters(specializationCriteria);

        assertThat(specializationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void specializationCriteriaCopyCreatesNullFilterTest() {
        var specializationCriteria = new SpecializationCriteria();
        var copy = specializationCriteria.copy();

        assertThat(specializationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(specializationCriteria)
        );
    }

    @Test
    void specializationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var specializationCriteria = new SpecializationCriteria();
        setAllFilters(specializationCriteria);

        var copy = specializationCriteria.copy();

        assertThat(specializationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(specializationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var specializationCriteria = new SpecializationCriteria();

        assertThat(specializationCriteria).hasToString("SpecializationCriteria{}");
    }

    private static void setAllFilters(SpecializationCriteria specializationCriteria) {
        specializationCriteria.id();
        specializationCriteria.name();
        specializationCriteria.description();
        specializationCriteria.doctorProfilesId();
        specializationCriteria.distinct();
    }

    private static Condition<SpecializationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getDoctorProfilesId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SpecializationCriteria> copyFiltersAre(
        SpecializationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getDoctorProfilesId(), copy.getDoctorProfilesId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

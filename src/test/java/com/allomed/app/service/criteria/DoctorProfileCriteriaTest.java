package com.allomed.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DoctorProfileCriteriaTest {

    @Test
    void newDoctorProfileCriteriaHasAllFiltersNullTest() {
        var doctorProfileCriteria = new DoctorProfileCriteria();
        assertThat(doctorProfileCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void doctorProfileCriteriaFluentMethodsCreatesFiltersTest() {
        var doctorProfileCriteria = new DoctorProfileCriteria();

        setAllFilters(doctorProfileCriteria);

        assertThat(doctorProfileCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void doctorProfileCriteriaCopyCreatesNullFilterTest() {
        var doctorProfileCriteria = new DoctorProfileCriteria();
        var copy = doctorProfileCriteria.copy();

        assertThat(doctorProfileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(doctorProfileCriteria)
        );
    }

    @Test
    void doctorProfileCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var doctorProfileCriteria = new DoctorProfileCriteria();
        setAllFilters(doctorProfileCriteria);

        var copy = doctorProfileCriteria.copy();

        assertThat(doctorProfileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(doctorProfileCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var doctorProfileCriteria = new DoctorProfileCriteria();

        assertThat(doctorProfileCriteria).hasToString("DoctorProfileCriteria{}");
    }

    private static void setAllFilters(DoctorProfileCriteria doctorProfileCriteria) {
        doctorProfileCriteria.id();
        doctorProfileCriteria.phoneNumber();
        doctorProfileCriteria.officeAddress();
        doctorProfileCriteria.latitude();
        doctorProfileCriteria.longitude();
        doctorProfileCriteria.inpeCode();
        doctorProfileCriteria.isVerified();
        doctorProfileCriteria.lastLoginIp();
        doctorProfileCriteria.lastUserAgent();
        doctorProfileCriteria.lastLoginDate();
        doctorProfileCriteria.internalUserId();
        doctorProfileCriteria.specializationsId();
        doctorProfileCriteria.distinct();
    }

    private static Condition<DoctorProfileCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getOfficeAddress()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getInpeCode()) &&
                condition.apply(criteria.getIsVerified()) &&
                condition.apply(criteria.getLastLoginIp()) &&
                condition.apply(criteria.getLastUserAgent()) &&
                condition.apply(criteria.getLastLoginDate()) &&
                condition.apply(criteria.getInternalUserId()) &&
                condition.apply(criteria.getSpecializationsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DoctorProfileCriteria> copyFiltersAre(
        DoctorProfileCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getOfficeAddress(), copy.getOfficeAddress()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getInpeCode(), copy.getInpeCode()) &&
                condition.apply(criteria.getIsVerified(), copy.getIsVerified()) &&
                condition.apply(criteria.getLastLoginIp(), copy.getLastLoginIp()) &&
                condition.apply(criteria.getLastUserAgent(), copy.getLastUserAgent()) &&
                condition.apply(criteria.getLastLoginDate(), copy.getLastLoginDate()) &&
                condition.apply(criteria.getInternalUserId(), copy.getInternalUserId()) &&
                condition.apply(criteria.getSpecializationsId(), copy.getSpecializationsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

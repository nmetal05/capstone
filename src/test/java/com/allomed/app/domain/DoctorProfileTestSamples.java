package com.allomed.app.domain;

import java.util.UUID;

public class DoctorProfileTestSamples {

    public static DoctorProfile getDoctorProfileSample1() {
        return new DoctorProfile()
            .id("id1")
            .phoneNumber("phoneNumber1")
            .officeAddress("officeAddress1")
            .inpeCode("inpeCode1")
            .lastLoginIp("lastLoginIp1")
            .lastUserAgent("lastUserAgent1");
    }

    public static DoctorProfile getDoctorProfileSample2() {
        return new DoctorProfile()
            .id("id2")
            .phoneNumber("phoneNumber2")
            .officeAddress("officeAddress2")
            .inpeCode("inpeCode2")
            .lastLoginIp("lastLoginIp2")
            .lastUserAgent("lastUserAgent2");
    }

    public static DoctorProfile getDoctorProfileRandomSampleGenerator() {
        return new DoctorProfile()
            .id(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .officeAddress(UUID.randomUUID().toString())
            .inpeCode(UUID.randomUUID().toString())
            .lastLoginIp(UUID.randomUUID().toString())
            .lastUserAgent(UUID.randomUUID().toString());
    }
}

package com.allomed.app.service.google.dto;

import java.util.List;
import java.util.Map;

/** Single element from Routes distanceMatrix/v2 response. */
public record RouteMatrixElement(
    Integer originIndex,
    Integer destinationIndex,
    Integer distanceMeters,
    String duration,
    Status status,
    String condition
) {
    public static record Status(Integer code, String message, List<Map<String, Object>> details) {}
}

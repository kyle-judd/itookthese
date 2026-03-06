package io.itookthese.api.dto;

import java.util.List;

public record PhotoReorderRequest(List<Long> photoIds) {}

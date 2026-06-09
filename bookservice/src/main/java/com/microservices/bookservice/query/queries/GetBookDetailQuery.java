package com.microservices.bookservice.query.queries;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetBookDetailQuery {
    private String id;
}

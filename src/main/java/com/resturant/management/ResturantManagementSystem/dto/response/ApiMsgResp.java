package com.resturant.management.ResturantManagementSystem.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Include non-null fields only
public class ApiMsgResp<T> {
    private T payload;
    private int statusCode;
    private String message;

    public ApiMsgResp(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ApiMsgResp(T payload, int statusCode, String message) {
        this.payload = payload;
        this.statusCode = statusCode;
        this.message = message;
    }
}

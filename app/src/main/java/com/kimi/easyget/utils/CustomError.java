package com.kimi.easyget.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomError {
    private Boolean error;
    private String message;
}

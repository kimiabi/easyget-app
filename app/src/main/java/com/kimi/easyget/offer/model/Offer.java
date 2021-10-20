package com.kimi.easyget.offer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class Offer {
    private String id;
    private String name;
    private String description;

}

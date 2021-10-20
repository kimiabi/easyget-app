package com.kimi.easyget.products.models;

import com.google.gson.Gson;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.NONE;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product implements Serializable {
    private String id;
    private String name;
    private String description;
    private String photo_url;
    private String price;

    @Getter(NONE)
    Boolean enabled;

    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

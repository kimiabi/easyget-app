package com.kimi.easyget.categories.models;

import com.google.gson.Gson;

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
public class Category {
    private String id;
    private String name;
    private String description;

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

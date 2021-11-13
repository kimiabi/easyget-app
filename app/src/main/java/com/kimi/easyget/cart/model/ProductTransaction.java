package com.kimi.easyget.cart.model;

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
public class ProductTransaction implements Serializable {
    private String productId;
    private String name;
    private String description;
    private String photoUrl;
    private String price;
    private String totalPrice;
    private String totalQuantity;
    private Boolean offer;

    @Getter(NONE)
    private Boolean enabled;

    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

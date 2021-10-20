package com.kimi.easyget.cart.model;

import com.google.firebase.firestore.FieldValue;
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
public class ProductTransaction {
    private String productId;
    private String name;
    private String description;
    private String photoUrl;
    private String price;
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

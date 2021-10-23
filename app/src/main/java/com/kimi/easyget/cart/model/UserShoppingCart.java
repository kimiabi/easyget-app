package com.kimi.easyget.cart.model;

import com.google.firebase.firestore.FieldValue;
import com.kimi.easyget.user.models.User;

import java.io.Serializable;
import java.util.List;

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
public class UserShoppingCart implements Serializable {
    private List<ProductTransaction> products;
    private User user;
    String createdBy;

    @Getter(NONE)
    Boolean enabled;

    public Boolean isEnabled() {
        return enabled;
    }

}

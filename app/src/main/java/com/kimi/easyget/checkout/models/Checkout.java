package com.kimi.easyget.checkout.models;

import com.google.firebase.firestore.FieldValue;
import com.kimi.easyget.cart.model.ProductTransaction;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Checkout implements Serializable {
    private double totalAmount;
    private double subTotalAmount;
    private double saving;
    private String userId;
    private String paymentType;
    private String deliveryAddress;
    private List<ProductTransaction> products;
    private FieldValue createdAt;
    private String shoppingCartId;
}

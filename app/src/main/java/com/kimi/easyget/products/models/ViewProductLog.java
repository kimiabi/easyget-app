package com.kimi.easyget.products.models;

import com.google.firebase.firestore.FieldValue;

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
public class ViewProductLog {
    private String categoryId;
    private FieldValue createdAt;
    private String productId;
    private String userId;
    private String os;
    private String device;
}

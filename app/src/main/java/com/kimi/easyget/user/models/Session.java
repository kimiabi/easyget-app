package com.kimi.easyget.user.models;

import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;

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
public class Session implements Serializable {
    String userId;
    FieldValue registration;
    String OS;
    String device;
}

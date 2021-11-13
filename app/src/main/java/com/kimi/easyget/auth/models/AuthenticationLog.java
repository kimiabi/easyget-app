package com.kimi.easyget.auth.models;

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
public class AuthenticationLog {
    private FieldValue registration;
    private String device;
    private String os;
    private String ip;
    private String provider;
    private String type;
    private String userId;
}

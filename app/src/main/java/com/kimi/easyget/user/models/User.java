package com.kimi.easyget.user.models;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
    String uid;
    String firstName;
    String lastName;
    String displayName;
    String phoneNumber;
    String photoUser;
    String address;
    String authProvider;
    String datOfBirth;
    String email;
    String gender;
    FieldValue createdAt;
    String createdBy;
    String updatedAt;
    String updatedBy;

    @Getter(NONE)
    Boolean enabled;

    public Boolean isEnabled() {
        return enabled;
    }

}

package com.kimi.easyget.search.models;

import com.google.firebase.firestore.FieldValue;

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
public class Search implements Serializable {
    String userId;
    FieldValue registration;
    String key;
    String select;
    List<SearchResult> results;
}

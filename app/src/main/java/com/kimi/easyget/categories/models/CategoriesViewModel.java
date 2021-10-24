package com.kimi.easyget.categories.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CategoriesViewModel extends ViewModel {
    private final MutableLiveData<Category> selectedCategory = new MutableLiveData<>();

    public void selectCategory(Category category) {
        selectedCategory.setValue(category);
    }

    public LiveData<Category> getSelectedCategory() {
        return selectedCategory;
    }
}


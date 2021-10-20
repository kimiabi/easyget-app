package com.kimi.easyget.products.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kimi.easyget.cart.model.ProductTransaction;

public class ProductTransactionViewModel extends ViewModel {
    private final MutableLiveData<ProductTransaction> selectedProduct = new MutableLiveData<ProductTransaction>();

    public void selectProduct(ProductTransaction productTransaction) {
        selectedProduct.setValue(productTransaction);
    }

    public LiveData<ProductTransaction> getSelectedProduct() {
        return selectedProduct;
    }
}

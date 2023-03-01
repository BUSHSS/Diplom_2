package api.model;

import api.ingredient.DataIngredient;

import java.util.List;


public class JsonIngredients {
    private Boolean success;
    private List<DataIngredient> data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<DataIngredient> getData() {
        return data;
    }

    public void setData(List<DataIngredient> data) {
        this.data = data;
    }


}

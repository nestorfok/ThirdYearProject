package com.example.sushiroo;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.ArrayList;
import java.util.List;

public class OWLEntity {

    private OWLNamedIndividual namedIndividual;
    private String iriShortForm;
    private float calorie;
    private float price;
    private int order;
    private String sushiName;
    private String ingredients;

    public OWLEntity() {

    }

    public OWLEntity(OWLNamedIndividual namedIndividual, String iriShortForm, String sushiName, float calorie, float price, int order, String ingredients) {
        this.namedIndividual = namedIndividual;
        this.iriShortForm = iriShortForm;
        this.sushiName = sushiName;
        this.calorie = calorie;
        this.price = price;
        this.order = order;
        this.ingredients = ingredients;
    }

    public OWLNamedIndividual getNamedIndividual() {
        return namedIndividual;
    }

    public String getIriShortForm() {
        return iriShortForm;
    }

    public String getSushiName() {
        return sushiName;
    }

    public float getCalorie() {
        return calorie;
    }

    public float getPrice() {
        return price;
    }

    public int getOrder() {
        return order;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setNamedIndividual(OWLNamedIndividual namedIndividual) {
        this.namedIndividual = namedIndividual;
    }

    public void setIriShortForm(String iriShortForm) {
        this.iriShortForm = iriShortForm;
    }

    public void setSushiName(String sushiName) {
        this.sushiName = sushiName;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}

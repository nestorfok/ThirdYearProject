package com.example.sushiroo;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class OWLEntity {

    private OWLNamedIndividual namedIndividual;
    private IRI iri;
    private float calorie;
    private float price;
    private String sushiName;

    public OWLEntity() {

    }

    public OWLEntity(OWLNamedIndividual namedIndividual, IRI iri, String sushiName, float calorie, float price) {
        this.namedIndividual = namedIndividual;
        this.iri = iri;
        this.sushiName = sushiName;
        this.calorie = calorie;
        this.price = price;
    }

    public OWLNamedIndividual getNamedIndividual() {
        return namedIndividual;
    }

    public IRI getIri() {
        return iri;
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

    public void setNamedIndividual(OWLNamedIndividual namedIndividual) {
        this.namedIndividual = namedIndividual;
    }

    public void setIri(IRI iri) {
        this.iri = iri;
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
}

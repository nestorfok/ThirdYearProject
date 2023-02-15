package com.example.sushiroo.service;

import com.example.sushiroo.model.OWLEntity;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class OWLService {

    OWLOntologyManager man = OWLManager.createOWLOntologyManager();
    OWLDataFactory df = man.getOWLDataFactory();
    OWLReasonerFactory rf = new ReasonerFactory();
    private OWLOntology o;
    private OWLReasoner r;
    private OWLAnnotationProperty labelProperty;
    private List<OWLEntity> allSushi;
    private List<OWLEntity> currentSushiList;
    private List<OWLEntity> currentSushiListAfterFilter;
    private List<String> currentFilterList;

    public OWLService() throws OWLOntologyCreationException {
        File file = new File("src/main/resources/static/Sushi.owl");
        this.o = man.loadOntologyFromOntologyDocument(file);
        this.r = rf.createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        this.labelProperty = df.getRDFSLabel();
        this.currentSushiListAfterFilter = new ArrayList<>();
        //this.currentFilterList = new ArrayList<>();
    }

    /**
     * Get RDFSLabel from an iri
     * @param iri
     * @return RDFSLabel of the iri
     */
    public String getRDFSLabelFromIRI (IRI iri) {

        Iterator<OWLAnnotationAssertionAxiom> iterator = o.annotationAssertionAxioms(iri).iterator();

        while (iterator.hasNext()) {
            OWLAnnotationAssertionAxiom axiom = iterator.next();
            if (axiom.getProperty().equals(labelProperty)) {
                OWLAnnotationValue value = axiom.getValue();
                if (value instanceof OWLLiteral) {
                    return ((OWLLiteral) value).getLiteral();
                }
            }
        }
        return null;
    }

    /**
     * Get data property of an individual
     * @param namedIndividual individual that we want to get the data property
     * @param iri data property iri that we want to get from the individual;
     * @return List<OWLLiteral>
     */
    public List<OWLLiteral> getDataPropertyFromIndividual (OWLNamedIndividual namedIndividual, String iri) {
        Set<OWLLiteral> literals = r.getDataPropertyValues(namedIndividual, df.getOWLDataProperty(iri));
        return new ArrayList<>(literals);
    }

    public String getIngredientsFromSushiIndividual (OWLNamedIndividual namedIndividual, OWLObjectProperty prop) {
        NodeSet<OWLNamedIndividual> owlLiterals = r.getObjectPropertyValues(namedIndividual, prop);
        String combined = "";
        for (Node<OWLNamedIndividual> node : owlLiterals) {
            for (OWLNamedIndividual ingre : node) {
                String shorty = ingre.getIRI().getShortForm();
                String shortyRe = shorty.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
                if (combined != "") {
                    combined = combined + ", " + shortyRe;
                } else {
                    combined = shortyRe;
                }
            }
        }
        return combined;
    }

    /** Get all sushi in OWL from a type
     * @param type The type of sushi (E.g. VegetarianSushi)
     * @return List<OWLEntity> of all sushi with that type
     */
    public List<OWLEntity> getAllSushiInOWLFromType (String type) {
        List<OWLEntity> owlEntities = new ArrayList<>();
        String irl = "http://www.sushiro.com/ontologies/sushiro.owl#" + type;
        OWLClass sushiClass = df.getOWLClass(irl);
        NodeSet<OWLNamedIndividual> individuals = r.getInstances(sushiClass, false);
        OWLObjectProperty propHasIngredient = df.getOWLObjectProperty("http://www.sushiro.com/ontologies/sushiro.owl#hasIngredient");
        for (Node<OWLNamedIndividual> node : individuals) {
            for (OWLNamedIndividual sushiIndividual : node) {
                IRI sushiIri = sushiIndividual.getIRI();
                String rdfsSushiName = getRDFSLabelFromIRI(sushiIri);

                float calories = getDataPropertyFromIndividual(sushiIndividual,
                        "http://www.sushiro.com/ontologies/sushiro.owl#hasCalories").get(0).parseFloat();

                float price = getDataPropertyFromIndividual(sushiIndividual,
                        "http://www.sushiro.com/ontologies/sushiro.owl#hasPrice").get(0).parseFloat();

                String ingredients = getIngredientsFromSushiIndividual(sushiIndividual, propHasIngredient);

                OWLEntity owlEntity = new OWLEntity(sushiIndividual, sushiIri.getShortForm(), rdfsSushiName, calories, price, 0, ingredients);
                owlEntities.add(owlEntity);
            }
        }
        return owlEntities;
    }

    /** Use for reloading the homepage */
    public List<OWLEntity> getAllSushi () {
        if (this.allSushi == null) {
            this.currentSushiList = getAllSushiInOWLFromType("Sushi");
            this.allSushi = currentSushiList;
        }
        return allSushi;
    }

    /**
     * Use for the search bar
     * @param name The value of the search bar
     * @return List<OWLEntity> List of sushi that contains the value
     */
    public List<OWLEntity> searchSushiFromName (String name) {
        currentSushiList= new ArrayList<>();
        resetCurrentSushiListAfterFilter();
        name = name.toLowerCase();
        for (OWLEntity owlEntity : allSushi) {
            String sushiName = owlEntity.getSushiName().toLowerCase();
            if (sushiName.contains(name)) {
                currentSushiList.add(owlEntity);
            }
        }
        return currentSushiList;
    }

    /**
     * Use for button
     * @param type Sushi type of the button that user press
     * @return List<OWLEntity> List of sushi of that type
     */
    public List<OWLEntity> getAllSushiFromType (String type) {
        currentSushiList = new ArrayList<>();
        resetCurrentSushiListAfterFilter();
        String iri = "http://www.sushiro.com/ontologies/sushiro.owl#" + type;
        OWLClass sushiClass = df.getOWLClass(iri);
        for (OWLEntity e : allSushi) {
            boolean isSubclass = r.isEntailed(df.getOWLClassAssertionAxiom(sushiClass, e.getNamedIndividual()));
            if (isSubclass) {
                currentSushiList.add(e);
            }
        }
        return currentSushiList;
    }

    /**
     * Use for allergen filter
     * @param filterList List of allergens of user
     * @return List<OWLEntity> List of sushi after filtering the allergen
     */
    public List<OWLEntity> sushiFilter(String[] filterList) {
        //List<OWLEntity> currentResultByFilter = new ArrayList<>();
        resetCurrentSushiListAfterFilter();
        this.currentFilterList = Arrays.asList(filterList);
        for (OWLEntity e : currentSushiList) {
            int i = 0;
            for (String s : filterList) {
                String iri = "http://www.sushiro.com/ontologies/sushiro.owl#" + s;
                OWLClass sushiClass = df.getOWLClass(iri);
                boolean isSubclass = r.isEntailed(df.getOWLClassAssertionAxiom(sushiClass, e.getNamedIndividual()));
                if (isSubclass) {
                    break;
                } else {
                    i += 1;
                }
            }
            if (i == filterList.length) {
                currentSushiListAfterFilter.add(e);
            }
        }

        return currentSushiListAfterFilter;
    }


    public List<OWLEntity> getCurrentSushiList() {
        return currentSushiList;
    }

    public List<String> getCurrentFilterList() {
        return currentFilterList;
    }

    public List<String> resetCurrentFilterList() {
        currentFilterList = new ArrayList<>();
        return currentFilterList;
    }

    public List<OWLEntity> getCurrentSushiListAfterFilter() {
        return currentSushiListAfterFilter;
    }

    public void resetCurrentSushiListAfterFilter() {
        currentSushiListAfterFilter = new ArrayList<>();
    }

    public void changeOrderNumber (boolean bool, String sushiName){
        for (OWLEntity e : allSushi) {
            if (e.getSushiName().equals(sushiName)) {
                int i = e.getOrder();
                //System.out.println(e.getOrder());
                if (bool) {
                    e.setOrder(e.getOrder() + 1);
                } else if (i < 1) {
                    e.setOrder(0);
                } else {
                    e.setOrder(e.getOrder() - 1);
                }
                //System.out.println(e.getOrder());
                break;
            }
        }

    }

    public OWLEntity getSushiDetail (String sushiName) {
        for (OWLEntity e : allSushi) {
            if (e.getIriShortForm().equals(sushiName)) {
                //System.out.println("match");
                return e;
            }
        }
        return null;
    }

    public List<OWLEntity> getCurrentOrder () {
        List<OWLEntity> currentOrderList = new ArrayList<>();
        for (OWLEntity e: allSushi) {
            if (e.getOrder() > 0) {
                currentOrderList.add(e);
            }
        }
        return  currentOrderList;
    }




}

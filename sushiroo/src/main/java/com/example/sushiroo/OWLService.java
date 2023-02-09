package com.example.sushiroo;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OWLService {

    OWLOntologyManager man = OWLManager.createOWLOntologyManager();
    OWLDataFactory df = man.getOWLDataFactory();
    OWLReasonerFactory rf = new ReasonerFactory();
    private OWLOntology o;
    private OWLReasoner r;
    private OWLAnnotationProperty labelProperty;
    private List<OWLEntity> allSushi;

    public OWLService() throws OWLOntologyCreationException {
        File file = new File("src/main/resources/static/Sushi.owl");
        this.o = man.loadOntologyFromOntologyDocument(file);
        this.r = rf.createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        this.labelProperty = df.getRDFSLabel();
    }

    public List<String> getSushiFromType (String type) {

        List<String> sushi = new ArrayList<>();
        String irl = "http://www.sushiro.com/ontologies/sushiro.owl#" + type;
        r.getSubClasses(df.getOWLClass(irl),
                true).forEach(a -> {
            List<String> remainders = a.entities()
                    .map(entity -> entity.getIRI().getRemainder())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            sushi.addAll(remainders);
        });


        for (int i = 0; i < sushi.size(); i++) {
            String item = sushi.get(i);
            StringBuilder modifiedItem = new StringBuilder();
            modifiedItem.append(item.charAt(0));
            for (int j = 1; j < item.length(); j++) {
                if (Character.isUpperCase(item.charAt(j))) {
                    modifiedItem.append(" ");
                }
                modifiedItem.append(item.charAt(j));
            }
            sushi.set(i, modifiedItem.toString());
        }

        return sushi;
    }

    public List<OWLEntity> searchSushiFromName (String name) {
        List<OWLEntity> searchSushi = new ArrayList<>();
        name = name.toLowerCase();
        for (OWLEntity owlEntity : allSushi) {
            String sushiName = owlEntity.getSushiName().toLowerCase();
            if (sushiName.contains(name)) {
                searchSushi.add(owlEntity);
            }
        }
        return searchSushi;
    }

    /**
     * Get RDFSLabel from iri
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
     * Get float data property from individual
     */
    public List<OWLLiteral> getDataPropertyFromIndividual (OWLNamedIndividual namedIndividual, String iri) {
        Set<OWLLiteral> literals = r.getDataPropertyValues(namedIndividual, df.getOWLDataProperty(iri));
        return new ArrayList<>(literals);
    }

    public List<OWLEntity> getAllSushiFromType (String type) {
        List<OWLEntity> owlEntities = new ArrayList<>();
        String irl = "http://www.sushiro.com/ontologies/sushiro.owl#" + type;
        OWLClass sushiClass = df.getOWLClass(irl);
        NodeSet<OWLNamedIndividual> individuals = r.getInstances(sushiClass, false);
        for (Node<OWLNamedIndividual> node : individuals) {
            for (OWLNamedIndividual sushiIndividual : node) {
                IRI sushiIri = sushiIndividual.getIRI();
                String rdfsSushiName = getRDFSLabelFromIRI(sushiIri);

                float calories = getDataPropertyFromIndividual(sushiIndividual,
                        "http://www.sushiro.com/ontologies/sushiro.owl#hasCalories").get(0).parseFloat();

                float price = getDataPropertyFromIndividual(sushiIndividual,
                        "http://www.sushiro.com/ontologies/sushiro.owl#hasPrice").get(0).parseFloat();

                OWLEntity owlEntity = new OWLEntity(rdfsSushiName, calories, price);
                owlEntities.add(owlEntity);
            }
        }
        return owlEntities;
    }

    public List<OWLEntity> getAllSushi () {
        this.allSushi = getAllSushiFromType("Sushi");
        return allSushi;
    }
}

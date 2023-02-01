package com.example.sushiroo;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.springframework.context.annotation.Bean;
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

    public OWLService() throws OWLOntologyCreationException {
        //IRI pizzaontology = IRI.create("http://protege.stanford.edu/ontologies/pizza/pizza.owl");
        File file = new File("src/main/resources/static/Sushi.owl");
        //this.o = man.loadOntology(pizzaontology);
        this.o = man.loadOntologyFromOntologyDocument(file);
        this.r = rf.createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
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

    public List<String> getSushiFromName (String name) {
        List<String> sushi = new ArrayList<>();
        List<String> sushiByName = new ArrayList<>();

        String irl = "http://www.sushiro.com/ontologies/sushiro.owl#NamedSushi";
        r.getSubClasses(df.getOWLClass(irl),
                true).forEach(a -> {
            List<String> remainders = a.entities()
                    .map(entity -> entity.getIRI().getRemainder())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            sushi.addAll(remainders);
        });

        name = name.toLowerCase();
        for (int i = 0; i < sushi.size(); i++) {
            String item = sushi.get(i);
            if (item.toLowerCase().contains(name)) {
                StringBuilder modifiedItem = new StringBuilder();
                modifiedItem.append(item.charAt(0));
                for (int j = 1; j < item.length(); j++) {
                    if (Character.isUpperCase(item.charAt(j))) {
                        modifiedItem.append(" ");
                    }
                    modifiedItem.append(item.charAt(j));
                }
                sushiByName.add(modifiedItem.toString());
            }
        }

        return sushiByName;

    }
}

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
import java.util.ArrayList;
import java.util.Collection;

@Service
public class OWLService {

    OWLOntologyManager man = OWLManager.createOWLOntologyManager();
    OWLDataFactory df = man.getOWLDataFactory();
    OWLReasonerFactory rf = new ReasonerFactory();
    private OWLOntology o;
    private OWLReasoner r;

    public OWLService() throws OWLOntologyCreationException {
        IRI pizzaontology = IRI.create("http://protege.stanford.edu/ontologies/pizza/pizza.owl");
        File file = new File("/Users/chihinnestorfok/Documents/Year 3/COMP30040/Sushi.owl");
        //this.o = man.loadOntology(pizzaontology);
        this.o = man.loadOntologyFromOntologyDocument(file);
        this.r = rf.createReasoner(o);
    }

    public Collection<OWLClass> getSushi () {
        Collection<OWLClass> sushi = new ArrayList<OWLClass>();
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        r.getSubClasses(df.getOWLClass("http://www.sushiro.com/ontologies/sushiro.owl#NamedSushi"), true)
                .forEach(a -> sushi.add(a.getRepresentativeElement()));
        return sushi;
    }

}

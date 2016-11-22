package es.upm.oeg.tools.rdfshapes.stringsimilarity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.elasticsearch.common.base.Joiner;

import java.util.*;

public class SimilarProperties {

    public SimilarProperties(String localName) {
        this.localName = localName;
    }

    String localName;

    Set<String> qualifiedNames = new HashSet<>();

    Map<String, Double> similarNamesMap = new HashMap<>();

    Set<String> similarIriSet = new HashSet<>();

    public String getLocalName() {
        return localName;
    }

    public Set<String> getQualifiedNames() {
        return qualifiedNames;
    }

    public void addQualifiedNames(Collection<String> qualifiedNames) {
        qualifiedNames.addAll(qualifiedNames);
    }

    public void addQualifedName(String iri) {
        qualifiedNames.add(iri);
    }

    public void addSimilarLocalName(String property, double similarity){
        similarNamesMap.put(property, similarity);
    }

    public void addSimilarIri(String iri){
        similarIriSet.add(iri);
    }
    public void addSimiarIris(Collection<String> iris){
        similarIriSet.addAll(iris);
    }

    public Map<String, Double> getSimilarNamesMap() {
        return similarNamesMap;
    }

    public String getPrintableSimilarNameSet(){

        List<String> simlarNames = new ArrayList<>();
        List<Map.Entry<String, Double>> entryList = new ArrayList<>();

        for (Map.Entry<String, Double> entry : similarNamesMap.entrySet()) {
            entryList.add(entry);
        }

        Collections.sort(entryList, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (o1.getValue() < o2.getValue() ) {
                    return 1;
                } else if (o1.getValue() > o2.getValue() ) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (Map.Entry<String, Double> entry : entryList) {
            if (entry.getValue() == 1.0) {
                continue;
            }
            simlarNames.add(String.format("%s (%g)", entry.getKey(), entry.getValue()));
        }

        return Joiner.on(" | ").join(simlarNames);
    }

    public Set<String> getSimilarIriSet() {
        return similarIriSet;
    }

    public int getSimilarPropertyCount() {
        return similarIriSet.size();
    }

    public int getSimilarNamesCount() {
        return similarNamesMap.size();
    }


}

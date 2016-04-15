package es.upm.oeg.tools.rdfshapes.utils;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright 2014-2015 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Nandana Mihindukulasooriya
 * @since 1.0.0
 */
public class RDFTermUtils {

    private static AtomicInteger counter = new AtomicInteger();

    public static Map<String, String> nsMap = Maps.newHashMap();


    static {
        nsMap.put("http://dbpedia.org/ontology/", "dbo");
        nsMap.put("http://dbpedia.org/property/", "dbprop");
        nsMap.put("http://dbpedia.org/resource/", "dbpedia");
        nsMap.put("http://schema.org/", "schema");
        nsMap.put("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#", "dul");
        nsMap.put("http://www.openlinksw.com/schemas/virtrdf#", "openlinks");
        nsMap.put("http://www.openlinksw.com/schemas/DAV#", "openlinkdav");
        nsMap.put("http://www.w3.org/2002/07/owl#", "owl");
        nsMap.put("http://wikidata.dbpedia.org/resource/", "dbpedia-en");
        nsMap.put("http://www.ontologydesignpatterns.org/ont/d0.owl#", "d0");
        nsMap.put("http://xmlns.com/foaf/0.1/", "foaf");
        nsMap.put("http://purl.org/ontology/bibo/", "bibo");
        nsMap.put("http://www.opengis.net/gml/", "ogcgml");
        nsMap.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
        nsMap.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        nsMap.put("http://www.w3.org/2003/01/geo/wgs84_pos#", "geo");
        nsMap.put("http://www.geonames.org/ontology#", "geonames");
        nsMap.put("http://www.georss.org/georss/", "georss");
        nsMap.put("http://www.w3.org/2004/02/skos/core#", "skos");
        nsMap.put("http://www.w3.org/ns/sparql-service-description#", "sd");
        nsMap.put("http://www.w3.org/ns/sparql-service-description#", "sd");
        nsMap.put("http://purl.org/dc/elements/1.1/", "dc");
        nsMap.put("http://purl.org/dc/terms/", "dcterms");
        nsMap.put("http://rdf.freebase.com/ns/", "freebase");
        nsMap.put("http://purl.org/obo/owl/GO#", "go");
        nsMap.put("http://www.w3.org/ns/ldp#", "ldp");
        nsMap.put("http://www.w3.org/2000/10/swap/math#", "math");
        nsMap.put("http://dbpedia.org/class/yago/", "yago");
        nsMap.put("http://mpii.de/yago/resource/", "yago");
        nsMap.put("http://purl.org/commons/record/mesh/", "yago-res");
        nsMap.put("http://rdfs.org/sioc/ns#", "sioc");
        nsMap.put("http://purl.org/NET/scovo#", "scovo");
        nsMap.put("http://purl.org/ontology/mo/", "mo");
        nsMap.put("http://purl.org/NET/c4dm/event.owl#", "event");
        nsMap.put("http://open.vocab.org/terms/", "vo");
        nsMap.put("http://purl.org/muto/core#", "muto");
        nsMap.put("http://webenemasuno.linkeddata.es/ontology/OPMO/", "opmo");
        nsMap.put("http://openprovenance.org/model/opmo#", "openprov");
        nsMap.put("http://metadata.net/mpeg7/mpeg7.owl#", "mpeg7");
        nsMap.put("http://webenemasuno.linkeddata.es/ontology/MPEG7/", "mpeg7es");
        nsMap.put("http://www.w3.org/ns/prov#", "prov");
        nsMap.put("http://rtve.linkeddata.es/def/OntoRTVE#", "rtve");
        nsMap.put("http://data.linkedmdb.org/resource/movie/", "movie");
        nsMap.put("http://data.linkedmdb.org/resource/oddlinker/", "oddlinker");
        nsMap.put("http://data.linkedevents.org/def/location#", "leloc");
        nsMap.put("http://www.w3.org/ns/locn#", "locn");
        nsMap.put("http://www.w3.org/2006/vcard/ns#", "vcard");
        nsMap.put("http://www.w3.org/ns/ma-ont#", "maont");
        nsMap.put("http://geovocab.org/geometry#", "geometry");
        nsMap.put("http://vocab.org/transit/terms/", "transit");
        nsMap.put("http://3cixty.com/ontology#", "3cixty");

    }


    public static String getPrefixedTerm(String namespace, String localName){

        String prefix = nsMap.get(namespace);

        if (prefix == null) {
            prefix = "ns" + counter.getAndIncrement();
            nsMap.put(namespace, prefix);
        }

        return String.format("%s:%s", prefix, localName);

    }

    public static String getPrefixedTerm(String uri){

        int i = uri.lastIndexOf('#');
        if (i == -1) {
            i = uri.lastIndexOf('/');
        }
        if (i == -1) {
            //TODO handle other URIs, do we really have to?
            throw new IllegalStateException("Unable to extract local name from '"+ uri + "'");
        }

        String ns = uri.substring(0, i+1);
        String localName = uri.substring(i + 1, uri.length());

        return getPrefixedTerm(ns, localName);

    }

}
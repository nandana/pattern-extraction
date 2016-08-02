package es.upm.oeg.tools.rdfshapes.mappings;

/**
 * Copyright 2014-2016 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Nandana Mihindukulasooriya
 * @since 1.0.0
 */
public class PropPair {

    private String templateA;

    private String templateB;

    private String attributeA;

    private String attributeB;

    private String propA;

    private String propB;

    private long m1;

    private long m2;

    private long m3a;

    private long m3b;

    private long m4;

    private long m5a;

    private long m5b;


    public PropPair(String propA, String propB) {
        this.propA = propA;
        this.propB = propB;
    }

    public PropPair(String propA, String propB, long m1) {
        this.propA = propA;
        this.propB = propB;
        this.m1 = m1;
    }

    public PropPair(String templateA, String templateB, String attributeA, String attributeB,
                    String propA, String propB, long m1) {
        this.templateA = templateA;
        this.templateB = templateB;
        this.attributeA = attributeA;
        this.attributeB = attributeB;
        this.propA = propA;
        this.propB = propB;
        this.m1 = m1;
    }

    public String getPropA() {
        return propA;
    }

    public void setPropA(String propA) {
        this.propA = propA;
    }

    public String getPropB() {
        return propB;
    }

    public void setPropB(String propB) {
        this.propB = propB;
    }

    public String getTemplateA() {
        return templateA;
    }

    public void setTemplateA(String templateA) {
        this.templateA = templateA;
    }

    public String getTemplateB() {
        return templateB;
    }

    public void setTemplateB(String templateB) {
        this.templateB = templateB;
    }

    public String getAttributeA() {
        return attributeA;
    }

    public void setAttributeA(String attributeA) {
        this.attributeA = attributeA;
    }

    public String getAttributeB() {
        return attributeB;
    }

    public void setAttributeB(String attributeB) {
        this.attributeB = attributeB;
    }

    public long getM1() {
        return m1;
    }

    public void setM1(long m1) {
        this.m1 = m1;
    }

    public long getM2() {
        return m2;
    }

    public long getM3a() {
        return m3a;
    }

    public void setM3a(long m3a) {
        this.m3a = m3a;
    }

    public long getM3b() {
        return m3b;
    }

    public void setM3b(long m3b) {
        this.m3b = m3b;
    }

    public void setM2(long m2) {
        this.m2 = m2;
    }

    public long getM4() {
        return m4;
    }

    public void setM4(long m4) {
        this.m4 = m4;
    }

    public long getM5a() {
        return m5a;
    }

    public void setM5a(long m5a) {
        this.m5a = m5a;
    }

    public long getM5b() {
        return m5b;
    }

    public void setM5b(long m5b) {
        this.m5b = m5b;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof PropPair) {

            if (propA == null || propB == null) {
                return false;
            }

            if (propA.equals(((PropPair) obj).getPropA())
                    && propB.equals(((PropPair) obj).getPropB())) {
                    return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }
}

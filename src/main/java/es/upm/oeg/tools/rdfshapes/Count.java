package es.upm.oeg.tools.rdfshapes;

/**
 * Copyright 2014-2016 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
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
public class Count implements Comparable<Count> {

    public Count(String subject) {
        this.subject = subject;
    }

    public Count(String subject, long count) {
        this.subject = subject;
        this.count = count;
    }

    protected String subject;

    protected long count;

    public String getSubject() {
        return subject;
    }

    public long getCount() {
        return count;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setCount(long count) {
        this.count = count;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Count other) {

        if (this.count > other.count) {
            return 1;
        } else if (this.count < other.count) {
            return -1;
        } else {
            return 0;
        }

    }
}

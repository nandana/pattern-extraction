package es.upm.oeg.tools.rdfshapes.libdemo;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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
public class CommonsMathDemo {

    public static void main(String[] args) {

        DescriptiveStatistics stats = new DescriptiveStatistics();

        int inputArray[] = {3,4,5,6,2,3,4,3,3,4,3,6,3,2,3,1,2,1,1,1,3};

        // Add the data from the array
        for( int i = 0; i < inputArray.length; i++) {
            stats.addValue(inputArray[i]);
        }

        double mean = stats.getMean();
        double std = stats.getStandardDeviation();
        double median = stats.getPercentile(50);

        System.out.println("mean" + stats.getMean());
        System.out.println("standard deviation" + stats.getStandardDeviation());
        System.out.println("skewness" + stats.getSkewness());



    }

}

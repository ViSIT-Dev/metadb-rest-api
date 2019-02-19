package rest.persistence.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Class that supports a method to merge several given RDF triples within their common parts.
 */
public class TripleMerger {
    
    public static String mergeTriples(List<String> wrapperQueries) {

        LinkedList<String> singleTriples = extractSingleTriples(wrapperQueries);

        // Used to remember variable substitutions
        HashMap<String, String> substitutions = new HashMap<String, String>();

        LinkedList<String> mergedTriples = new LinkedList<String>();

        for(int i = 0; i < singleTriples.size(); ++i) {
            for(int j = i + 1; j < singleTriples.size(); ++j) {
                LinkedList<String> intermediateTriples = mergeTwoTriples(singleTriples.get(i), singleTriples.get(j), substitutions);

                for(String triple : intermediateTriples) {
                    if(!mergedTriples.contains(triple)) {
                        mergedTriples.add(triple);
                    }
                }
            }
        }

        return createCombinedTriples(mergedTriples);
    }

    private static String createCombinedTriples(LinkedList<String> mergedTriples) {
        String result = "";

        for(String triple : mergedTriples) {
            result += triple + " .\n";
        }

        return result;
    }

    private static LinkedList<String> mergeTwoTriples(String triple1, String triple2, HashMap<String, String> substitutions) {

        LinkedList<String> result = new LinkedList<String>();

        // Check substitutions and do them if found
        for(String subKey : substitutions.keySet()) {
            if(triple1.contains(subKey)) {
                triple1 = triple1.replace(subKey, substitutions.get(subKey));
            }

            if(triple2.contains(subKey)) {
                triple2 = triple2.replace(subKey, substitutions.get(subKey));
            }
        }

        // Check if the same triples are given
        if(triple1.equals(triple2)) {
            result.addAll(Arrays.asList(triple1, triple2));
        }

        // Do process:
        String[] triple1Split = triple1.split(" ");
        String[] triple2Split = triple2.split(" ");

        if(triple1Split[0].equals(triple2Split[0]) && triple1Split[1].equals(triple2Split[1]) && !triple1Split[2].equals(triple2Split[2])) {
            // Subject and predicate equal, object differing
            substitutions.put(triple2Split[2], triple1Split[2]);

            result.add(triple1);
        } else if(!triple1Split[0].equals(triple2Split[0]) && triple1Split[1].equals(triple2Split[1]) && triple1Split[2].equals(triple2Split[2])) {
            // Predicate and object equal, subject differing
            substitutions.put(triple2Split[0], triple1Split[0]);

            result.add(triple1);
        } else {
            if(!result.contains(triple1)) {
                result.add(triple1);
            }

            if(!result.contains(triple2)) {
                result.add(triple2);
            }
        }

        return result;
    }

    private static LinkedList<String> extractSingleTriples(List<String> wrapperQueries) {
        LinkedList<String> singleTriples = new LinkedList<String>();

        for(String wrapperQuery : wrapperQueries) {
            String[] split = wrapperQuery.split(" . ");

            Collections.addAll(singleTriples, split);
        }

        return singleTriples;
    }
}

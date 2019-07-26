package rest.persistence.util;

import model.namespace.CIDOC;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that supports a method to merge several given RDF triples within their common parts.
 */
public class TripleMerger {

    private final static String RDF_TYPE = "rdf:type";

    public static String mergeTriples(List<String> wrapperQueries) {

        LinkedList<String> singleTriples = extractSingleTriples(wrapperQueries);

        // Used to remember variable substitutions
        HashMap<String, String> substitutions = new HashMap<String, String>();

        findSubstitutions(substitutions, singleTriples);

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

    private static void findSubstitutions(HashMap<String, String> substitutions, LinkedList<String> triples) {
        for(int i = 0; i < triples.size(); ++i) {
            for(int j = i + 1; j < triples.size(); ++j) {
                String triple1 = triples.get(i);
                String triple2 = triples.get(j);
                
				if ((triple1.contentEquals("") || triple2.contentEquals(""))) {
					// happens...don't know why
				} else {
					String[] triple1Split = triple1.split(" ");
					String[] triple2Split = triple2.split(" ");

					if (triple1Split[0].equals(triple2Split[0]) && triple1Split[1].equals(triple2Split[1])
							&& !triple1Split[2].equals(triple2Split[2]) && triple1Split[2].startsWith("?")
							&& triple2Split[2].startsWith("?")) {
						// Subject and predicate equal, object differing

						// Special case: ignore cases that have rdf:type as predicate and do not refer
						// to the same type afterwards
						if (triple1Split[1].equals("<" + CIDOC.P2_HAS_TYPE + ">")
								&& triple2Split[1].equals("<" + CIDOC.P2_HAS_TYPE + ">")) {
							String furtherTriple1 = "";
							String furtherTriple2 = "";

							for (String searchTriple : triples) {
								if (searchTriple.startsWith(triple1Split[2] + " " + RDF_TYPE)) {
									furtherTriple1 = searchTriple;
								} else if (searchTriple.startsWith(triple2Split[2] + " " + RDF_TYPE)) {
									furtherTriple2 = searchTriple;
								}
							}

							String furtherTriple1Ending = furtherTriple1
									.substring(furtherTriple1.indexOf(RDF_TYPE) + RDF_TYPE.length());
							String furtherTriple2Ending = furtherTriple2
									.substring(furtherTriple2.indexOf(RDF_TYPE) + RDF_TYPE.length());

							if (furtherTriple1Ending.equals(furtherTriple2Ending)) {
								substitutions.put(triple2Split[2], triple1Split[2]);
							} // If different, no substitutions should happen

						} else {
							substitutions.put(triple2Split[2], triple1Split[2]);
						}
					} else if (!triple1Split[0].equals(triple2Split[0]) && triple1Split[1].equals(triple2Split[1])
							&& triple1Split[2].equals(triple2Split[2]) && triple1Split[0].startsWith("?")
							&& triple2Split[0].startsWith("?")) {
						// Predicate and object equal, subject differing
						substitutions.put(triple2Split[0], triple1Split[0]);
					}
				}
			}
		}

        // Treat transitive substitutions (could and should be swapped for a more efficient method -> TopSort?)
        boolean change = true;
        HashMap<String, String> adds = new HashMap<String, String>();
        LinkedList<String> removes = new LinkedList<String>();
        while(change) {
            change = false;
            for(String key : substitutions.keySet()) {
                for(String key2 : substitutions.keySet()) {
                    if(!key.equals(key2) && key.equals(substitutions.get(key2))) {
                        adds.put(key2, substitutions.get(key));

                        removes.add(key2);
                        change = true;
                    }
                }
            }

            for (String remove : removes) {
                substitutions.remove(remove);
            }

            substitutions.putAll(adds);
        }

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
//            result.addAll(Arrays.asList(triple1, triple2));
            result.add(triple1);
        } else {
            if(!result.contains(triple1)) {
                result.add(triple1);
            }

            if(!result.contains(triple2)) {
                result.add(triple2);
            }
        }

        // Do process:
//        String[] triple1Split = triple1.split(" ");
//        String[] triple2Split = triple2.split(" ");
//
//        if(triple1Split[0].equals(triple2Split[0]) && triple1Split[1].equals(triple2Split[1]) && !triple1Split[2].equals(triple2Split[2]) && triple1Split[2].startsWith("?") && triple2Split[2].startsWith("?")) {
//            // Subject and predicate equal, object differing
//            substitutions.put(triple2Split[2], triple1Split[2]);
//
//            result.add(triple1);
//        } else if(!triple1Split[0].equals(triple2Split[0]) && triple1Split[1].equals(triple2Split[1]) && triple1Split[2].equals(triple2Split[2])  && triple1Split[0].startsWith("?") && triple2Split[0].startsWith("?")) {
//            // Predicate and object equal, subject differing
//            substitutions.put(triple2Split[0], triple1Split[0]);
//
//            result.add(triple1);
//        } else {
//            if(!result.contains(triple1)) {
//                result.add(triple1);
//            }
//
//            if(!result.contains(triple2)) {
//                result.add(triple2);
//            }
//        }

        return result;
    }

    private HashMap<String, String> findSubstitutions(LinkedList<String> triples) {
        HashMap<String, String> substitutions = new HashMap<String, String>();



        return substitutions;
    }

    private static LinkedList<String> extractSingleTriples(List<String> wrapperQueries) {
        LinkedList<String> singleTriples = new LinkedList<String>();

        for(String wrapperQuery : wrapperQueries) {
            String[] split = wrapperQuery.split(" \\. ");

            Collections.addAll(singleTriples, split);
        }

        return singleTriples;
    }
}

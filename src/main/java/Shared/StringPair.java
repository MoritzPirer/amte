package Shared;

import java.util.Set;

public record StringPair(String first, String second) {
    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StringPair stringPair = (StringPair) o;

        return first.equals(stringPair.first) && second.equals(stringPair.second);
    }

    /**
     * checks if the given target is the first element of a StringPair in container
     * @param container the container to look through
     * @param target the target to look for
     * @return true if container has target as a first element, false otherwise
     */
    public static boolean containsAsFirst(Set<StringPair> container, String target) {
        for (StringPair pair : container) {
            if (target.equals(pair.first())) {
                return true;
            }
        }

        return false;
    }

    /**
     * assuming that container has a pair that has first as its first element, find the matching second element
     * @param container the container to look through
     * @param first the token for which the counterpart should be found
     * @return the token that matches opening, or null if no such token exists
     */
    public static String getSecond(Set<StringPair> container, String first) {
        for (StringPair pair : container) {
            if (first.equals(pair.first())) {
                return pair.second();
            }
        }

        return null;
    }
}

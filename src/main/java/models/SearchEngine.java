package models;

import java.util.List;

public interface SearchEngine {
    void setup();
    List<Integer> search(String searchText, int maxNumberOfHits);
    List<Integer> search(String searchText, int maxNumberOfHits, String locale);
}

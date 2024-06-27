package models;

import java.util.List;

public interface SearchEngine {
    void setup();
    List<Integer> search(String searchText, int maxNumberOfHits);
}

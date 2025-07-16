package org.example.UI;

import java.util.*;

public class DataProvider {

    private final Map<String, List<String>> dati;

    public DataProvider() {
        dati = new HashMap<>();
        dati.put("Frutta", Arrays.asList("Mela", "Banana", "Arancia"));
        dati.put("Animali", Arrays.asList("Cane", "Gatto", "Elefante"));
        dati.put("Colori", Arrays.asList("Rosso", "Verde", "Blu"));
    }

    public Set<String> getNomiListe() {
        return dati.keySet();
    }

    public List<String> getElementiDiLista(String nomeLista) {
        return dati.getOrDefault(nomeLista, Collections.emptyList());
    }
}

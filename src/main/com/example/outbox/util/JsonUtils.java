package com.example.outbox.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Classe di utilità per operazioni comuni di serializzazione/deserializzazione JSON.
 * Fornisce metodi statici per semplificare il lavoro con JSON in tutta l'applicazione.
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper = createObjectMapper();

    /**
     * Crea e configura un ObjectMapper con le impostazioni comuni per l'applicazione.
     *
     * @return Un ObjectMapper configurato
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registra il modulo per gestire correttamente le date Java 8 (LocalDate, LocalDateTime, ecc.)
        mapper.registerModule(new JavaTimeModule());

        // Configura per non serializzare le date come timestamp ma come stringhe ISO
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return mapper;
    }

    /**
     * Converte un oggetto in una stringa JSON.
     *
     * @param object L'oggetto da serializzare
     * @return La stringa JSON o null in caso di errore
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Errore durante la serializzazione in JSON", e);
            return null;
        }
    }

    /**
     * Converte un oggetto in una stringa JSON formattata (pretty print).
     *
     * @param object L'oggetto da serializzare
     * @return La stringa JSON formattata o null in caso di errore
     */
    public static String toPrettyJson(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Errore durante la serializzazione in JSON formattato", e);
            return null;
        }
    }

    /**
     * Converte una stringa JSON in un oggetto del tipo specificato.
     *
     * @param json La stringa JSON da deserializzare
     * @param clazz La classe dell'oggetto di destinazione
     * @param <T> Il tipo dell'oggetto di destinazione
     * @return L'oggetto deserializzato o null in caso di errore
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("Errore durante la deserializzazione dal JSON", e);
            return null;
        }
    }

    /**
     * Controlla se una stringa è un JSON valido.
     *
     * @param json La stringa da validare
     * @return true se la stringa è un JSON valido, false altrimenti
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Converte una stringa JSON in un JsonNode per manipolazione avanzata.
     *
     * @param json La stringa JSON
     * @return Un JsonNode o null in caso di errore
     */
    public static JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            log.error("Errore durante il parsing del JSON", e);
            return null;
        }
    }

    /**
     * Fonde due oggetti JSON.
     * Le proprietà del secondo oggetto sovrascrivono quelle del primo.
     *
     * @param json1 Primo oggetto JSON
     * @param json2 Secondo oggetto JSON (ha la precedenza)
     * @return Il JSON risultante dalla fusione o null in caso di errore
     */
    public static String mergeJson(String json1, String json2) {
        try {
            JsonNode node1 = objectMapper.readTree(json1);
            JsonNode node2 = objectMapper.readTree(json2);

            // Crea una deep copy di node1
            JsonNode merged = objectMapper.readTree(objectMapper.writeValueAsString(node1));

            // Applica node2 sopra merged
            if (merged.isObject() && node2.isObject()) {
                ObjectMapper mapper = new ObjectMapper();
                Object mergedObj = mapper.treeToValue(merged, Object.class);
                Object node2Obj = mapper.treeToValue(node2, Object.class);

                // Esegue la fusione a livello di Java Object
                // (questo è un approccio semplificato, in produzione potrebbe
                // essere necessario un algoritmo di merge più sofisticato)
                if (mergedObj instanceof java.util.Map && node2Obj instanceof java.util.Map) {
                    ((java.util.Map) mergedObj).putAll((java.util.Map) node2Obj);
                    return objectMapper.writeValueAsString(mergedObj);
                }
            }

            // Fallback: restituisce solo il secondo JSON
            return json2;
        } catch (IOException e) {
            log.error("Errore durante la fusione degli oggetti JSON", e);
            return null;
        }
    }

    /**
     * Ottiene l'istanza dell'ObjectMapper per usi avanzati.
     *
     * @return L'istanza configurata di ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}


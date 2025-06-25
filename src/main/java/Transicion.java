// Clase que representa una transición en un autómata
public class Transicion {
    // Estado de origen de la transición
    public Estado desde;
    // Símbolo o etiqueta que dispara la transición
    public String simbolo;
    // Estado destino al que se llega con esta transición
    public Estado hacia;

    public Transicion(Estado desde, String simbolo, Estado hacia) {
        this.desde = desde;
        this.simbolo = simbolo;
        this.hacia = hacia;
    }

    /**
     * Representa la transición como una cadena legible.
     * Ejemplo: "0 -> 1 [a]"
     *
     * @return String con la representación de la transición
     */
    @Override
    public String toString() {
        return desde.id + " -> " + hacia.id + " [" + simbolo + "]";
    }
}

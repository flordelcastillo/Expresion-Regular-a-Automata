import java.util.ArrayList; // Lista dinámica para las transiciones
import java.util.HashSet;   // Conjunto para evitar duplicados en estados y alfabeto
import java.util.List;
import java.util.Set;

// Definición de la clase AFD (Autómata Finito Determinista) que hereda de AFN (Autómata Finito No Determinista)
public class AFD extends AFN {

    // Estado inicial del AFD (solo uno en un AFD)
    public Estado estadoInicial;

    // Conjunto de estados finales (aceptación) del AFD
    public Set<Estado> estadosFinales = new HashSet<>();

    // Lista de transiciones del AFD. Cada transición conecta un estado origen con uno destino según un símbolo.
    public List<Transicion> transiciones = new ArrayList<>();

    // Conjunto de todos los estados del AFD
    public Set<Estado> estados = new HashSet<>();

    // Conjunto de símbolos que forman el alfabeto del AFD
    public Set<String> alfabeto = new HashSet<>();

    public boolean accepts(String input) {
        Estado actual = this.estadoInicial;

        for (int i = 0; i < input.length(); i++) {
            char simbolo = input.charAt(i);
            Estado siguiente = null;

            for (Transicion t : this.transiciones) {
                if (t.getOrigen().equals(actual) && t.getSimbolo().equals(String.valueOf(simbolo))) {
                    siguiente = t.getDestino();
                    break;
                }
            }

            if (siguiente == null) {
                return false; // No hay transición válida
            }

            actual = siguiente;
        }

        return this.estadosFinales.contains(actual);
    }



    public AFD(AFN afn) {
        AFD afdGenerado = afn.convertirADeterminista();
        this.estadoInicial = afdGenerado.estadoInicial;
        this.estados = afdGenerado.estados;
        this.estadosFinales = afdGenerado.estadosFinales;
        this.transiciones = afdGenerado.transiciones;
        this.alfabeto = afdGenerado.alfabeto;
    }
    public AFD() {
        this.estados = new HashSet<>();
        this.estadosFinales = new HashSet<>();
        this.transiciones = new ArrayList<>();
        this.alfabeto = new HashSet<>();
    }

}

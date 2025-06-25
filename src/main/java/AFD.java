// Importación de clases necesarias para colecciones
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
}

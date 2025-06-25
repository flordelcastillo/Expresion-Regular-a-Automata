import java.util.*;
import java.util.stream.Collectors;

public class Estado {
    // Identificador numérico del estado, usado para estados individuales en AFN
    public int id;

    // Conjunto de IDs de estados, usado para estados "compuestos" en AFD (subconjuntos de estados AFN)
    public Set<Integer> conjunto;

    // Nombre representativo del estado (por ejemplo: "q0" o "\"q0,q1,q2\"")
    public String nombre;

    // Constructor para crear un estado simple (AFN)
    public Estado(int id) {
        this.id = id;
        // Nombre estándar para un estado simple: "q" seguido del id
        this.nombre = "q" + id;
    }

    // Constructor para crear un estado compuesto (AFD) a partir de un conjunto de IDs
    public Estado(Set<Integer> conjunto) {
        // Crear una copia del conjunto para evitar aliasing
        this.conjunto = new HashSet<>(conjunto);
        // Generar el nombre con los estados componentes entre comillas dobles
        // Ordena los IDs, los convierte a formato "qID", y los une con comas
        this.nombre = "\"" + conjunto.stream()
                .sorted()
                .map(id -> "q" + id)
                .collect(Collectors.joining(",")) + "\"";
    }

    // Método para representar el estado como texto (usado por ejemplo al imprimir)
    @Override
    public String toString() {
        return nombre;
    }

    // Método para comparar igualdad entre dos objetos Estado
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Misma referencia, son iguales
        if (o == null || getClass() != o.getClass()) return false; // Null o distinto tipo, no son iguales
        Estado estado = (Estado) o;

        // Si ambos son estados simples (sin conjunto), se comparan sus IDs
        if (conjunto == null && estado.conjunto == null) {
            return id == estado.id;
        }

        // Si ambos son estados compuestos (con conjunto), se comparan los conjuntos
        if (conjunto != null && estado.conjunto != null) {
            return conjunto.equals(estado.conjunto);
        }

        // Caso de comparación entre estado simple y compuesto: no son iguales
        return false;
    }

    // Método para generar un código hash, usado en colecciones como HashSet o HashMap
    @Override
    public int hashCode() {
        // Si es estado compuesto, devuelve el hash del conjunto
        if (conjunto != null) {
            return conjunto.hashCode();
        } else {
            // Si es estado simple, devuelve hash basado en el ID
            return Objects.hash(id);
        }
    }
}

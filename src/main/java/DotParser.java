// Importación de clases necesarias
import java.io.*;                         // Para manejo de archivos
import java.util.regex.*;                // Para trabajar con expresiones regulares
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

// Clase encargada de leer un archivo DOT y reconstruir un objeto AFN a partir de él
public class DotParser {

    // Método que lee un archivo DOT y devuelve un objeto AFN construido a partir de su contenido
    public static AFN leerDesdeArchivo(String ruta) throws IOException {
        AFN afn = new AFN(); // Crear un nuevo AFN vacío
        Map<String, Estado> mapaEstados = new HashMap<>(); // Mapa para evitar estados duplicados

        BufferedReader br = new BufferedReader(new FileReader(ruta)); // Lector de archivo línea por línea
        String linea;
        Set<String> estadosFinalesTemp = new HashSet<>(); // Almacena temporalmente los nombres de los estados finales

        // Leer línea por línea el archivo
        while ((linea = br.readLine()) != null) {
            linea = linea.trim(); // Eliminar espacios al inicio y final

            // Omitir líneas decorativas o de formato del archivo DOT
            if (linea.startsWith("digraph") ||
                    linea.startsWith("edge [") ||
                    linea.equals("rankdir=LR;") ||
                    linea.equals("node [shape = circle];") ||
                    linea.equals("{") || linea.equals("}")) {
                continue; // Saltar estas líneas
            }

            // Línea con estados finales: "node [shape = doublecircle]; q3 q4 q5;"
            if (linea.startsWith("node [shape = doublecircle];")) {
                // Eliminar el prefijo y el punto y coma final
                String estadosStr = linea.replace("node [shape = doublecircle];", "").trim().replace(";", "");
                // Separar los nombres de estados por espacios
                String[] estados = estadosStr.split("\\s+");
                for (String nombre : estados) {
                    if (!nombre.isEmpty()) {
                        estadosFinalesTemp.add(nombre); // Guardar los nombres temporalmente
                    }
                }
                continue; // Pasar a la siguiente línea
            }

            // Línea que define el estado inicial: "inic -> q0;"
            if (linea.startsWith("inic -> ")) {
                // Obtener el nombre del estado destino (sin ";")
                String destino = linea.split("->")[1].replace(";", "").trim();
                // Obtener o crear el objeto Estado correspondiente
                Estado inicial = mapaEstados.computeIfAbsent(destino,
                        k -> new Estado(Integer.parseInt(k.substring(1)))); // Extrae el número del nombre "q0" → 0
                afn.setEstadoInicial(inicial); // Asignar como estado inicial del AFN
                continue;
            }

            // Línea de transición: "q0 -> q1 [label = "a|b|c"];"
            // Usamos expresión regular para capturar: q0, q1 y "a|b|c"
            Pattern p = Pattern.compile("q(\\d+) -> q(\\d+) \\[label = \"(.*)\"\\];");
            Matcher m = p.matcher(linea);

            if (m.find()) {
                // Extraer estados desde y hacia, usando los grupos de la regex
                Estado desde = mapaEstados.computeIfAbsent("q" + m.group(1),
                        k -> new Estado(Integer.parseInt(m.group(1))));
                Estado hacia = mapaEstados.computeIfAbsent("q" + m.group(2),
                        k -> new Estado(Integer.parseInt(m.group(2))));
                String simbolos = m.group(3); // Extraer los símbolos separados por "|"

                // Agregar una transición por cada símbolo encontrado
                for (String simbolo : simbolos.split("\\|")) {
                    afn.agregarTransicion(desde, simbolo, hacia);
                }
            }
        }

        // Marcar como finales los estados que estaban definidos como tales en el archivo
        for (String nombre : estadosFinalesTemp) {
            Estado est = mapaEstados.get(nombre);
            if (est != null) {
                afn.agregarEstadoFinal(est);
            }
        }

        br.close(); // Cerrar el archivo
        return afn; // Devolver el AFN reconstruido
    }
}

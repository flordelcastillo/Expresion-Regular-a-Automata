// Importación de clases necesarias para operaciones de entrada/salida y colecciones
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

// Clase encargada de exportar un AFN o AFD a un archivo DOT (usado por Graphviz para visualización de grafos)
public class DotExporter {

    // Método para escribir un archivo DOT a partir de un AFN
    public static void escribirAFN(AFN afn, String nombreArchivo, String nombreGrafo) throws IOException {
        // Crear escritor de archivo
        PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo));

        // Encabezado del grafo
        pw.printf("digraph %s {%n", nombreGrafo);
        pw.println("\tnode [fontname=\"Helvetica,Arial,sans-serif\"]");
        pw.println("\tedge [fontname=\"Helvetica,Arial,sans-serif\"]");
        pw.println("\trankdir=LR;"); // Dirección izquierda a derecha

        // Si hay estados finales, se los representa con doble círculo
        if (!afn.estadosFinales.isEmpty()) {
            pw.print("\tnode [shape = doublecircle]; ");
            for (Estado ef : afn.estadosFinales) {
                pw.print(ef + "; ");
            }
            pw.println(); // Salto de línea
        }

        // El resto de los estados se dibujan como círculos normales
        pw.println("\tnode [shape = circle];");

        // Nodo de inicio ficticio (punto) y flecha hacia el estado inicial real
        pw.println("\tinic[shape=point];");
        pw.printf("\tinic -> %s;%n", afn.estadoInicial);

        // Agrupar transiciones por pares de origen y destino para combinarlas en una sola flecha con múltiples etiquetas
        Map<String, List<String>> agrupadas = new HashMap<>();
        for (Transicion t : afn.transiciones) {
            String clave = t.desde + "->" + t.hacia;
            // Agrupa los símbolos de transición para el mismo par (desde -> hacia)
            agrupadas.computeIfAbsent(clave, k -> new ArrayList<>()).add(t.simbolo);
        }

        // Escribir todas las transiciones agrupadas
        for (Map.Entry<String, List<String>> entrada : agrupadas.entrySet()) {
            String[] partes = entrada.getKey().split("->");
            String desde = partes[0];
            String hacia = partes[1];
            // Unir los símbolos con "|", ejemplo: a|b|c
            String etiqueta = String.join("|", entrada.getValue());
            pw.printf("\t%s -> %s [label = \"%s\"];%n", desde, hacia, etiqueta);
        }

        // Cierre del grafo
        pw.println("}");
        pw.close(); // Cerrar archivo
    }

    // Método para escribir un archivo DOT a partir de un AFD
    public static void escribirAFD(AFD afd, String nombreArchivo, String nombreGrafo) throws IOException {
        // Crear escritor de archivo
        PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo));

        // Encabezado del grafo
        pw.printf("digraph %s {%n", nombreGrafo);
        pw.println("\tnode [fontname=\"Helvetica,Arial,sans-serif\"]");
        pw.println("\tedge [fontname=\"Helvetica,Arial,sans-serif\"]");
        pw.println("\trankdir=LR;"); // Dirección de izquierda a derecha

        // Mensajes de depuración por consola
        //System.out.println("Estado inicial(DOTEXPORTER): " + afd.estadoInicial);
        //System.out.println("Estados(DOTEXPORTER): " + afd.estados);
        //System.out.println("Transiciones(DOTEXPORTER): " + afd.transiciones);
        //System.out.println("Estados finales(DOTEXPORTER): " + afd.estadosFinales);

        // Dibujar los estados finales como doble círculo
        if (!afd.estadosFinales.isEmpty()) {
            pw.print("\tnode [shape = doublecircle]; ");
            for (Estado ef : afd.estadosFinales) {
                pw.print(ef + "; ");
            }
            pw.println();
        }

        // El resto como círculos normales
        pw.println("\tnode [shape = circle];");

        // Nodo inicial ficticio que apunta al estado inicial real
        pw.println("\tinic[shape=point];");
        pw.printf("\tinic -> %s;%n", afd.estadoInicial);

        // Agrupar transiciones por par de estados (desde -> hacia) con múltiples símbolos
        Map<String, List<String>> agrupadas = new HashMap<>();
        for (Transicion t : afd.transiciones) {
            String clave = t.desde + "->" + t.hacia;
            agrupadas.computeIfAbsent(clave, k -> new ArrayList<>()).add(t.simbolo);
        }

        // Escribir todas las transiciones agrupadas en el archivo
        for (Map.Entry<String, List<String>> entrada : agrupadas.entrySet()) {
            String[] partes = entrada.getKey().split("->");
            String desde = partes[0];
            String hacia = partes[1];
            String etiqueta = String.join("|", entrada.getValue()); // Unifica símbolos con "|"
            pw.printf("\t%s -> %s [label = \"%s\"];%n", desde, hacia, etiqueta);
        }

        // Cierre del grafo
        pw.println("}");
        pw.close(); // Cerrar archivo
    }
}

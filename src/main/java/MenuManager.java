// Clase MenuManager modularizada
import java.io.IOException;
import java.util.*;

public class MenuManager {
    private final Map<String, AFN> automatas;
    private final String ruta_dot = "C:\\Users\\Enekon\\Desktop\\Expresion-Regular-a-Automata-main\\archivos_dot\\";
    private final String ruta_png = "C:\\Users\\Enekon\\Desktop\\Expresion-Regular-a-Automata-main\\automatas_png\\";
    private final String filePath = "C:\\Users\\Enekon\\Desktop\\Expresion-Regular-a-Automata-main\\src\\main\\java\\archivo.txt";
    private int contadorAFN = 1;

    public MenuManager( Map<String, AFN> automatas) {
        this.automatas = automatas;
    }

    public void convertirAFNaAFD(String nombre) throws IOException, InterruptedException {
        AFN afn = automatas.get(nombre);
        if (afn == null) {
            System.out.println("No existe ese AFN.");
            return;
        }
        AFD afd = afn.convertirADeterminista();
        String nombreAFD = nombre + "_AFD";
        String salidaDot = "archivos_dot/" + nombreAFD + ".dot";
        String salidaPng = "automatas_png/" + nombreAFD + ".png";
        DotExporter.escribirAFD(afd, salidaDot, nombreAFD);
        GraphvizExporter.generarImagen(salidaDot, salidaPng);
        System.out.println("✅ AFD generado como: " + nombreAFD);
        System.out.println("   DOT: " + salidaDot);
        System.out.println("   PNG: " + salidaPng);
    }

    public AFN copiarAFN(AFN original) {
        AFN copia = new AFN();
        Map<Estado, Estado> mapeo = new HashMap<>();
        for (Estado est : original.estados) {
            Estado nuevo = new Estado(est.id);
            mapeo.put(est, nuevo);
            copia.estados.add(nuevo);
            if (est.equals(original.estadoInicial)) copia.estadoInicial = nuevo;
        }
        for (Estado ef : original.estadosFinales) copia.estadosFinales.add(mapeo.get(ef));
        for (Transicion t : original.transiciones) {
            Estado desde = mapeo.get(t.desde);
            Estado hacia = mapeo.get(t.hacia);
            copia.agregarTransicion(desde, t.simbolo, hacia);
        }
        copia.alfabeto.addAll(original.alfabeto);
        return copia;
    }

    public void mostrarAFNs() throws IOException, InterruptedException {
        if (automatas.isEmpty()) {
            System.out.println("No hay AFNs cargados.");
            return;
        }
        for (Map.Entry<String, AFN> entrada : automatas.entrySet()) {
            String nombre = entrada.getKey();
            AFN afn = entrada.getValue();
            String dotPath = "archivos_dot/" + nombre + ".dot";
            String pngPath = "automatas_png/" + nombre + ".png";
            DotExporter.escribirAFN(afn, dotPath, nombre);
            GraphvizExporter.generarImagen(dotPath, pngPath);
            System.out.println("✅ Visualización generada para " + nombre + ":");
            System.out.println("   DOT: " + dotPath);
            System.out.println("   PNG: " + pngPath);
        }
    }

    public void crearAFNDesdeRegex(String regex) {
        try {
            AFN afn = RegexToAFN.build(regex);
            String nombreAFN = "af" + contadorAFN++;
            automatas.put(nombreAFN, afn);
            String salidaDot = ruta_dot + nombreAFN + ".dot";
            String salidaPng = ruta_png + nombreAFN + ".png";
            DotExporter.escribirAFN(afn, salidaDot, nombreAFN);
            GraphvizExporter.generarImagen(salidaDot, salidaPng);
            System.out.println("✅ AFN creado correctamente desde expresión regular.");
            System.out.println("   Nombre: " + nombreAFN);
            System.out.println("   DOT: " + salidaDot);
            System.out.println("   PNG: " + salidaPng);
        } catch (Exception e) {
            System.out.println("❌ Error: Expresión regular inválida. " + e.getMessage());
        }
    }

    public void usarGrep(String grepRegex) {
        List<String> resultados = Grep.grepFromFile(grepRegex, filePath);
        System.out.println("\nLíneas que coinciden:");
        if (resultados.isEmpty()) System.out.println("(ninguna coincidencia)");
        else resultados.forEach(System.out::println);
    }

    public void cargarAFN(String archivo) {
        String ruta = ruta_dot + archivo;
        try {
            AFN afn = DotParser.leerDesdeArchivo(ruta);
            String nombreAFN = "af" + contadorAFN++;
            automatas.put(nombreAFN, afn);
            System.out.println("✔ AFN cargado como: " + nombreAFN + " = " + archivo);
        } catch (IOException e) {
            System.err.println("❌ Error al leer el archivo: " + e.getMessage());
        }
    }
}


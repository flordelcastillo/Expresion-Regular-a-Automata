import java.io.IOException;
import java.util.*;

public class Main {
    static String ruta_dot = "C:\\Users\\tomif\\Documents\\florchu\\ExpresionaAutomata\\archivos_dot\\";
    static String ruta_png = "C:\\Users\\tomif\\Documents\\florchu\\ExpresionaAutomata\\automatas_png\\";
    static String filePath = "C:\\Users\\tomif\\Documents\\florchu\\ExpresionaAutomata\\src\\main\\java\\archivo.txt";
    // Scanner para leer entradas del usuario
    static Scanner scanner = new Scanner(System.in);

    // Mapa para almacenar los AFNs cargados con un nombre identificador
    static Map<String, AFN> automatas = new LinkedHashMap<>();

    // Contador incremental para asignar nombres únicos a los AFNs
    static int contadorAFN = 1;

    public static void main(String[] args) throws IOException, InterruptedException {
        int opcion;
        do {
            limpiarPantalla();
            // Menú principal
            System.out.println("--- Menú de Autómatas Finitos No Deterministas (AFN) ---");
            System.out.println("1. Cargar AFN desde archivo DOT");
            System.out.println("2. Mostrar AFNs cargados (genera DOT + PNG)");
            System.out.println("3. Operaciones con AFN");
            System.out.println("4. Convertir AFN a AFD");
            System.out.println("5. Crear AFN desde expresión regular");
            System.out.println("6. Ejecutar búsqueda tipo grep");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            // Switch de opciones
            switch (opcion) {
                case 1:
                    limpiarPantalla();
                    cargarAFN();   // Cargar AFN desde archivo
                    pausar();
                    break;
                case 2:
                    limpiarPantalla();
                    mostrarAFNs();  // Mostrar todos los AFNs cargados
                    pausar();
                    break;
                case 3:
                    limpiarPantalla();
                    submenuOperaciones(); // Submenú con operaciones (unión, concat., etc.)
                    pausar();
                    break;
                case 4:
                    convertirAFNaAFD(); // Convertir un AFN a AFD
                    break;
                case 5:
                    limpiarPantalla();
                    crearAFNDesdeRegex();
                    pausar();
                    break;
                case 6:
                    limpiarPantalla();
                    usarGrep();
                    pausar();
                    break;
                case 0:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
                    break;
            }
        } while (opcion != 0); // Repetir hasta que el usuario elija salir
    }

    // Carga un AFN desde un archivo DOT en la carpeta indicada
    private static void cargarAFN() {
        System.out.print("Ingrese el nombre del archivo DOT (en carpeta assets/, por ejemplo: a_b_c.dot): ");
        String archivo = scanner.nextLine().trim();
        String ruta = ruta_dot + archivo;
        try {
            AFN afn = DotParser.leerDesdeArchivo(ruta);
            String nombreAFN = "af" + contadorAFN++; // nombre único
            automatas.put(nombreAFN, afn); // almacenar
            System.out.println("✔ AFN cargado como: " + nombreAFN + " = " + archivo);
        } catch (IOException e) {
            System.err.println("❌ Error al leer el archivo: " + e.getMessage());
        }
    }

    // Genera el DOT y PNG para todos los AFNs cargados
    private static void mostrarAFNs() throws IOException, InterruptedException {
        if (automatas.isEmpty()) {
            System.out.println("No hay AFNs cargados.");
            return;
        }

        for (Map.Entry<String, AFN> entrada : automatas.entrySet()) {
            String nombre = entrada.getKey();
            AFN afn = entrada.getValue();
            String dotPath = "archivos_dot/" + nombre + ".dot";
            String pngPath = "automatas_png/" + nombre + ".png";

            DotExporter.escribirAFN(afn, dotPath, nombre); // genera .dot
            GraphvizExporter.generarImagen(dotPath, pngPath); // genera .png

            System.out.println("✅ Visualización generada para " + nombre + ":");
            System.out.println("   DOT: " + dotPath);
            System.out.println("   PNG: " + pngPath);
        }
    }

    // Lectura robusta de enteros desde consola
    private static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.print("Entrada inválida. Ingrese un número: ");
            }
        }
    }

    // Submenú con operaciones sobre AFNs
    private static void submenuOperaciones() throws IOException, InterruptedException {
        limpiarPantalla();
        int opcion;
        System.out.println("=== OPERACIONES CON AFN ===");
        System.out.println("1. Unión");
        System.out.println("2. Concatenación");
        System.out.println("3. Clausura de Kleene");
        System.out.println("0. Volver al menú principal");
        System.out.print("Seleccione una opción: ");
        opcion = leerEntero();

        switch (opcion) {
            case 1: unirAFNs(); break;
            case 2: concatenarAFNs(); break;
            case 3: aplicarClausuraKleene(); break;
            case 0: break;
            default: System.out.println("Opción inválida.");
        }
    }

    // Pausa la ejecución hasta que se presione Enter
    private static void pausar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    // Permite concatenar dos AFNs existentes
    private static void concatenarAFNs() throws IOException, InterruptedException {
        if (automatas.size() < 2) {
            System.out.println("Se requieren al menos dos AFNs cargados.");
            pausar();
            return;
        }

        // Mostrar AFNs disponibles
        System.out.println("AFNs disponibles:");
        for (String nombre : automatas.keySet()) {
            System.out.println("- " + nombre);
        }

        // Solicitar nombres de AFNs a concatenar
        System.out.print("Nombre del primer AFN: ");
        String nombre1 = scanner.nextLine();
        System.out.print("Nombre del segundo AFN: ");
        String nombre2 = scanner.nextLine();

        AFN afn1 = automatas.get(nombre1);
        AFN afn2 = automatas.get(nombre2);

        if (afn1 == null || afn2 == null) {
            System.out.println("Uno de los AFNs no existe.");
            pausar();
            return;
        }

        AFN copiaAFN2 = copiarAFN(afn2); // crear copia para no modificar el original
        afn1.concatenarAFN(copiaAFN2);   // aplicar concatenación

        // Guardar nuevo AFN resultante
        String nombreResultado = nombre1 + "_concat_" + nombre2;
        automatas.put(nombreResultado, afn1);

        // Exportar visualización
        String salidaDot = "archivos_dot/" + nombreResultado + ".dot";
        String salidaPng = "automatas_png/" + nombreResultado + ".png";
        DotExporter.escribirAFN(afn1, salidaDot, nombreResultado);
        GraphvizExporter.generarImagen(salidaDot, salidaPng);

        System.out.println("AFN concatenado generado como: " + nombreResultado);
        pausar();
    }

    // Crea una copia profunda de un AFN
    private static AFN copiarAFN(AFN original) {
        AFN copia = new AFN();
        Map<Estado, Estado> mapeo = new HashMap<>();

        // Copiar estados
        for (Estado est : original.estados) {
            Estado nuevo = new Estado(est.id);
            mapeo.put(est, nuevo);
            copia.estados.add(nuevo);
            if (est.equals(original.estadoInicial)) {
                copia.estadoInicial = nuevo;
            }
        }

        // Copiar estados finales
        for (Estado ef : original.estadosFinales) {
            copia.estadosFinales.add(mapeo.get(ef));
        }

        // Copiar transiciones
        for (Transicion t : original.transiciones) {
            Estado desde = mapeo.get(t.desde);
            Estado hacia = mapeo.get(t.hacia);
            copia.agregarTransicion(desde, t.simbolo, hacia);
        }

        // Copiar alfabeto
        copia.alfabeto.addAll(original.alfabeto);

        return copia;
    }

    // Convierte un AFN a AFD utilizando el algoritmo de subconjuntos
    private static void convertirAFNaAFD() throws IOException, InterruptedException {
        if (automatas.isEmpty()) {
            System.out.println("No hay AFNs cargados.");
            pausar();
            return;
        }

        System.out.println("AFNs disponibles:");
        for (String nombre : automatas.keySet()) {
            System.out.println("- " + nombre);
        }

        System.out.print("Nombre del AFN a convertir: ");
        String nombre = scanner.nextLine();
        AFN afn = automatas.get(nombre);

        if (afn == null) {
            System.out.println("No existe ese AFN.");
            pausar();
            return;
        }

        // Conversión a AFD
        AFD afd = afn.convertirADeterminista();


        String nombreAFD = nombre + "_AFD";
        String salidaDot = "archivos_dot/" + nombreAFD + ".dot";
        String salidaPng = "automatas_png/" + nombreAFD + ".png";

        DotExporter.escribirAFD(afd, salidaDot, nombreAFD);
        GraphvizExporter.generarImagen(salidaDot, salidaPng);

        System.out.println("✅ AFD generado como: " + nombreAFD);
        System.out.println("   DOT: " + salidaDot);
        System.out.println("   PNG: " + salidaPng);
        pausar();
    }

    // Aplica la clausura de Kleene a un AFN seleccionado
    private static void aplicarClausuraKleene() throws IOException, InterruptedException {
        if (automatas.isEmpty()) {
            System.out.println("No hay AFNs cargados.");
            pausar();
            return;
        }

        System.out.println("AFNs disponibles:");
        for (String nombre : automatas.keySet()) {
            System.out.println("- " + nombre);
        }

        System.out.print("Nombre del AFN para aplicar clausura de Kleene: ");
        String nombre = scanner.nextLine();
        AFN afnOriginal = automatas.get(nombre);

        if (afnOriginal == null) {
            System.out.println("No existe ese AFN.");
            pausar();
            return;
        }

        // Crear copia, aplicar operación, guardar y exportar
        AFN copiaAFN = copiarAFN(afnOriginal);
        copiaAFN.aplicarClausuraKleene();

        String nombreResultado = nombre + "_kleene";
        automatas.put(nombreResultado, copiaAFN);

        String salidaDot = "archivos_dot/" + nombreResultado + ".dot";
        String salidaPng = "automatas_png/" + nombreResultado + ".png";
        DotExporter.escribirAFN(copiaAFN, salidaDot, nombreResultado);
        GraphvizExporter.generarImagen(salidaDot, salidaPng);

        System.out.println("✅ Clausura de Kleene aplicada. Resultado guardado como: " + nombreResultado);
        pausar();
    }

    // Une dos AFNs usando un nuevo estado inicial y transiciones lambda
    private static void unirAFNs() throws IOException, InterruptedException {
        if (automatas.size() < 2) {
            System.out.println("Se requieren al menos dos AFNs cargados.");
            pausar();
            return;
        }

        System.out.println("AFNs disponibles:");
        for (String nombre : automatas.keySet()) {
            System.out.println("- " + nombre);
        }

        System.out.print("Nombre del primer AFN: ");
        String nombre1 = scanner.nextLine();
        System.out.print("Nombre del segundo AFN: ");
        String nombre2 = scanner.nextLine();

        AFN afn1 = automatas.get(nombre1);
        AFN afn2 = automatas.get(nombre2);

        if (afn1 == null || afn2 == null) {
            System.out.println("Uno de los AFNs no existe.");
            pausar();
            return;
        }

        // Crear copias para no alterar los originales
        AFN copiaAFN1 = copiarAFN(afn1);
        AFN copiaAFN2 = copiarAFN(afn2);

        // Aplicar unión
        copiaAFN1.unirCon(copiaAFN2);

        String nombreResultado = nombre1 + "_union_" + nombre2;
        automatas.put(nombreResultado, copiaAFN1);

        String salidaDot = "archivos_dot/" + nombreResultado + ".dot";
        String salidaPng = "automatas_png/" + nombreResultado + ".png";
        DotExporter.escribirAFN(copiaAFN1, salidaDot, nombreResultado);
        GraphvizExporter.generarImagen(salidaDot, salidaPng);

        System.out.println("AFN de unión generado como: " + nombreResultado);
        pausar();
    }

    private static void crearAFNDesdeRegex() {
        System.out.print("Ingrese la expresión regular: ");
        String regex = scanner.nextLine().trim();

        try {
            AFN afn = RegexToAFN.build(regex); // validación y construcción
            String nombreAFN = "af" + contadorAFN++;
            automatas.put(nombreAFN, afn);

            // Exportar visualización
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

    public static void usarGrep(){
        System.out.print("Ingrese expresión regular para grep: ");
        String grepRegex = scanner.nextLine();

        List<String> resultados = Grep.grepFromFile(grepRegex, filePath);

        System.out.println("\nLíneas que coinciden:");
        if (resultados.isEmpty()) {
            System.out.println("(ninguna coincidencia)");
        } else {
            for (String linea : resultados) {
                System.out.println(linea);
            }
        }
    }

    // Simula limpieza de pantalla (console clear)
    private static void limpiarPantalla() {
        for (int i = 0; i < 50; i++) System.out.println();
    }
}

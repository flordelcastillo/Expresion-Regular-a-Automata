import java.io.IOException;
import java.util.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static Map<String, AFN> automatas = new LinkedHashMap<>();
    static MenuManager menu = new MenuManager(automatas);

    public static void main(String[] args) throws IOException, InterruptedException {
        int opcion;
        do {
            limpiarPantalla();
            System.out.println("--- Menú de Autómatas Finitos No Deterministas (AFN) ---");
            System.out.println("1. Cargar AFN desde archivo DOT");
            System.out.println("2. Mostrar AFNs cargados (genera DOT + PNG)");
            System.out.println("3. Convertir AFN a AFD");
            System.out.println("4. Crear AFN desde expresión regular");
            System.out.println("5. Ejecutar búsqueda tipo grep");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el nombre del archivo DOT: ");
                    String archivo = scanner.nextLine().trim();
                    menu.cargarAFN(archivo);
                    pausar();
                    break;
                case 2:
                    menu.mostrarAFNs();
                    pausar();
                    break;
                case 3:
                    System.out.print("Nombre del AFN a convertir: ");
                    String nombreAFN = scanner.nextLine().trim();
                    menu.convertirAFNaAFD(nombreAFN);
                    pausar();
                    break;
                case 4:
                    System.out.print("Ingrese la expresión regular: ");
                    String regex = scanner.nextLine().trim();
                    menu.crearAFNDesdeRegex(regex);
                    pausar();
                    break;
                case 5:
                    System.out.print("Ingrese expresión regular para grep: ");
                    String grep = scanner.nextLine().trim();
                    menu.usarGrep(grep);
                    pausar();
                    break;
                case 0:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    pausar();
            }
        } while (opcion != 0);
    }

    private static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.print("Entrada inválida. Ingrese un número: ");
            }
        }
    }

    private static void pausar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private static void limpiarPantalla() {
        for (int i = 0; i < 50; i++) System.out.println();
    }
}


import java.io.IOException;

public class GraphvizExporter {

    public static void generarImagen(String dotPath, String pngPath) throws IOException, InterruptedException {
        // Crear un proceso para ejecutar el comando 'dot' de Graphviz con los argumentos:
        // -Tpng : formato de salida PNG
        // dotPath : archivo de entrada en formato DOT
        // -o pngPath : archivo de salida para la imagen PNG generada
        // Nota: Aquí se especifica la ruta completa del ejecutable dot.exe en Windows.
        ProcessBuilder pb = new ProcessBuilder(
                "C:\\Program Files\\Graphviz\\bin\\dot.exe",
                "-Tpng",
                dotPath,
                "-o",
                pngPath
        );

        // Heredar la entrada/salida del proceso para que se muestren en la consola
        pb.inheritIO();

        // Iniciar el proceso
        Process proceso = pb.start();

        // Esperar a que el proceso termine y capturar su código de salida
        int exitCode = proceso.waitFor();

        // Verificar si la ejecución fue exitosa (exitCode == 0)
        if (exitCode == 0) {
            System.out.println("Imagen PNG generada exitosamente: " + pngPath);
        } else {
            System.err.println("Error al generar la imagen PNG. Código de salida: " + exitCode);
        }
    }
}

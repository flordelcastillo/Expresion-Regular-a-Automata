

import java.io.*;
import java.util.*;

public class Grep {

    public static List<String> grepFromFile(String regex, String filePath) {
        List<String> matchingLines = new ArrayList<>();
        try {
            // Paso 1: Convertir la expresión regular a AFN y luego a AFD
            AFN afn = RegexToAFN.build(regex);
            AFD afd = afn.toAFD();

            // Paso 2: Leer archivo línea por línea
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                // Paso 3: Verificar si la línea es aceptada por el AFD
                if (afd.accepts(line)) {
                    matchingLines.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return matchingLines;
    }
}

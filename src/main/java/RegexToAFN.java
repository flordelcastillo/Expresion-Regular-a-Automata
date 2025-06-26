import java.util.*;

public class RegexToAFN {

    public static AFN build(String regex) {
        // Validación con el parser LL(1)
        Lexer lexer = new Lexer(regex);
        ParserExpReg parser = new ParserExpReg(lexer);
        parser.parse(); // lanza excepción si es inválida

        // Convertir a postfijo
        String postfix = toPostfix(regex);

        // Construir AFN con algoritmo de Thompson
        return buildFromPostfix(postfix);

    }

    // Convertir a postfijo usando Shunting Yard
    private static String toPostfix(String regex) {
        StringBuilder output = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        // Insertar concatenaciones explícitas
        regex = insertConcatenation(regex);

        Map<Character, Integer> precedence = Map.of(
                '*', 3,
                '.', 2,
                '|', 1,
                '(', 0
        );

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                output.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    output.append(stack.pop());
                }
                stack.pop(); // quitar '('
            } else { // operador
                while (!stack.isEmpty() && precedence.get(c) <= precedence.get(stack.peek())) {
                    output.append(stack.pop());
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            output.append(stack.pop());
        }

        return output.toString();
    }

    // Inserta '.' explícitamente como operador de concatenación
    private static String insertConcatenation(String regex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < regex.length(); i++) {
            char c1 = regex.charAt(i);
            result.append(c1);

            if (i + 1 < regex.length()) {
                char c2 = regex.charAt(i + 1);

                if ((Character.isLetterOrDigit(c1) || c1 == '*' || c1 == ')') &&
                        (Character.isLetterOrDigit(c2) || c2 == '(')) {
                    result.append('.');
                }
            }
        }
        return result.toString();
    }

    // Construcción del AFN con algoritmo de Thompson
    private static AFN buildFromPostfix(String postfix) {
        Stack<AFN> stack = new Stack<>();

        for (char c : postfix.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                stack.push(AFN.basico(c));
            } else if (c == '*') {
                AFN a = stack.pop();
                stack.push(AFN.clausuraKleene(a));
            } else if (c == '.') {
                AFN b = stack.pop();
                AFN a = stack.pop();
                stack.push(AFN.concatenacion(a, b));
            } else if (c == '|') {
                AFN b = stack.pop();
                AFN a = stack.pop();
                stack.push(AFN.union(a, b));
            }
        }

        return stack.pop();
    }
}

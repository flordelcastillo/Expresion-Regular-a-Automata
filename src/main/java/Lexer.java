// Analizador léxico: convierte una cadena de entrada en una secuencia de tokens
class Lexer {
    private final String input;
    private int pos = 0;

    Lexer(String input) {
        this.input = input;
    }

    Token nextToken() {
        while (pos < input.length()) { // Se recorre desde una pos inicial hasta el final de la cadena
            char c = input.charAt(pos++);
            switch (c) { // Dependiendo del caracter de la posición actual, se le trata como: Union, Star, Dot, Parent. Izq o Derecho.
                case '|': return new Token(TokenType.UNION, "|");
                case '*': return new Token(TokenType.STAR, "*");
                case '.': return new Token(TokenType.DOT, ".");
                case '(': return new Token(TokenType.LPAREN, "(");
                case ')': return new Token(TokenType.RPAREN, ")");
                default:
                    // En el caso de que sea UN SOLO caracter o digito, o un simbolo especial, lo retorna como tipo CHAR.
                    if (Character.isLetterOrDigit(c) || "!@#$%^&?_-=+<>".indexOf(c) >= 0) {
                        return new Token(TokenType.CHAR, String.valueOf(c));
                    }
                    throw new RuntimeException("Carácter no válido: " + c); // Si no cumple ninguna condición, enviamos error.
            }
        }
        return new Token(TokenType.EOF, ""); // Llega al final de la cadena y termina con el tipo End Of Function.
    }
}
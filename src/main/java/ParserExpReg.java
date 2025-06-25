// Analizador sintáctico: analiza tokens según la gramática LL(1)
class ParserExpReg {
    private final Lexer lexer;
    private Token currentToken;

    ParserExpReg(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }

    // Avanza al siguiente token si coincide con el esperado
    private void eat(TokenType expected) {
        if (currentToken.type == expected) {
            currentToken = lexer.nextToken(); //Invoca el método nextToken para analizar la proxima posicion de la cadena
        } else {
            throw new RuntimeException("Error de sintaxis: se esperaba " + expected + " pero se encontró " + currentToken.type);
        }
    }

    // Acá se ubican el paso a paso del parseo a la expresión algebraica.

    // S → E EOF
    public void parse() {
        parseE();
        if (currentToken.type != TokenType.EOF) {
            throw new RuntimeException("Error de sintaxis: entrada no consumida al final");
        }
    }

    // E → T E'
    private void parseE() {
        parseT();
        parseEPrime();
    }

    // E' → | T E' | ε
    private void parseEPrime() {
        if (currentToken.type == TokenType.UNION) {
            eat(TokenType.UNION);
            parseT();
            parseEPrime();
        }
    }

    // T → F T'
    private void parseT() {
        parseF();
        parseTPrime();
    }

    // T' → . F T' | ε
    private void parseTPrime() {
        if (currentToken.type == TokenType.DOT) {
            eat(TokenType.DOT);
            parseF();
            parseTPrime();
        }
    }

    // F → P F'
    private void parseF() {
        parseP();
        parseFPrime();
    }

    // F' → * F' | ε
    private void parseFPrime() {
        if (currentToken.type == TokenType.STAR) {
            eat(TokenType.STAR);
            // Verificamos que no haya otro * seguido inmediatamente
            if (currentToken.type == TokenType.STAR) {
                throw new RuntimeException("Error de sintaxis: uso inválido de operador * duplicado");
            }
            parseFPrime();
        }
    }

    // P → ( E ) | CHAR
    private void parseP() {
        if (currentToken.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN);
            parseE();
            eat(TokenType.RPAREN);
        } else if (currentToken.type == TokenType.CHAR) {
            eat(TokenType.CHAR);
        } else {
            throw new RuntimeException("Error de sintaxis: se esperaba '(' o un carácter");
        }
    }
}
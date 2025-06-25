// Programa en Java para analizar expresiones regulares usando una gramática LL(1)
// que requiere el uso explícito del operador de concatenación '.' y evita usos inválidos como "a**".

// Tipos de tokens permitidos
enum TokenType {
    CHAR,       // Caracteres alfanuméricos y símbolos especiales
    UNION,      // |
    STAR,       // *
    DOT,        // . (operador de concatenación obligatorio)
    LPAREN,     // (
    RPAREN,     // )
    EOF         // Fin de entrada
}
# Proyecto de Conversión de Expresiones Regulares a Autómatas Finitos

## Descripción

Este proyecto Java convierte expresiones regulares en Autómatas Finitos No Deterministas (AFN), luego los transforma en Autómatas Finitos Deterministas (AFD), y permite visualizar ambos mediante gráficos generados con Graphviz. El sistema incluye un menú interactivo para cargar, manipular y visualizar autómatas.


## Características Principales

🚀 Conversión de expresiones regulares a AFN (usando construcción de Thompson)

🔄 Transformación de AFN a AFD (algoritmo de subconjuntos)

🧩 Operaciones entre autómatas: unión, concatenación, clausura de Kleene

📊 Generación de gráficos (DOT + PNG) para visualización

💾 Carga de autómatas desde archivos DOT

✅ Validación sintáctica de expresiones regulares (gramática LL1)

## Requisitos

- Java JDK 11 o superior

- Graphviz (para generación de imágenes)

- Windows: Instalar desde graphviz.org

Configurar ruta en `GraphvizExporter.java` si es necesario

## Estructura del Proyecto

```text
/proyecto
├── src/
│   ├── AFD.java             # Implementación de Autómata Finito Determinista
│   ├── AFN.java             # Implementación de Autómata Finito No Determinista
│   ├── DotExporter.java     # Exportación a formato DOT
│   ├── DotParser.java       # Lectura de archivos DOT
│   ├── Estado.java          # Representación de estados
│   ├── GraphvizExporter.java# Generador de imágenes PNG
│   ├── Lexer.java           # Analizador léxico para regex
│   ├── Main.java            # Programa principal con menú
│   ├── ParserExpReg.java    # Analizador sintáctico para regex
│   ├── RegexToAFN.java      # Conversión regex → AFN
│   ├── Token.java           # Unidad léxica
│   ├── TokenType.java       # Tipos de tokens
│   └── Transicion.java      # Representación de transiciones
├── archivos_dot/            # Directorio para archivos .dot
├── automatas_png/           # Directorio para imágenes .png
└── README.md                # Este archivo
```

### Flujo de Trabajo

**1.** Cargar AFN desde archivo DOT:

- Colocar archivos DOT en `/archivos_dot`

- Usar opción 1 del menú para cargarlos

**2.** Crear AFN desde expresión regular:

- Usar opción 5 del menú

- Ejemplos válidos:

`a|b` (unión)

`a.b` (concatenación)

`a*` (clausura de Kleene)

`(a|b)*.c`

**3.** Operaciones con autómatas:

- Unión, concatenación y clausura (Opción 3)

- Los resultados se guardan como nuevos autómatas

**4.** Conversión AFN→AFD:

- Seleccionar un AFN cargado (Opción 4)

- El AFD resultante se guarda automáticamente

**5.** Visualización:

- Los archivos .dot y .png se generan automáticamente en:

`/archivos_dot`

`/automatas_png`

- Se pueden abrir con cualquier visor de imágenes

## Ejemplo de Expresiones regulares

| Expresion   | Descripcion |
|-------------|-------------|
|   a.b	      |      Concatenación (a seguido de b)|
|a&#124;b	|Unión (a o b)|
|a*	|Cero o más repeticiones de a|
|(a&#124;b)*.c	|Cero o más a/b seguidos de c|

## Utilización de la función MiniGrep

Ésta función la utilizamos para buscar que una expresión regular ingresada por el usuario 
coincida con una expresión regular en un archivo de texto.

### 🔍 ¿Qué hace paso a paso?

- Recibe una expresión regular (regex) y un path de archivo (filePath).

- Compila la expresión regular.

- Lee línea por línea el archivo indicado.

- Para cada línea, verifica si contiene algo que coincida con la regex.

- Si hay coincidencia, la agrega a una lista de resultados.

- Devuelve esa lista.

## Clases Principales

### Core 

- **AFN:** Implementa operaciones con autómatas no deterministas
- **AFD:** Extiende AFN para autómatas deterministas
- **RegexToAFN:** Convierte regex → AFN usando Thompson 

### Soporte 

- **DotExporter:** Genera representación DOT para visualización
- **DotParser:** Lee autómatas desde archivos DOT
- **GraphvizExporter:** Crea imágenes PNG desde archivos DOT

### Utilidades

- **Estado:** Representa estados simples/compuestos
- **Transicion:** Modela transiciones entre estados
- **Lexer/ParserExpReg:** Validación sintáctica de regex

## Realizado por:

- [@Esteban499](https://github.com/Esteban499)
- [@SantiMolina9](https://github.com/SantiMolina9) 
- [@flordelcastillo](https://github.com/flordelcastillo)
- [@JoelRodriguez174](https://github.com/JoelRodriguez174)

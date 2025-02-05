Compilador para MiniJava
Este proyecto implementa un compilador para MiniJava, una versión simplificada de Java que elimina características avanzadas como genericidad, excepciones y manejo de hilos, y define una sintaxis más estricta. El compilador se desarrolló siguiendo varias etapas, cada una de las cuales amplió la funcionalidad del sistema de análisis y generación de código.

Etapa 1: Análisis Léxico
Objetivo: Identificar los elementos básicos del lenguaje (tokens) a partir del código fuente.
Descripción:
Se definieron las expresiones regulares para cada tipo de token (palabras reservadas, identificadores, literales, etc.) basándose en la sintaxis de MiniJava.
Se construyeron autómatas individuales para reconocer cada token y luego se integraron en un autómata general capaz de escanear el programa fuente y extraer secuencialmente todos los tokens.
Esto permitió transformar el código fuente en una secuencia de tokens, facilitando el posterior análisis sintáctico.

Etapa 2: Análisis Sintáctico
Objetivo: Construir un analizador sintáctico que, utilizando los tokens generados, verifique la estructura del programa conforme a la gramática de MiniJava.
Descripción:
Se diseñó un parser descendente recursivo basado en una gramática modificada para que fuera LL(1).
Para lograrlo, se realizaron transformaciones sobre la gramática original eliminando recursiones a izquierda y factorizando producciones conflictivas, sin perder la capacidad de generar el mismo lenguaje.
Además, se implementó un módulo principal que maneja la interfaz con el usuario, reporta los errores detectados por el análisis léxico y sintáctico, y confirma cuando el análisis es exitoso.

Etapa 3: Chequeo de Declaraciones (Análisis Semántico I)
Objetivo: Construir y consolidar la Tabla de Símbolos (TS) durante el análisis sintáctico y verificar la correcta declaración de todas las entidades del programa.
Descripción:
Se extendió el analizador sintáctico para que, mediante acciones semánticas, construyera una TS global a medida que se reconocían las declaraciones de clases, métodos, atributos, variables locales y parámetros.
Se implementaron funciones que controlan la correcta declaración (por ejemplo, evitando duplicidades) y consolidan la información de la TS.
Este proceso garantiza que cada identificador esté definido y sea único en su ámbito, y que los tipos de datos sean compatibles.

Etapa 4: Chequeo de Sentencias (Análisis Semántico II)
Objetivo: Verificar la correcta utilización de los identificadores en el cuerpo de los métodos y constructores, y comprobar la consistencia de tipos en las sentencias y expresiones.
Descripción:
Se construyeron los Árboles de Sintaxis Abstracta (ASTs) para representar las sentencias y expresiones del programa.
Cada nodo del AST (por ejemplo, nodos para asignación, llamadas a métodos, estructuras de control, etc.) se diseñó como una clase, estableciendo relaciones de herencia para representar las jerarquías (por ejemplo, una expresión binaria es un tipo de expresión).
Se realizaron dos tareas esenciales: la resolución de nombres (vincular cada identificador con su definición en la TS) y la comprobación de tipos (verificar que las operaciones sean semánticamente correctas).
De esta forma, se asegura que el programa sea semánticamente válido antes de proceder a la generación de código.

Etapa 5: Generación de Código
Objetivo: Traducir el AST y la información de la TS en código intermedio para la CEIVM, la máquina virtual diseñada para ejecutar los programas compilados.
Descripción:
Se extendieron los nodos del AST para que cada uno tenga la responsabilidad de generar el código correspondiente a su significado.
El proceso de generación de código consiste en recorrer el AST, y en cada nodo se producen las instrucciones CEIVM adecuadas, como accesos a variables, llamadas a métodos (estáticos y dinámicos), control de flujo (if-else, while, switch) y creación de objetos.
La generación de código depende de haber resuelto previamente los nombres en la TS y de haber comprobado la corrección semántica.
Se implementó un módulo principal que gestiona la interfaz con el usuario, reporta errores de análisis (léxico, sintáctico y semántico) y genera el archivo de salida con el código intermedio listo para ser ejecutado en la CEIVM.

///[SinErrores]
class EjemplosWhile {
    static void main(String args) {
        var contador = 0;

        // Ejemplo de while con llaves
        while (contador < 5) {
            println("Contador con llaves: " + contador);
            contador = contador + 1;
        }

        // Ejemplo de while sin llaves (solo se ejecuta una instrucción)
        while (contador < 10)
            println("Contador sin llaves: " + contador);

        var numero = 10;

        // Ejemplo de while con llaves y condición inversa
        while (numero > 0) {
            println("Número con llaves: " + numero);
            numero = numero - 1;
        }

        // Ejemplo de while sin llaves y condición inversa
        while (numero > -5)
            println("Número sin llaves: " + numero);
    }
}

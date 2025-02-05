///hola&true&false&a&1&hola&true&&exitosamente
//

class A{

    String a3;

    void setall(){
        a3 = "hola";
        System.printSln(a3);
        System.printB(true);
        System.printB(false);
        System.printC('a');
        System.printI(1);
        System.printS("hola");
        System.println();
        System.printBln(true);
        System.printSln("hola");
        System.printIln(1);
        System.printCln('a');
    }


}




class Init{
    static void main()

    {
        var x = new A();
        x.setall();

    }
}
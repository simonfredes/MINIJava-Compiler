///&exitosamente




class A{
    int a1;
    int a2;
    String a3;


    void setall(int p1){
        a1 = p1;
        a2 = a1*2;
        a3 = "hola";
    }

   void m1(){
        a1= 1;
        a2 = 5;

        if (a1 < a2) {
            if (true) {
                debugPrint(3);
            }
            debugPrint(1);
            a1= a1+1;
        }else{
            debugPrint(2);
        }
    }
}


class Init{
    static void main()
    {

        var x = new A();

        x.m1();
    }
}
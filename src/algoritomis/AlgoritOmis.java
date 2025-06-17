package algoritomis;

// creado por Omwekiatl 2025
// clase principal para poner a prueba diversos algoritmos de clases
// con el objetivo de practicar Java

public class AlgoritOmis {

    public static void main(String[] args) {
        System.out.println("Omwekiatl algorithms\n");
        // descomente alguna linea para probar los algoritmos
        
        //AlgoritOmis.speedTest(100000000);
        
        //StringCodify.demo();
        
        //AwtTriangulo.run();
        
        //SimpleLocalCRUD.run();
        
        //Primos.printPrimos(100);
        
        //MultiRusa.ciclo();
        
        //Modularin.mainloop();
    }
    
    public static void speedTest(int ciclos) {
        long tiempo = System.currentTimeMillis();
        for (int i = 0; i < ciclos; i++) {
            // poner aqui el codigo o funciones a ser testeadas
            MultiRusa.multi(248, 213);
        }
        tiempo = System.currentTimeMillis() - tiempo;
        System.out.println("Total: " + Long.toString(tiempo));
        System.out.println("Dt: " + Double.toString(
                (double)tiempo / ciclos));
    }
}

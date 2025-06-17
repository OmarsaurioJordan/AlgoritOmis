package algoritomis;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class Modularin {
    
    public static void mainloop() {
        Scanner scn = new Scanner(System.in);
        System.out.println("Bienvenido a Apacsito by Omwekiatl");
        while(true) {
            System.out.println("\nSe operarán 3 valores!!!");
            ArrayList<Integer> arr = inputArray(scn, 3);
            int suma = sumatoria(arr);
            int multi = multiplicatoria(arr);
            int mayor = mayorvalor(arr);
            salida(suma, multi, mayor);
            System.out.println("\n¿Desea repetir? s/n:");
            if (scn.nextLine().toLowerCase().equals("n")) {
                break;
            }
        }
    }
    
    public static int inputInt(Scanner scn) {
        int res = 0;
        while (res == 0) {
            try {
                res = Integer.parseInt(scn.nextLine());
                if (res < 1) {
                    System.out.println("reinténtalo...");
                    res = 0;
                }
            }
            catch (Exception ex) {
                System.out.println("reinténtalo...");
                res = 0;
            }
        }
        return res;
    }
    
    public static ArrayList<Integer> inputArray(Scanner scn, int total) {
        
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            System.out.println("Digita el valor #" + (i + 1) + ": ");
            arr.add(inputInt(scn));
        }
        return arr;
    }
    
    public static int sumatoria(ArrayList<Integer> arr) {
        int sum = 0;
        for (int n: arr) {
            sum += n;
        }
        return sum;
    }
    
    public static int multiplicatoria(ArrayList<Integer> arr) {
        int mul = 1;
        for (int n: arr) {
            mul *= n;
        }
        return mul;
    }
    
    public static int mayorvalor(ArrayList<Integer> arr) {
        arr.sort(null);
        return arr.get(arr.size() - 1);
    }
    
    public static void salida(int suma, int multi, int mayor) {
        System.out.println("Suma: " + suma);
        System.out.println("Multi: " + multi);
        System.out.println("Mayor: " + mayor);
    }
}

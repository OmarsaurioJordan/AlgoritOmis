package algoritomis;

import java.util.ArrayList;

public abstract class Primos {
    
    public static boolean isPrimo(int n) {
        if (n <= 0) {
            return false;
        }
        float r;
        for (int i = 1; i <= n; i++) {
            r = (float)n / (float)i;
            if (Math.floor(r) == r) {
                if (i != 1 && i != n) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static int[] getPrimos(int p) {
        ArrayList<Integer> primos = new ArrayList<>();
        int i = 1;
        while (primos.size() < p) {
            if (isPrimo(i)) {
                primos.add(i);
            }
            i += 1;
        }
        int[] res = new int[primos.size()];
        for (int r = 0; r < primos.size(); r++) {
            res[r] = primos.get(r);
        }
        return res;
    }
    
    public static void printPrimos(int p) {
        int[] primos = getPrimos(p);
        for (int r = 0; r < primos.length; r++) {
            System.out.print(Integer.toString(primos[r]) + ",");
        }
        System.out.println("");
    }
}

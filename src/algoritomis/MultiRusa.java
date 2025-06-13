package algoritomis;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class MultiRusa {
    
    public static int multiplica(int a, int b, boolean printAll) {
        if (a <= 0 || b <= 0) {
            return 0;
        }
        ArrayList<Integer> rA = new ArrayList<>();
        ArrayList<Integer> rB = new ArrayList<>();
        if (a < b) {
            rA.add(a);
            rB.add(b);
        }
        else {
            rA.add(b);
            rB.add(a);
        }
        while (rA.get(rA.size() - 1) != 1) {
            rA.add(rA.get(rA.size() - 1) / 2);
            rB.add(rB.get(rB.size() - 1) * 2);
        }
        int tot = 0;
        for (int i = 0; i < rA.size(); i++) {
            if (rA.get(i) % 2 != 0) {
                tot += rB.get(i);
            }
        }
        if (printAll) {
            for (int i = 0; i < rA.size(); i++) {
                System.out.print(rA.get(i).toString() + " - " +
                        rB.get(i).toString());
                if (rA.get(i) % 2 != 0) {
                    System.out.println(" +");
                }
                else {
                    System.out.println("");
                }
            }
        }
        return tot;
    }
    
    public static int multi(int a, int b) {
        if (a <= 0 || b <= 0) { return 0; }
        if (a > b) {
            int c = a;
            a = b;
            b = c;
        }
        int tot = 0;
        do {
            if (a % 2 != 0) {
                tot += b;
            }
            a /= 2;
            b *= 2;
        }
        while (a >= 1);
        return tot;
    }
    
    public static void ciclo() {
        Scanner scn = new Scanner(System.in);
        System.out.println("Multiplicación Rusa");
        while (true) {
            System.out.println("...");
            try {
                System.out.println("Digite el número A:");
                int a = Integer.parseInt(scn.nextLine());
                System.out.println("Digite el número B:");
                int b = Integer.parseInt(scn.nextLine());
                int r = MultiRusa.multiplica(a, b, true);
                System.out.println(r);
            }
            catch (Exception ex) {
                System.out.println("Ojo, digite valores válidos");
            }
        }
    }
}

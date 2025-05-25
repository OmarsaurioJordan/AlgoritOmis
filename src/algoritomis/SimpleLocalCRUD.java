package algoritomis;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class SimpleLocalCRUD {    
    
    public static void run() {
        // titulos iniciales
        System.out.println("*** Bienvenido a San Lorenzo de Patias ***");
        System.out.println("(by Omar Jordan Jordan - ADSO24)");

        // objeto que obtiene datos del usuario
        Scanner get = new Scanner(System.in);

        // constantes de evaluacion, pesos para la ponderacion y titulos de ellos
        float pesos[] = {0.15f, 0.15f, 0.2f, 0.2f, 0.3f};
        String examen[] = {
            "parcial 1", "parcial 2", "exposición", "quiz", "exámen final"
        };

        // la cadena que contiene al menu, cada opcion tiene su ID
        String menu = "...\nMenú:";
            menu += "\n1. registrar estudiante";
            menu += "\n2. registrar notas";
            menu += "\n3. información estudiante";
            menu += "\n4. información general";
            menu += "\n5. eliminar estudiante";
            menu += "\n6. eliminar todo";
            menu += "\n7. salir";

        // modelo de informacion de la base de datos, DB
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();
        ArrayList<String> apellidos = new ArrayList<>();
        // cada estudiante tendra un array de 6 notas, 5 crudas + la total
        ArrayList<float[]> notas = new ArrayList<>();

        // variable auxiliares que se usaran en diferentes estados del menu
        String auxs;
        float auxf;
        int auxi;

        // esta variable y while administraran el main loop del software y sus estados
        String select = "0";
        while (!select.equals("7")) {

            // se muestra el menu al usuario y se obtiene un ID seleccionado
            System.out.print(menu + "\n-> ");
            select = get.next();
            switch (select) {

                case "1": // registrar estudiante
                    System.out.print("digite el ID: ");
                    auxs = get.next();
                    auxi = ids.indexOf(auxs);
                    if (auxi == -1) {
                        // como no existe ese ID en la DB se crea estudiante nuevo
                        ids.add(auxs);
                        System.out.print("digite el nombre: ");
                        auxs = get.next();
                        nombres.add(auxs);
                        System.out.print("digite los apellidos: ");
                        auxs = get.next();
                        apellidos.add(auxs);
                        float[] nts = {0f, 0f, 0f, 0f, 0f, 0f};
                        notas.add(nts);
                    }
                    else {
                        // pero si ya existe el ID, no se puede crear uno nuevo
                        // la unica opcion es modificarlo, asi que se indaga por ello
                        System.out.print("desea modificar a " + nombres.get(auxi) +
                            " " + apellidos.get(auxi) + " (s/n): ");
                        auxs = get.next();
                        if (auxs.equals("s") || auxs.equals("S")) {
                            // se procede a hacer las modificaciones
                            System.out.print("digite el nuevo nombre: ");
                            auxs = get.next();
                            nombres.set(auxi, auxs);
                            System.out.print("digite los nuevos apellidos: ");
                            auxs = get.next();
                            apellidos.set(auxi, auxs);
                            // se pregunta si las notas deben eliminarse
                            System.out.print("desea borrar las notas (s/n): ");
                            auxs = get.next();
                            if (auxs.equals("s") || auxs.equals("S")) {
                                // entonces se reestablecen en ceros
                                for (int n = 0; n < 6; n++) {
                                    notas.get(auxi)[n] = 0f;
                                }
                            }
                        }
                    }
                    break;

                case "2": // registrar notas
                    System.out.print("digite el ID: ");
                    auxs = get.next();
                    auxi = ids.indexOf(auxs);
                    if (auxi == -1) {
                        // esto sucede porque el ID no esta en la DB
                        System.out.println("ID no encontrado!");
                    }
                    else {
                        // muestra el nombre del estudiante asociado al ID
                        System.out.println("ID de " + nombres.get(auxi) +
                            " " + apellidos.get(auxi));
                        // la nota total se inicializa en cero
                        notas.get(auxi)[5] = 0f;
                        // se procede a obtener las 5 notas
                        for (int n = 0; n < 5; n++) {
                            System.out.print("digite 0-5 para " + examen[n] + ": ");
                            notas.get(auxi)[n] = Math.min(5f,
                                Math.max(0f, getFloat(get)));
                            // la nota total se halla acumulando la ponderacion
                            notas.get(auxi)[5] += notas.get(auxi)[n] * pesos[n];
                        }
                        // la nota total se redondeara a un decimal
                        notas.get(auxi)[5] = rnd(notas.get(auxi)[5]);
                    }
                    break;
                
                case "3": // info estudiante
                    System.out.print("digite el ID: ");
                    auxs = get.next();
                    auxi = ids.indexOf(auxs);
                    if (auxi == -1) {
                        // esto sucede porque el ID no esta en la DB
                        System.out.println("ID no encontrado!");
                    }
                    else {
                        // muestra el nombre del estudiante asociado al ID
                        System.out.println(nombres.get(auxi) + " " + apellidos.get(auxi));
                        // imprime todas sus 5 calificaciones
                        for (int n = 0; n < 5; n++) {
                            // se obtiene el porcentaje que esa calificacion representa
                            // p = (nota * peso / 5) * 100%
                            auxf = (notas.get(auxi)[n] * pesos[n] / 5f) * 100f;
                            System.out.println(examen[n] + ": " + notas.get(auxi)[n] +
                                " (" + rndp(auxf) + " %)");
                        }
                        // antes de mostrar la nota final se dira si aprobo o no
                        if (notas.get(auxi)[5] >= 3) {
                            System.out.print("Aprobó");
                        }
                        else {
                            System.out.print("Reprobó");
                        }
                        // ahora si mostrar el valor de la nota final
                        System.out.println(" con: " + notas.get(auxi)[5]);
                    }
                    break;
                
                case "4": // info general
                    // mostrar el total de estudiantes
                    System.out.println("hay " + ids.size() + " estudiantes:");
                    // aqui se contaran los aprobados
                    auxi = 0;
                    // para cada estudiante, se dibujara una linea con sus datos
                    for (int e = 0; e < ids.size(); e++) {
                        // comenzando por la letra A de aprobado o sino R
                        if (notas.get(e)[5] >= 3) {
                            System.out.print("A");
                            auxi++;
                        }
                        else {
                            System.out.print("R");
                        }
                        // luego se pone su ID, nombre y nota
                        // X(ID) nombre: nota
                        System.out.println("(" + ids.get(e) + ") " + nombres.get(e) +
                            " " + apellidos.get(e) + ": " + notas.get(e)[5]);
                    }
                    // finalmente se muestra el total de aprobados y su porcentaje
                    auxf = ((float)auxi / Math.max(1, ids.size())) * 100f;
                    System.out.println("aprobaron " + auxi + " (" + rndp(auxf) + " %)");
                    break;

                case "5": // eliminar estudiante
                    System.out.print("digite el ID: ");
                    auxs = get.next();
                    auxi = ids.indexOf(auxs);
                    if (auxi == -1) {
                        // esto sucede porque el ID no esta en la DB
                        System.out.println("ID no encontrado!");
                    }
                    else {
                        // guardar el nombre en una variable para posterior uso
                        auxs = nombres.get(auxi) + " " + apellidos.get(auxi);
                        // eliminar el estudiante de la DB usando su indice
                        ids.remove(auxi);
                        nombres.remove(auxi);
                        apellidos.remove(auxi);
                        notas.remove(auxi);
                        // decir que se elimino, para esto se guardo el nombre en la var
                        System.out.println("se borró a " + auxs + "!");
                    }
                    break;
                
                case "6": // eliminar todo
                    ids.clear();
                    nombres.clear();
                    apellidos.clear();
                    notas.clear();
                    // mostrar mensaje de confirmacion
                    System.out.println("todos los datos eliminados!");
                    break;
                
                case "7": // salir
                    System.out.println("...\nSesión cerrada!");
                    break;
                
                default: // error
                    System.out.println("comando invalido!");
                    break;
            }
        }
        get.close();
    }

    public static float getFloat(Scanner get) {
        // pide un numero flotante al usuario, que sera 0 si se digita algo erroneo
        String num = get.next();
        float res;
        try {
            res = Float.parseFloat(num);
        }
        catch (Exception ex) {
            res = 0f;
        }
        // sera redondeado a un decimal
        return rnd(res);
    }

    public static float rnd(float num) {
        // forzar a que el redondeo tenga un decimal
        return Math.round(num * 10f) / 10f;
    }

    public static int rndp(float num) {
        // para usarlo con los porcentajes, redondeo sin decimales
        return (int)Math.round(num);
    }
}

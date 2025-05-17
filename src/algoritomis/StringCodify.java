package algoritomis;

// creado por Omwekiatl 2025

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public abstract class StringCodify {
    
    /*
    hay 7 bloques de simbolos, cada uno con 16 simbolos (4 bits)
    estan ordenados segun probabilidad de recurrencia, el primero (0)
    son las letras minusculas mas comunes en el espannol, otro (2) son
    los numeros, otro las mayusculas mas comunes del espannol
    
    el formato guarda en 4 bits un header que decide que bloque
    continua y para los mas comunes, cuantas veces se repite, por ejemplo
    el header puede decir que continuan 4 simbolos del bloque 0, entonces
    los siguientes 4 bits son simblos de ese bloque 0, asi se comprime
    la informacion probabilisticamente, para textos estructurados
    
    el sistema de encriptacion simplemente invierte el texto original,
    le agrega datos aleatorios basura, en espaciado oscilatorio, donde
    los datos basura son del bloque 0 para aprovechar la compresion,
    luego se aplica a los datos no basura un desplazamiento dentro de
    la lista de simbolos, por ejemplo, el simbolo R se desplaza a ser N
    notar ademas que esta organizacion no coincide con Unicode ASCII
    
    trabajo futuro: en el header el valor 0 se usa como fin de lectura
    esto es solo para evitar el posible ultimo 4 bits residual, pero es
    espacio perdido, podria utilizarse para codificar otra regla de
    repeticion, y colocando en un posible ultimo 4 bits sobrante un espacio
    */
    
    public static final String MSK_MIN_REC = "eaosrnidlctump, ";
    public static final String MSK_MIN_EXT = "bgvyqhfzjñxkw_;:";
    public static final String MSK_MAY_REC = "EAOSRNIDLCTUMP\"\n";
    public static final String MSK_MAY_EXT = "BGVYQHFZJÑXKW_;:";
    public static final String MSK_NUMEROS = "0123456789.$%-+/";
    public static final String MSK_EXT_ACE = "áéíóúÁÉÍÓÚ()¿?¡!";
    public static final String MSK_EXT_SYM = "[]{}=*|°#'&^<>¬~";
    public static final String[] MSK_ALL = {
        MSK_MIN_REC, MSK_MIN_EXT, MSK_MAY_REC, MSK_MAY_EXT,
        MSK_NUMEROS, MSK_EXT_ACE, MSK_EXT_SYM
    };
    
    public static short[] stringToCode(String texto, boolean compress) {
        // principal algoritmo, es quien codifica y comprime los textos
        // eliminara chars invalidos como son emoticones, etc
        texto = stringPrepare(texto);
        // las arraylist son dinamicas, code es el resultado final
        // historial son los pequennos tramos repetitivos
        ArrayList<Short> code = new ArrayList<>();
        ArrayList<Short> historial = new ArrayList<>();
        char c;
        short[] info; // [ind de MSK_ALL, ind de char en msk]
        short[] anterior = {-1, 0}; // [ind de MSK_ALL, maximos chars]
        // recorre todo el texto generando los codigos para cada simbolo
        for (int i = 0; i < texto.length(); i++) {
            // obtiene la msk y posicion del char actual
            c = texto.charAt(i);
            info = getMskInfo(c);
            // salta el ciclo si no encontro informacion
            if (info[0] == -1) {
                continue;
            }
            // colocar los datos anteriores
            if (anterior[0] != info[0] || historial.size() >= anterior[1]) {
                if (anterior[0] != -1) {
                    code.add(getHeader(anterior[0], (short)historial.size()));
                    for (int k = 0; k < historial.size(); k++) {
                        code.add(historial.get(k));
                    }
                    historial.clear();
                }
            }
            // agregar los nuevos datos
            anterior[0] = info[0];
            historial.add(info[1]);
            if (compress) {
                anterior[1] = getRepeticionMsk(info[0]);
            }
            else {
                anterior[1] = 1;
            }
        }
        // agregar los ultimos datos
        if (anterior[0] != -1) {
            code.add(getHeader(anterior[0], (short)historial.size()));
            for (int k = 0; k < historial.size(); k++) {
                code.add(historial.get(k));
            }
        }
        // el codigo cero marca el final
        code.add((short)0);
        // convertir ArrayList a Array
        short[] result = new short[code.size()];
        for (int i = 0; i < code.size(); i++) {
            result[i] = code.get(i);
        }
        return result;
    }
    
    public static String codeToString(short[] code) {
        // algoritmo de decodificacion de textos comprimidos
        String texto = "";
        String msk;
        short repeticion;
        short header;
        // recorrera todos los numeros del codigo iterativamente
        for (int i = 0; i < code.length; i++) {
            // empieza leyendo el header que indica de que tratan los
            // siguientes simbolos, sea uno o varios (repeticion)
            header = code[i];
            repeticion = getRepeticionInd(header);
            if (repeticion == 0) {
                // 0 indica fin de la lectura
                break;
            }
            // se imprime repetidas veces con la mascara de simbolos
            msk = MSK_ALL[getMskInd(header)];
            for (int c = 0; c < repeticion; c++) {
                i++;
                texto += msk.charAt(code[i]);
            }
        }
        return texto;
    }
    
    public static String codeToPrint(short[] code, String separador) {
        // convierte los numeros de 4 bits en una cadena de texto
        // separada por un simbolo parametrico
        String texto = "";
        if (separador.isEmpty()) {
            separador = " ";
        }
        for (short n: code) {
            texto += separador + n;
        }
        return texto + separador;
    }
    
    public static char[] codeToChars(short[] code) {
        // los codigos obtenidos son valores de 4 bits, pero un buffer
        // guarda bytes (8 bits) lo que se hace aqui es ensamblarlos
        // el buffer tendra la mitad de talla que el array de codigos
        char[] buffer = new char[(int)Math.ceil(code.length / 2.0f)];
        int k = 0;
        // ciclicamente, para un par de valores i del codigo se llena
        // un solo espacio k del buffer, esto se hace desplazando bits
        for (int i = 0; i < code.length; i++) {
            buffer[k] = (char)(code[i] << 4);
            i++;
            if (i >= code.length) {
                break;
            }
            buffer[k] = (char)(buffer[k] | code[i]);
            k++;
        }
        return buffer;
    }
    
    public static char[] stringToChars(String texto) {
        // desglosa una cadena en un array de simbolos (chars)
        char[] buffer = new char[texto.length()];
        texto.getChars(0, texto.length(), buffer, 0);
        return buffer;
    }
    
    public static String charsToPrint(char[] code, String separador) {
        // devuelve una cadena donde los numeros que representan
        // un simbolo char, son mostrados crudamente, separados por simbolo
        String texto = "";
        if (separador.isEmpty()) {
            separador = " ";
        }
        for (char n: code) {
            texto += separador + (short)n;
        }
        return texto + separador;
    }
    
    public static String stringPrepare(String texto) {
        // dado un texto, verifica que cada uno de sus simbolos pertenezca
        // al espacio simbolico de este programa, excluyendo por ejemplo
        // a los emoticones o caracteres especiales
        String result = "";
        String msk_all = String.join("", MSK_ALL);
        char c;
        for (int i = 0; i < texto.length(); i++) {
            c = texto.charAt(i);
            if (msk_all.contains(String.valueOf(c))) {
                result += c;
            }
        }
        return result;
    }
    
    public static String stringEncrypt(String texto) {
        // modifica la cadena para hacerla ilegible
        // primero la pone al revez
        texto = new StringBuilder(texto).reverse().toString();
        // se usara un aleatorio para seleccionar chars de la mascara 0
        Random rnd = new Random();
        int maxRand = MSK_MIN_REC.length();
        // msk_all contiene todos los simbolos en una sola cadena
        String msk_all = String.join("", MSK_ALL);
        String result = "";
        char c;
        int ind;
        short count = 0;
        // recorre todos los chars de el texto original
        for (int i = 0; i < texto.length(); i++) {
            // primero desplaza el char +1 en la lista de simbolos
            c = texto.charAt(i);
            ind = msk_all.indexOf(c);
            if (ind == msk_all.length() - 1) {
                result += msk_all.charAt(0);
            }
            else {
                result += msk_all.charAt(ind + 1);
            }
            // luego agrega simbolos basura, en cantidades crecientes
            // pero oscilantes para no agregar demasiada basura
            // 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1...
            count++;
            for (int r = 0; r < count; r++) {
                result += MSK_MIN_REC.charAt(rnd.nextInt(maxRand));
            }
            if (count >= 4) {
                count = 0;
            }
        }
        return result;
    }
    
    public static String stringDecrypt(String texto) {
        // dada una cadena ilegible le aplica el proceso inverso
        String msk_all = String.join("", MSK_ALL);
        String result = "";
        char c;
        int ind;
        short count = 0;
        // para cada char de la cadena ilegible hara dos cosas
        for (int i = 0; i < texto.length(); i++) {
            // movera el char importante (no basura) a la izquierda -1
            // en la lista de caracteres global
            c = texto.charAt(i);
            ind = msk_all.indexOf(c);
            if (ind == 0) {
                result += msk_all.charAt(msk_all.length() - 1);
            }
            else {
                result += msk_all.charAt(ind - 1);
            }
            // hara el mismo conteo de la encriptacion pero esta vez
            // saltandose esos chars basura, esto adelante el for i++
            count++;
            for (int r = 0; r < count; r++) {
                i++;
            }
            if (count >= 4) {
                count = 0;
            }
        }
        // al final la voltea al revez
        return new StringBuilder(result).reverse().toString();
    }
    
    private static short[] getMskInfo(char simbol) {
        // retorna [ind de MSK_ALL, ind de char en msk]
        // mejor dicho, dado un simbolo lo asocia a una mascara y posicion
        short[] res = {-1, -1};
        String c = String.valueOf(simbol);
        for (int m = 0; m < MSK_ALL.length; m++) {
            if (MSK_ALL[m].contains(c)) {
                res[0] = (short)m;
                int ind = MSK_ALL[m].indexOf(c);
                res[1] = (short)ind;
                return res;
            }
        }
        return res;
    }
    
    private static short getMskInd(short indHeader) {
        // dado el numero del header retorna el ind de la mascara usada
        switch (indHeader) {
            case 0:
                return -1;
            case 1: case 2: case 3: case 4: case 5:
                return 0; // MSK_MIN_REC;
            case 6:
                return 1; // MSK_MIN_EXT;
            case 7: case 8:
                return 2; // MSK_MAY_REC;
            case 9:
                return 3; // MSK_MAY_EXT;
            case 10: case 11: case 12: case 13:
                return 4; // MSK_NUMEROS;
            case 14:
                return 5; // MSK_EXT_ACE;
            case 15:
                return 6; // MSK_EXT_SYM;
        }
        return -1;
    }
    
    private static short getRepeticionInd(short indHeader) {
        // dado el ind del header devuelve las repeticiones esperadas
        switch (indHeader) {
            case 0:
                return 0;
            case 1: case 6: case 7: case 9: case 10: case 14: case 15:
                return 1;
            case 2: case 8: case 11:
                return 2;
            case 3: case 12:
                return 3;
            case 4: case 13:
                return 4;
            case 5:
                return 5;
        }
        return 0;
    }
    
    private static short getRepeticionMsk(short indMsk) {
        // dada una mascara, dice la cantidad maxima de repeticiones
        // que puede tener en su header, la cantidad de veces
        // seguidas que caracteres suyos se representan con un header
        switch (indMsk) {
            case 0: // MSK_MIN_REC
                return 5;
            case 2: // MSK_MAY_REC
                return 2;
            case 4: // MSK_NUMEROS
                return 4;
            case 1: // MSK_MIN_EXT
            case 3: // MSK_MAY_EXT
            case 5: // MSK_EXT_ACE
            case 6: // MSK_EXT_SYM
                return 1;
        }
        return 0;
    }
    
    private static short getHeader(short indMsk, short talla) {
        // dada una mascara y numero de repeticiones (talla) obtiene
        // el ind de header que representa a esa configuracion
        switch (indMsk) {
            case 0: // MSK_MIN_REC
                return talla;
            case 1: // MSK_MIN_EXT
                return 6;
            case 2: // MSK_MAY_REC
                return (short)(6 + talla);
            case 3: // MSK_MAY_EXT
                return 9;
            case 4: // MSK_NUMEROS
                return (short)(9 + talla);
            case 5: // MSK_EXT_ACE
                return 14;
            case 6: // MSK_EXT_SYM
                return 15;
        }
        return 0;
    }
    
    public static void demo() {
        // pone a prueba las capacidades del software, ejemplifica
        // obtener el texto a tratar, si vacio se pondra uno demo
        Scanner sc = new Scanner(System.in);
        System.out.println("...Demo...Str2Cod...\n"
                + "-> escribe algo (vacío demo):");
        String msj = sc.nextLine();
        if (msj.isEmpty()) {
            msj = "El estudio de los primeros pobladores del territorio "
                    + "que hoy comprende la Nación se ha dividido en tres "
                    + "etapas de la época precolombina: el paleolítico "
                    + "(15000-7000 a.C.), el periodo Arcaico Andino "
                    + "(7000 a 2000 a.C.), y el periodo formativo "
                    + "(2000 a.C. hasta el siglo xvi). Los primeros "
                    + "seres humanos que llegaron al territorio datan "
                    + "de aproximadamente 10000 y 15000 años. Los "
                    + "cazadores y recolectores nómadas de esta época "
                    + "utilizaban artefactos líticos, herramientas y "
                    + "armas hechas con piedra que datan de 10450 a.C., "
                    + "hallados en El Abra, donde se comprobó que "
                    + "existían habitantes en la sabana de Bogotá "
                    + "en 10500 a.C.";
        }
        msj = stringPrepare(msj);
        System.out.println("-> texto original:\n" + msj);
        
        // opcionalmente, encriptarlo
        float peso = StringCodify.stringToChars(msj).length;
        System.out.println("-> escribe 1 si deseas encriptar:");
        boolean encrypt = !sc.nextLine().isEmpty();
        String msjc = msj;
        if (encrypt) {
            msjc = stringEncrypt(msj);
            System.out.println("-> texto encriptado:\n" + msjc);
        }
        else {
            System.out.println("-> sin encriptación");
        }
        
        // pinta informacion del mensaje
        char[] crudoOrigi = StringCodify.stringToChars(msjc);
        float estadisticas = crudoOrigi.length;
        String strNumOriginal = StringCodify.charsToPrint(crudoOrigi, "");
        System.out.println("-> valores bytes:\n" + strNumOriginal);
        System.out.println("-> longitud bytes:\n" + crudoOrigi.length);
        
        // convertir a codigo no compacto
        short[] code = StringCodify.stringToCode(msjc, false);
        String codeStr = StringCodify.codeToPrint(code, "");
        System.out.println("...\n-> codificado sin compresión...\n"
                + "-> valores 4 bits:\n" + codeStr);
        System.out.println("-> longitud 4 bits:\n" + code.length);
        
        // convertir a codigo compacto
        code = StringCodify.stringToCode(msjc, true);
        codeStr = StringCodify.codeToPrint(code, "");
        System.out.println("...\n-> codificado con compresión...\n"
                + "-> valores 4 bits:\n" + codeStr);
        System.out.println("-> longitud 4 bits:\n" + code.length);
        
        // pintar informacion de la compresion
        char[] crudoFin = StringCodify.codeToChars(code);
        estadisticas = (crudoFin.length / estadisticas) * 100.0f;
        estadisticas = (float)Math.round(estadisticas * 100.0f) / 100.0f;
        String strNumFinal = StringCodify.charsToPrint(crudoFin, "");
        System.out.println("-> texto final:\n" + new String(crudoFin));
        System.out.println("-> valores bytes:\n" + strNumFinal);
        System.out.println("-> longitud bytes:\n" + crudoFin.length);
        System.out.println("-> nivel de compresión:\n" + estadisticas + " %");
        if (encrypt) {
            peso = (crudoFin.length / peso) * 100.0f;
            peso = (float)Math.round(peso * 100.0f) / 100.0f;
            System.out.println("-> tamaño texto final:\n" + peso + " %");
        }
        
        // demostracion decodificacion
        String reMsj = StringCodify.codeToString(code);
        if (encrypt) {
            reMsj = stringDecrypt(reMsj);
        }
        System.out.println("...\n-> decodificación...\n"
                + "-> texto original:\n" + reMsj);
        String isOk = (reMsj.equals(msj)) ? "Si" : "No";
        System.out.println("-> ¿decodificó bien?\n" + isOk);
        
        // finalizar demo
        System.out.println("-> fin\n...");
        sc.close();
    }
}

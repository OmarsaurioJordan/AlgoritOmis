package algoritomis;

import java.awt.*;
import java.awt.event.*;

/*
Ejemplo de uso sencillo de AWT, donde puedes ingresar tres longitudes
para formar un triangulo, entonces se calculan los angulos y se dibuja
*/

public abstract class AwtTriangulo extends Frame {
    
    public static void run() {
        
        // esta es la fuente de texto que usara toda la GUI
        Font ltr = new Font("Serif", Font.BOLD, 18);
        
        // se creae el frame principal donde se dibujara toda la GUI
        Frame gui = new Frame("Trianguloso");
        gui.setResizable(false);

        // esto es para que se pueda cerrar la ventana al pulsar en la X
        gui.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
        
        // contenedor para los 3 campos de escritura horizontales para los lados
        Panel cjns = new Panel();
        cjns.setLayout(new GridLayout(1, 3));
        TextField lado[] = {
            unCajon(ltr, "lado 1"),
            unCajon(ltr, "lado 2"),
            unCajon(ltr, "lado 3")
        };
        cjns.add(lado[0]);
        cjns.add(lado[1]);
        cjns.add(lado[2]);

        // contenedor para los 3 cuadros de texto horizontales para los angulos
        Panel angls = new Panel();
        angls.setLayout(new GridLayout(1, 3));
        Label ang[] = {
            unTexto(ltr, "A"),
            unTexto(ltr, "B"),
            unTexto(ltr, "C")
        };
        angls.add(ang[0]);
        angls.add(ang[1]);
        angls.add(ang[2]);
        
        // contenedor para la parte superior: titulo, creditos, lados y angulos
        Panel header = new Panel();
        header.setLayout(new GridLayout(5, 1));
        Label titulo = unTexto(ltr, "Trianguloso");
        header.add(titulo);
        Label credits = unTexto(ltr, "Omar Jordan Jordan");
        header.add(credits);
        header.add(cjns);
        header.add(angls);
        
        // agrega al contenedor superior un boton que sera el que haga los calculos
        Button boton = new Button("Calcular");
        boton.setFont(ltr);
        header.add(boton);
        
        // crea un lienzo para dibujar el triangulo
        Panel drw = dibujo();
        
        // contenedor para los dos cuadros de texto horizontales inferiores
        Panel respuestas = new Panel();
        respuestas.setLayout(new GridLayout(1, 2));
        Label resp[] = {
            unTexto(ltr, "Relación"),
            unTexto(ltr, "Inclinacón")
        };
        respuestas.add(resp[0]);
        respuestas.add(resp[1]);
        
        // este metodo se disparara cuando se pulse el boton
        boton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // obtener los datos de los textos editables, ingresados por usuario
                float abc[] = {
                    getFloat(lado[0].getText()),
                    getFloat(lado[1].getText()),
                    getFloat(lado[2].getText())
                };
                // verificar que ninguno sea cero, para evitar errores
                if (abc[0] == 0 || abc[1] == 0 || abc[2] == 0) {
                    resp[0].setText("Relación");
                    resp[1].setText("Inclinacón");
                    // re dibujar el triangulo pero va a hacer el basico
                    reDibujo(drw, abc, 0, 0, 0);
                }
                else {
                    // elegir categoria segun relacion
                    if (abc[0] == abc[1] && abc[0] == abc[2]) {
                        resp[0].setText("Equilatero");
                    }
                    else if (abc[0] == abc[1] || abc[0] == abc[2] ||
                            abc[1] == abc[2]){
                        resp[0].setText("Isósceles");
                    }
                    else {
                        resp[0].setText("Escaleno");
                    }
                    // hallar los angulos
                    int a;
                    int b;
                    int c;
                    try {
                        // A = arccos(( b^2 + c^2 - a^2) / (2 b c))
                        a = (int)Math.toDegrees(Math.acos((Math.pow(abc[1], 2f) +
                            Math.pow(abc[2], 2f) - Math.pow(abc[0], 2f)) /
                            (2f * abc[1] * abc[2])));
                        // B = arccos(( a^2 + c^2 - b^2) / (2 a c))
                        b = (int)Math.toDegrees(Math.acos((Math.pow(abc[0], 2f) +
                            Math.pow(abc[2], 2f) - Math.pow(abc[1], 2f)) /
                            (2f * abc[0] * abc[2])));
                        // C = 180 - A - B
                        c = 180 - a - b;
                    }
                    catch (Exception ex) {
                        a = 0;
                        b = 0;
                        c = 0;
                    }
                    // elegir categoria segun inclinacion
                    if (a == 0 || b == 0 || c == 0) {
                        c = 0;
                        resp[1].setText("Indefinido");
                    }
                    else if (a == 90 || b == 90 || c == 90) {
                        resp[1].setText("Rectángulo");
                    }
                    else if (a < 90 && b < 90 && c < 90) {
                        resp[1].setText("Acutángulo");
                    }
                    else {
                        resp[1].setText("Obtusángulo");
                    }
                    // poner angulos en los labels
                    ang[0].setText(String.valueOf(a) + "°");
                    ang[1].setText(String.valueOf(b) + "°");
                    ang[2].setText(String.valueOf(c) + "°");
                    // re dibujar el triangulo con todos los datos disponibles
                    reDibujo(drw, abc, a, b, c);
                }
            }
        });
        
        // se conectaran todos los contenedores al frame principal
        gui.setLayout(new BorderLayout());
        gui.add(header, BorderLayout.NORTH);
        gui.add(drw, BorderLayout.CENTER);
        gui.add(respuestas, BorderLayout.SOUTH);

        // se le da una talla al frame principal y se muestra
        gui.setSize(300, 400);
        gui.setVisible(true);
    }
    
    // dado un texto, retornara un valor flotante, o sino 0
    public static float getFloat(String txt) {
        float val;
        try {
            val = Math.abs(Float.parseFloat(txt));
        }
        catch (Exception ex) {
            val = 0f;
        }
        return val;
    }
    
    // crea un campo de escritura y luego de parametrizarlo lo devuelve
    public static TextField unCajon(Font f, String txt) {
        TextField c = new TextField();
        c.setFont(f);
        return c;
    }
    
    // crea un cuadro de texto y luego de parametrizarlo lo devuelve
    public static Label unTexto(Font f, String txt) {
        Label t = new Label(txt);
        t.setAlignment(Label.CENTER);
        t.setFont(f);
        return t;
    }
    
    // crear  el componente de dibujo del triangulo
    public static Panel dibujo() {
        Panel lnz = new Panel();
        lnz.setBackground(Color.white);
        float lds[] = {0f, 0f, 0f};
        reDibujo(lnz, lds, 0, 0, 0);
        return lnz;
    }

    // actualiza el dibujo, usando los datos para el triangulo
    public static void reDibujo(Panel pnl, float abc[], int a, int b, int c) {
        // Tarea dibujar
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacionmemoriacaceh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Diego
 */
public class SimulacionMemoriaCaceh {

    //Variables necesarias
    static int tamanoRam = 4096;//bytes
    static int tamanoCache = 1024;//bytes
    static int tamanoBloque = 16;//bytes
    static int tamanoPalabra = 1;//byte
    static int numeroLineas = tamanoCache / tamanoBloque;
    static int numeroBloques = tamanoRam / tamanoBloque;
    static int[] memoriaRam = new int[tamanoRam];
    static int[][] memoriaCache = new int[numeroLineas][tamanoBloque + 3];
    static final int NULL = -1;
    static float tiempo = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        //Cargando RAm
        cargarRam(memoriaRam);
        for (int i = 100; i <= 115; i++) {
            System.out.println(memoriaRam[i]);
        }
        //  System.out.println(leerDirecto(15));
        //  System.out.println(leerSinCache(15));

        System.out.println(tiempo);
        prueba(1);
        System.out.println(tiempo);

        for (int i = 100; i <= 115; i++) {
            System.out.println(memoriaRam[i]);
        }
    }

    public static void iniciarCache(int[][] cache) {
        //Valor 
        for (int i = 0; i < cache.length; i++) {
            for (int j = 0; j < cache.length; j++) {
                cache[i][j] = NULL;
            }
        }
    }

    public static int leerSinCache(int direccion) {
        //Valores calculados
        tiempo += 0.1;
        return memoriaRam[direccion];
    }

    public static void escribirSinCache(int direccion, int valor) {
        try {

            memoriaRam[direccion] = valor;
            tiempo += 0.1;
        } catch (Exception e) {
            System.out.println("Direccion no valida");
        }

    }

    public static int leerDirecto(int direccion) {
        //Valores calculados
        //10 bits totales
        //4 bit para las palabras
        //6 bit para linea 
        //MemoriaCache[linea][0] = valida
        //MemoriaCache[linea][1] = etiqueta
        //MemoriaCache[linea][0] = modificada
        int bloque = direccion / tamanoBloque;
        int linea = bloque % numeroLineas;
        int palabra = direccion % tamanoBloque;
        int etiqueta = bloque / numeroLineas;
//        System.out.println("Direccion: " + direccion);
//        System.out.println("Bloque " + bloque);
//        System.out.println("linea " + linea);
//        System.out.println("palabra " + palabra);
//        System.out.println("etiqueta " + etiqueta);
        if (memoriaCache[linea][0] == 1) {
            if (memoriaCache[linea][1] == etiqueta && memoriaCache[linea][2] == 0) {
                tiempo += 0.01;
                return memoriaCache[linea][palabra + 3];
            } else if (memoriaCache[linea][2] == 1) {
                //Cache a Ram
                int bloqueEsta = memoriaCache[linea][3] / tamanoBloque;
                int direccionEnRam = bloqueEsta * tamanoBloque;

                for (int i = 0; i < tamanoBloque; i++) {
                    memoriaRam[direccionEnRam] = memoriaCache[linea][i + 3];
                    direccionEnRam++;
                }
                //Ram a Cache
                int primeraDireccion = bloque * tamanoBloque;
                for (int i = 0; i < tamanoBloque; i++) {
                    memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                    primeraDireccion++;
                }
                tiempo += 0.22;
                // memoriaCache[linea][1] = etiqueta;
                memoriaCache[linea][2] = 0;
                return memoriaCache[linea][palabra + 3];
            } else {
                //Ram a Cache
                tiempo += 0.11;
                int primeraDireccion = bloque * tamanoBloque;
                for (int i = 0; i < tamanoBloque; i++) {
                    memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                    primeraDireccion++;
                }
                //   memoriaCache[linea][1] = etiqueta;
                memoriaCache[linea][2] = 0;
                return memoriaCache[linea][palabra + 3];

            }
        } else {

            //Ram a Cache
            tiempo += 0.11;
            int primeraDireccion = bloque * tamanoBloque;
            for (int i = 0; i < tamanoBloque; i++) {
                memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                primeraDireccion++;
                System.out.println(primeraDireccion);
            }
            // memoriaCache[linea][1] = etiqueta;
            memoriaCache[linea][0] = 1;
            memoriaCache[linea][2] = 0;
            return memoriaCache[linea][palabra + 3];

        }

    }

    public static int escribirDirecto(int direccion, int valor) {
        //Valores calculados
        //10 bits totales
        //4 bit para las palabras
        //6 bit para linea 
        //MemoriaCache[linea][0] = valida
        //MemoriaCache[linea][1] = etiqueta
        //MemoriaCache[linea][2] = modificada

        int bloque = direccion / tamanoBloque;
        int linea = bloque % numeroLineas;
        int palabra = direccion % tamanoBloque;
        int etiqueta = bloque / numeroLineas;
//        System.out.println("Direccion: " + direccion);
//        System.out.println("Bloque " + bloque);
//        System.out.println("linea " + linea);
//        System.out.println("palabra " + palabra);
//        System.out.println("etiqueta " + etiqueta);
        if (memoriaCache[linea][0] == 1) {
            if (memoriaCache[linea][1] == etiqueta && memoriaCache[linea][2] == 0) {
                tiempo += 0.01;
                //Cambia Valor
                memoriaCache[linea][palabra + 3] = valor;
                //modificado true;
                memoriaCache[linea][2] = 1;
                return NULL;
            } else if (memoriaCache[linea][2] == 1) {
                tiempo += 0.22;
                //Cache a Ram
                int bloqueEsta = memoriaCache[linea][3] / tamanoBloque;
                int direccionEnRam = bloqueEsta * tamanoBloque;
                for (int i = 0; i < tamanoBloque; i++) {
                    memoriaRam[direccionEnRam] = memoriaCache[linea][i + 3];
                    direccionEnRam++;
                }
                //Ram a Cache
                int primeraDireccion = bloque * tamanoBloque;
                for (int i = 0; i < tamanoBloque; i++) {
                    memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                    primeraDireccion++;
                }
                //            memoriaCache[linea][1] = etiqueta;
                memoriaCache[linea][2] = 1;
                memoriaCache[linea][palabra + 3] = valor;
                return NULL;
            } else {
                //Ram a Cache
                tiempo += 0.11;
                int primeraDireccion = bloque * tamanoBloque;
                for (int i = 0; i < tamanoBloque; i++) {
                    memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                    primeraDireccion++;
                }
                //          memoriaCache[linea][1] = etiqueta;
                memoriaCache[linea][2] = 1;
                memoriaCache[linea][palabra + 3] = valor;
                return NULL;
            }
        } else {

            //Ram a Cache
            tiempo += 0.11;
            int primeraDireccion = bloque * tamanoBloque;
            for (int i = 0; i < tamanoBloque; i++) {
                memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                primeraDireccion++;
            }
            //  memoriaCache[linea][1] = etiqueta;
            memoriaCache[linea][0] = 1;
            memoriaCache[linea][2] = 1;
            memoriaCache[linea][palabra + 3] = valor;
            return NULL;

        }

    }

    public static void cargarRam(int[] ram) throws FileNotFoundException, IOException {
        File f = new File("./src/simulacionmemoriacaceh/datos.txt");
        FileReader file = new FileReader(f);
        BufferedReader buff = new BufferedReader(file);
        for (int i = 0; i < tamanoRam; i++) {
            ram[i] = Integer.parseInt(buff.readLine());
        }

        buff.close();
        file.close();

    }

    public static int leer(int direccion, int tipo) {
        System.out.println("Leer");
        if (tipo == 0) {
            return leerSinCache(direccion);

        } else if (tipo == 1) {

            return leerDirecto(direccion);
        } else {
            return 0;
        }
    }

    public static void escribir(int posicion, int tipo, int valor) {
        if (tipo == 0) {
            escribirSinCache(posicion, valor);
        } else if (tipo == 1) {
            System.out.println("Escribir");
            escribirDirecto(posicion, valor);
        }
    }

    public static void prueba(int tipo) {
        escribir(100, tipo, 10);    //En la memoria 100 escribe un 10
        escribir(101, tipo, 13);
        escribir(102, tipo, 21);
        escribir(103, tipo, 11);
        escribir(104, tipo, 67);
        escribir(105, tipo, 43);
        escribir(106, tipo, 9);
        escribir(107, tipo, 11);
        escribir(108, tipo, 19);
        escribir(109, tipo, 23);
        escribir(110, tipo, 32);
        escribir(111, tipo, 54);
        escribir(112, tipo, 98);
        escribir(113, tipo, 7);
        escribir(114, tipo, 13);
        escribir(115, tipo, 1);
        int menor = leer(100, tipo);
        int mayor = menor;
        int a = 0;
        for (int i = 101; i <= 115; i++) {
            a++;
            escribir(615, tipo, a);
            if (leer(i, tipo) < menor) {
                menor = leer(i, tipo);
            }
            if (leer(i, tipo) > mayor) {
                mayor = leer(i, tipo);
            }
        }
        System.out.println("Mayor");
        System.out.println(mayor);

    }

}

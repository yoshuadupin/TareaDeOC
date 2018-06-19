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
import java.util.Scanner;

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
    static int tamanoConjunto = 8;
    static int numeroLineas = tamanoCache / tamanoBloque;
    static int numeroBloques = tamanoRam / tamanoBloque;
    static int[] memoriaRam = new int[tamanoRam];
    static int[][] memoriaCache = new int[numeroLineas][tamanoBloque + 3];
    static final int NULL = -1;
    static double tiempo = 0;
    static int cantidadConjunto = numeroLineas / tamanoConjunto;
    static int[] punterosConjunto = new int[cantidadConjunto];
    static int puntero = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        //Cargando RAm
        iniciarPunteros();
        cargarRam(memoriaRam);
        iniciarCache(memoriaCache);
//        for (int i = 0; i < 100; i++) {
//            System.out.println(memoriaRam[i]);
//        }
        //  System.out.println(leerDirecto(15));
        //  System.out.println(leerSinCache(15));
        double tiempo1 = 0;
        double tiempo2 = 0;
        double tiempo3 = 0;
        double tiempo4 = 0;
        Scanner in = new Scanner(System.in);
        System.out.println("Ingrese N:");
        int input = in.nextInt();
        ordenarNumeros(0, input);
        tiempo1 = tiempo;
        tiempo = 0;
        iniciarCache(memoriaCache);
        cargarRam(memoriaRam);
        ordenarNumeros(1, input);
        tiempo2 = tiempo;
        tiempo = 0;
        iniciarCache(memoriaCache);
        cargarRam(memoriaRam);
        ordenarNumeros(2, input);
        tiempo3 = tiempo;
        tiempo = 0;
        iniciarCache(memoriaCache);
        cargarRam(memoriaRam);
        ordenarNumeros(3, input);
        tiempo4 = tiempo;
        System.out.println(tiempo);
        System.out.println("TIPO        TIEMPO");
        System.out.println("Sin Cache      " + tiempo1);
        System.out.println("Directo        " + tiempo2);
        System.out.println("Asociativa     " + tiempo3);
        System.out.println("Aso.Conjunto   " + tiempo4);

//        vaciarCacheEnRam();
//        for (int i = 0; i < 100; i++) {
//            System.out.println(memoriaRam[i]);
//        }
    }

    public static void iniciarCache(int[][] cache) {
        //Valor 
        for (int i = 0; i < cache.length; i++) {
            for (int j = 0; j < cache[i].length; j++) {
                cache[i][j] = NULL;
            }
        }
    }

    public static void vaciarCacheEnRam() {
        for (int i = 0; i < memoriaCache.length; i++) {
            if (memoriaCache[i][1] != NULL) {
                int linea = memoriaCache[i][1] * tamanoBloque;
                for (int j = 3; j < memoriaCache[i].length; j++) {
                    memoriaRam[linea] = memoriaCache[i][j];
                    linea++;
                }
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

    public static void ordenarNumeros(int tipo, int n) {
        int b, a;
        int cambios = 1;
        int limites = n - 1;
        while (cambios > 0) {
            cambios = 0;
            b = leer(0, tipo);
            for (int i = 0; i < limites; i++) {
                a = leer(i, tipo);
                if (a < b) {
                    escribir(i, tipo, b);
                    escribir(i - 1, tipo, a);
                    a = b;
                    cambios += 1;
                } else {
                    b = a;
                }
            }
            limites = limites - 1;
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
//        System.out.println("Bloque " + bloque);
//        System.out.println("Bloque Linea " + memoriaCache[linea][1]);

        if (memoriaCache[linea][0] == 1) {
            if (memoriaCache[linea][1] == bloque) {
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
                tiempo += 0.21;
                memoriaCache[linea][1] = bloque;
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
                memoriaCache[linea][1] = bloque;
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
            }
            memoriaCache[linea][1] = bloque;
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
//
//        System.out.println("Bloque " + bloque);
//        System.out.println("Bloque Linea " + memoriaCache[linea][1]);

        if (memoriaCache[linea][0] == 1) {
            if (memoriaCache[linea][1] == bloque) {
                tiempo += 0.01;
                //Cambia Valor
                memoriaCache[linea][palabra + 3] = valor;
                //modificado true;
                memoriaCache[linea][2] = 1;
                return NULL;
            } else if (memoriaCache[linea][2] == 1) {
                tiempo += 0.21;
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
                memoriaCache[linea][1] = bloque;
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
            memoriaCache[linea][1] = bloque;
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
        //System.out.println("Leer");
        if (tipo == 0) {
            return leerSinCache(direccion);

        } else if (tipo == 1) {

            return leerDirecto(direccion);
        } else if (tipo == 3) {

            return leerAsociativaConjunto(direccion);
        } else if (tipo == 2) {
            return leerAsociativa(direccion);
        } else {
            return 0;
        }
    }

    public static void escribir(int posicion, int tipo, int valor) {
        if (tipo == 0) {
            escribirSinCache(posicion, valor);
        } else if (tipo == 1) {

            escribirDirecto(posicion, valor);
        } else if (tipo == 3) {

            escribirAsociativaConjunto(posicion, valor);
        } else if (tipo == 2) {
            escribirAsociativa(posicion, valor);

        }
    }

    public static void prueba(int tipo) {
        escribir(100, tipo, 10);    //0.11
        escribir(101, tipo, 13);    //0.01
        escribir(102, tipo, 21);    //0.01
        escribir(103, tipo, 11);    //0.01
        escribir(104, tipo, 67);    //0.01
        escribir(105, tipo, 43);    //0.01
        escribir(106, tipo, 9);     //0.01
        escribir(107, tipo, 11);    //0.01
        escribir(108, tipo, 19);    //0.01
        escribir(109, tipo, 23);    //0.01
        escribir(110, tipo, 32);    //0.01
        escribir(111, tipo, 54);    //0.01
        escribir(112, tipo, 98);    //0.11
        escribir(113, tipo, 7);     //0.01
        escribir(114, tipo, 13);    //0.01
        escribir(115, tipo, 1);     //0.01

        int menor = leer(100, tipo);//0.11 
        int mayor = menor;
        int a = 0;
        int me = 600;
        for (int i = 101; i <= 115; i++) {
            a++;
            escribir(me, tipo, a);//0.11
            if (leer(i, tipo) < menor) {//0.01
                menor = leer(i, tipo);
            }
            if (leer(i, tipo) > mayor) {//0.01
                mayor = leer(i, tipo);
            }
            me += 10;
        }
        System.out.println("Mayor");
        System.out.println(mayor);

    }

    public static void iniciarPunteros() {
        for (int i = 0; i < punterosConjunto.length; i++) {
            punterosConjunto[i] = i * tamanoConjunto;
        }

    }

    public static int leerAsociativaConjunto(int direccion) {
        int bloque = direccion / tamanoBloque;
        int conjunto = bloque % cantidadConjunto;
        int palabra = direccion % tamanoBloque;
        //buscamos la linea asignada para esa direccion si no esta devuelve la primera
        int linea = lineaCache(bloque, conjunto);

        if (memoriaCache[linea][0] == 1) {
            //Esta en cahce
            if (estaEnCache(bloque, conjunto)) {
                tiempo += 0.01;
                return memoriaCache[linea][palabra + 3];
            } else if (memoriaCache[linea][2] == 1) {
                //Cache a Ram
                int bloqueEsta = memoriaCache[linea][1];
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
                tiempo += 0.21;
                memoriaCache[linea][1] = bloque;
                memoriaCache[linea][2] = 0;
                return memoriaCache[linea][palabra + 3];
            } else {
                //Aumentamos el puntero de la siguiente direccion en el conjunto
                //Para poder a escribir alli si fuese necesario
                punterosConjunto[conjunto]++;
                //Se ha pasado del conjunto lo reinicia a su posicion inicial
                if (punterosConjunto[conjunto] % tamanoConjunto == 0) {
                    punterosConjunto[conjunto] -= 8;
                }
                //Ram a Cache
                tiempo += 0.11;
                int primeraDireccion = bloque * tamanoBloque;
                for (int i = 0; i < tamanoBloque; i++) {
                    memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                    primeraDireccion++;
                }
                memoriaCache[linea][1] = bloque;
                memoriaCache[linea][0] = 1;
                memoriaCache[linea][2] = 0;
                return memoriaCache[linea][palabra + 3];

            }

        } else {
            //Aumentamos el puntero de la siguiente direccion en el conjunto
            //Para poder a escribir alli si fuese necesario
            punterosConjunto[conjunto]++;
            //Se ha pasado del conjunto lo reinicia a su posicion inicial
            if (punterosConjunto[conjunto] % tamanoConjunto == 0) {
                punterosConjunto[conjunto] -= 8;
            }
            //Ram a Cache
            tiempo += 0.11;
            int primeraDireccion = bloque * tamanoBloque;
            for (int i = 0; i < tamanoBloque; i++) {
                memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                primeraDireccion++;
            }
            memoriaCache[linea][1] = bloque;
            memoriaCache[linea][0] = 1;
            memoriaCache[linea][2] = 0;
            return memoriaCache[linea][palabra + 3];
        }

    }

    public static int escribirAsociativaConjunto(int direccion, int valor) {
        int bloque = direccion / tamanoBloque;
        int conjunto = bloque % cantidadConjunto;
        int palabra = direccion % tamanoBloque;
        //buscamos la linea asignada para esa direccion si no esta devuelve la primera
        int linea = lineaCache(bloque, conjunto);

        if (memoriaCache[linea][0] == 1) {
            //Esta en cahce
            if (estaEnCache(bloque, conjunto)) {
                tiempo += 0.01;
                memoriaCache[linea][palabra + 3] = valor;
                return memoriaCache[linea][palabra + 3];
            } else if (memoriaCache[linea][2] == 1) {
                //Cache a Ram
                int bloqueEsta = memoriaCache[linea][1];
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
                tiempo += 0.21;
                memoriaCache[linea][1] = bloque;
                memoriaCache[linea][2] = 0;
                memoriaCache[linea][palabra + 3] = valor;
                return memoriaCache[linea][palabra + 3];
            } else {
                //Aumentamos el puntero de la siguiente direccion en el conjunto
                //Para poder a escribir alli si fuese necesario
                punterosConjunto[conjunto]++;
                //Se ha pasado del conjunto lo reinicia a su posicion inicial
                if (punterosConjunto[conjunto] % tamanoConjunto == 0) {
                    punterosConjunto[conjunto] -= 8;
                }
                //Ram a Cache
                tiempo += 0.11;
                int primeraDireccion = bloque * tamanoBloque;
                for (int i = 0; i < tamanoBloque; i++) {
                    memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                    primeraDireccion++;
                }
                memoriaCache[linea][1] = bloque;
                memoriaCache[linea][0] = 1;
                memoriaCache[linea][2] = 0;
                memoriaCache[linea][palabra + 3] = valor;
                return memoriaCache[linea][palabra + 3];

            }

        } else {
            //Aumentamos el puntero de la siguiente direccion en el conjunto
            //Para poder a escribir alli si fuese necesario
            punterosConjunto[conjunto]++;
            //Se ha pasado del conjunto lo reinicia a su posicion inicial
            if (punterosConjunto[conjunto] % tamanoConjunto == 0) {
                punterosConjunto[conjunto] -= 8;
            }
            //Ram a Cache
            tiempo += 0.11;
            int primeraDireccion = bloque * tamanoBloque;
            for (int i = 0; i < tamanoBloque; i++) {
                memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                primeraDireccion++;
            }
            memoriaCache[linea][1] = bloque;
            memoriaCache[linea][0] = 1;
            memoriaCache[linea][2] = 0;
            memoriaCache[linea][palabra + 3] = valor;
            return memoriaCache[linea][palabra + 3];
        }

    }

    public static int lineaCache(int bloque, int conjunto) {
        int lineaCont = conjunto * tamanoConjunto;
        for (int i = 0; i < tamanoConjunto; i++) {
            if (bloque == memoriaCache[lineaCont][1]) {
                return lineaCont;
            }
            lineaCont++;
        }
        return punterosConjunto[conjunto];
    }

    public static boolean estaEnCache(int bloque, int conjunto) {
        int lineaCont = conjunto * tamanoConjunto;
        for (int i = 0; i < tamanoConjunto; i++) {
            if (bloque == memoriaCache[lineaCont][1]) {
                return true;
            }
            lineaCont++;
        }
        return false;
    }

    public static int leerAsociativa(int direccion) {
        int bloque = direccion / tamanoBloque;
        int linea = lineaCacheAsociativa(bloque);
        int palabra = direccion % tamanoBloque;
        if (memoriaCache[linea][0] == 1) {
            if (memoriaCache[linea][1] == bloque) {
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
                tiempo += 0.21;
                memoriaCache[linea][1] = bloque;
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
                memoriaCache[linea][1] = bloque;
                memoriaCache[linea][2] = 0;
                puntero++;
                if (puntero == numeroLineas) {
                    puntero = 0;
                }
                return memoriaCache[linea][palabra + 3];

            }
        } else {

            //Ram a Cache
            tiempo += 0.11;
            int primeraDireccion = bloque * tamanoBloque;
            for (int i = 0; i < tamanoBloque; i++) {
                memoriaCache[linea][i + 3] = memoriaRam[primeraDireccion];
                primeraDireccion++;
            }
            memoriaCache[linea][1] = bloque;
            memoriaCache[linea][0] = 1;
            memoriaCache[linea][2] = 0;
            puntero++;
            if (puntero == numeroLineas) {
                puntero = 0;
            }
            return memoriaCache[linea][palabra + 3];

        }

    }

    public static int escribirAsociativa(int direccion, int valor) {
        int bloque = direccion / tamanoBloque;
        int linea = lineaCacheAsociativa(bloque);
        int palabra = direccion % tamanoBloque;
        if (memoriaCache[linea][0] == 1) {
            if (memoriaCache[linea][1] == bloque) {
                tiempo += 0.01;
                //Cambia Valor
                memoriaCache[linea][palabra + 3] = valor;
                //modificado true;
                memoriaCache[linea][2] = 1;
                return NULL;
            } else if (memoriaCache[linea][2] == 1) {
                tiempo += 0.21;
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
                memoriaCache[linea][1] = bloque;
                memoriaCache[linea][2] = 1;
                memoriaCache[linea][palabra + 3] = valor;
                puntero++;
                if (puntero == numeroLineas) {
                    puntero = 0;
                }
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
            memoriaCache[linea][1] = bloque;
            memoriaCache[linea][0] = 1;
            memoriaCache[linea][2] = 1;
            memoriaCache[linea][palabra + 3] = valor;
            puntero++;
            if (puntero == numeroLineas) {
                puntero = 0;
            }
            return NULL;

        }

    }

    public static boolean estaCacheAsociativa(int bloque) {
        for (int i = 0; i < numeroLineas; i++) {
            if (memoriaCache[i][1] == bloque) {
                return true;
            }
        }
        return false;
    }
    public static int lineaCacheAsociativa(int bloque) {
        for (int i = 0; i < numeroLineas; i++) {
            if (memoriaCache[i][1] == bloque) {
                return i;
            }
        }
        return puntero;
    }

}

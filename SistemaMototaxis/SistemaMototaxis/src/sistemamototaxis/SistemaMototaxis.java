package sistemamototaxis;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Sistema de Gestión de Mototaxis - Chiclayo, Perú
 * Este sistema permite registrar rutas de mototaxis, verificar su legalidad
 * y generar reportes de infracciones basados en zonas restringidas y horarios pico
 */
public class SistemaMototaxis {
    // Scanners para entrada de datos del usuario
    static Scanner entradaNumeros = new Scanner(System.in);
    static Scanner entradaCadenas = new Scanner(System.in);    // Array con todas las zonas disponibles en la ciudad de Chiclayo
    static String zonas[] = {
        "Av. Balta", "Calle San Jose", "Elias Aguirre", "Vicente de la vega",
        "Saenz Penia", "Av Leguia", "Av Chilayo", "Lora y Lora","La Victoria",
        "Mariscal Nieto", "Belaunde", "La Despensa"
    };

    // Array con las zonas restringidas (centro histórico) donde no pueden circular mototaxis
    static String zonasRestringidas[] = {
        "Av. Balta", "Calle San Jose", "Elias Aguirre", "Vicente de la vega", "Saenz Penia"
    };

    // Lista principal que almacena todas las rutas registradas en el sistema
    static ArrayList<Ruta> listaRutas = new ArrayList<>();
   
    
    private static void mostrarBienvenida() {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║        SISTEMA DE GESTIÓN DE MOTOTAXIS    ║");
        System.out.println("║              CHICLAYO - PERÚ              ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println();
    }  

    public static void main(String[] args) {
        mostrarBienvenida();
        int opc;
        do {
            mostrarMenu();
            opc = entradaNumeros.nextInt();
            switch (opc) {
                case 1 -> registrarNuevaRuta();
                case 2 -> mostrarRutasInfractoras();
                case 3 -> registrarMototaxi();
                case 4 -> consultarLegalidadRuta();
                case 5 -> generarReporteInfracciones();
                case 6 -> System.out.println("¡Gracias por usar el sistema!");
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        } while (opc != 6);
    }    

    private static void mostrarMenu() {
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│                MENÚ PRINCIPAL           │");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.println("│ 1. Registrar Ruta                       │");
        System.out.println("│ 2. Verificar Rutas Infractoras          │");
        System.out.println("│ 3. Registrar Mototaxi                   │");
        System.out.println("│ 4. Consultar Legalidad de Ruta          │");
        System.out.println("│ 5. Generar Reporte de Infracciones      │");
        System.out.println("│ 6. Salir                                │");
        System.out.println("└─────────────────────────────────────────┘");
        System.out.print("Seleccione una opción (1-6): ");
    }    
    /**
     * Función principal para registrar una nueva ruta en el sistema
     * Solicita al usuario información sobre el mototaxista, hora y zonas de recorrido
     * Valida los datos ingresados y determina si la ruta es legal o infractora
     * Finalmente almacena la ruta en la lista del sistema
     */
    public static void registrarNuevaRuta() {
        System.out.print("Ingrese el nombre del mototaxista: ");
        String nombre = entradaCadenas.nextLine();

        String horaTexto;
        int horaEnMinutos = -1;
        boolean horaValida = false;

        // Validación de hora en formato correcto
        do {
            System.out.print("Ingrese la hora en formato HH:mm (ejemplo 08:30): ");
            horaTexto = entradaCadenas.nextLine();
            try {
                horaEnMinutos = convertirHorasMinutos(horaTexto);
                horaValida = true;
            } catch (Exception e) {
                System.out.println("Formato de hora inválido. Intente nuevamente.");
            }
        } while (!horaValida);

        // Validación del número mínimo de zonas
        int numeroZonas;
        do {
            System.out.print("Cuántas zonas registrará? (mínimo 3): ");
            numeroZonas = entradaNumeros.nextInt();
        } while (numeroZonas < 3);

        String zonasElegidas[] = elegirZonas(numeroZonas);        // Crear nueva ruta y evaluar su legalidad
        Ruta rutaNueva = new Ruta(nombre, horaEnMinutos, zonasElegidas);
        boolean esLegal = esRutaLegal(rutaNueva);
        rutaNueva.setEstado(esLegal);

        listaRutas.add(rutaNueva);
        System.out.println("\nRuta registrada correctamente:");
        imprimirRuta(rutaNueva);
        
        // Si la ruta es ilegal, mostrar resumen de motivos
        if (!esLegal) {
            mostrarResumenInfracciones(rutaNueva);
        }
    }
    /**
     * Función alternativa para registrar un mototaxi
     * Reutiliza la funcionalidad de registrarNuevaRuta() con un título específico
     */
    public static void registrarMototaxi() {
        System.out.println("\n=== REGISTRO DE MOTOTAXI ===");
        registrarNuevaRuta(); // Utiliza el mismo proceso de registro de ruta
    }    
    /**
     * Convierte una cadena de texto en formato HH:mm a minutos totales desde las 00:00
     * utilzando split y un try-catch para manejar errores de formato
     */
    public static int convertirHorasMinutos(String hora) {
        try {
            String partes[] = hora.split(":");
            if (partes.length != 2) throw new Exception("Formato incorrecto");

            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);

            // Validación de rangos válidos
            if (horas < 0 || horas > 23 || minutos < 0 || minutos > 59) {
                throw new Exception("Hora fuera de rango");
            }

            return horas * 60 + minutos;
        } catch (Exception e) {
            System.out.println("Ingreso invalido. Debe ingresar la hora en formato HH:mm.");
            return -1;
        }
    }    
    /**
     * Permite al usuario seleccionar zonas de recorrido desde una lista predefinida
     * Evita la selección de zonas repetidas y valida las opciones ingresadas
     */
    public static String[] elegirZonas(int numZonas) {
        String zonasElegidas[] = new String[numZonas];
        menuZonas();
        for (int i = 0; i < numZonas; i++) {
            int opc;
            boolean repetida;
            do {
                System.out.print("Seleccione zona " + (i + 1) + ": ");
                opc = entradaNumeros.nextInt();
                repetida = false;

                // Validar que la opción esté en rango válido
                if (opc < 1 || opc > zonas.length) {
                    System.out.println("Opcion invalida.");
                    repetida = true;
                    continue;
                }

                // Verificar que la zona no haya sido seleccionada anteriormente
                for (int j = 0; j < i; j++) {
                    if (zonas[opc - 1].equalsIgnoreCase(zonasElegidas[j])) {
                        System.out.println("Zona repetida. Elija otra.");
                        repetida = true;
                        break;
                    }
                }
            } while (repetida);

            zonasElegidas[i] = zonas[opc - 1];
        }

        return zonasElegidas;
    }    
    /**
     * Muestra la lista numerada de todas las zonas disponibles para seleccionar
     * Facilita al usuario la visualización de opciones antes de hacer su elección
     */
    public static void menuZonas() {
        System.out.println("Zonas disponibles:");
        for (int i = 0; i < zonas.length; i++) {
            System.out.println((i + 1) + ". " + zonas[i]);
        }
    }   
    /**
     * Determina si una ruta es legal basándose en el número de infracciones
     * Una ruta es legal solo si no tiene ninguna infracción
     */
    public static boolean esRutaLegal(Ruta ruta) {
        return contarInfracciones(ruta) == 0;
    }    
    /**
     * Cuenta el número total de infracciones cometidas en una ruta específica
     */
    public static int contarInfracciones(Ruta r) {
        int infracciones = 0;
        
        // Contar infracciones por zonas restringidas
        for (String z : r.zonasRecorridas) {
            for (String zr : zonasRestringidas) {
                if (z.equalsIgnoreCase(zr)) {
                    infracciones++;
                    break;
                }
            }
        }
        
        // Verificar infracciones por horas pico (7-9 AM y 5-7 PM)
        if ((r.getHora() >= 7 * 60 && r.getHora() < 9 * 60) || (r.getHora() >= 17 * 60 && r.getHora() < 19 * 60)) {
            infracciones++;
        }
        return infracciones;
    }    
    /**
     * Imprime los datos completos de una ruta 
     */
    public static void imprimirRuta(Ruta r) {
        System.out.println(r.getDatos());
        System.out.println("--------------------");
    }    
    /**
     * Muestra todas las rutas registradas que han cometido infracciones
     */
    public static void mostrarRutasInfractoras() {
        System.out.println("\n--- Rutas Infractoras ---");
        boolean hay = false;
        for (Ruta r : listaRutas) {
            if (!r.isEstado()) {
                imprimirRuta(r);
                hay = true;
            }
        }
        if (!hay) System.out.println("No hay rutas infractoras registradas.");
    }    
    /**
     * Permite consultar el estado legal de las rutas de un mototaxista en específico
     * Busca por nombre del mototaxista 
     */
    public static void consultarLegalidadRuta() {
        System.out.print("Ingrese el nombre del mototaxista: ");
        String nombre = entradaCadenas.nextLine();
        boolean encontrado = false;
        for (Ruta r : listaRutas) {
            if (r.getNombreMototaxista().equalsIgnoreCase(nombre)) {
                System.out.println("Resultado:");
                imprimirRuta(r);
                encontrado = true;
            }
        }
        if (!encontrado) {
            System.out.println("Mototaxista no encontrado.");
        }
    }    
    /**
     * Muestra todas las rutas registradas que son completamente legales
     * Filtra las rutas por estado (true = legal) y las presenta al usuario
     */
    public static void mostrarRutasLegales() {
        System.out.println("\n--- Rutas Legales Registradas ---");
        boolean hay = false;
        for (Ruta r : listaRutas) {
            if (r.isEstado()) {
                imprimirRuta(r);
                hay = true;
            }
        }
        if (!hay) System.out.println("No hay rutas legales registradas.");
    }    
    
    /**
     * Genera un reporte de infracciones agrupadas por mototaxista
     * Cuenta todas las infracciones de cada conductor y las ordena de mayor a menor
     */
    public static void generarReporteInfracciones() {
        System.out.println("\n--- Reporte de infracciones ---");

        String[] nombres = new String[listaRutas.size()];
        int[] conteo = new int[listaRutas.size()];
        int total = 0;

        // Agrupar infracciones por mototaxista
        for (Ruta r : listaRutas) {
            int inf = contarInfracciones(r);
            if (inf > 0) {
                boolean existe = false;
                // Buscar si el mototaxista ya está en la lista
                for (int i = 0; i < total; i++) {
                    if (nombres[i].equalsIgnoreCase(r.getNombreMototaxista())) {
                        conteo[i] += inf;
                        existe = true;
                        break;
                    }
                }
                // Si es nuevo, agregarlo a la lista
                if (!existe) {
                    nombres[total] = r.getNombreMototaxista();
                    conteo[total] = inf;
                    total++;
                }
            }
        }

        if (total == 0) {
            System.out.println("No se encontraron infracciones registradas.");
            return;
        }

        // Ordenar de mayor a menor número de infracciones
        ordenarInfracciones(nombres, conteo, total);

        // Mostrar el reporte ordenado
        for (int i = 0; i < total; i++) {
            System.out.println((i + 1) + ". " + nombres[i] + " - " + conteo[i] + " infraccion(es)");
        }
    }    

     //Algoritmo de ordenamiento burbuja para organizar las infracciones
  
    public static void ordenarInfracciones(String[] nombres, int[] conteo, int total) {
        for (int i = 0; i < total - 1; i++) {
            for (int j = 0; j < total - 1 - i; j++) {
                if (conteo[j] < conteo[j + 1]) {
                    // Intercambiar conteos
                    int tmp = conteo[j];
                    conteo[j] = conteo[j + 1];
                    conteo[j + 1] = tmp;

                    // Intercambiar nombres correspondientes
                    String tmpNom = nombres[j];
                    nombres[j] = nombres[j + 1];
                    nombres[j + 1] = tmpNom;
                }
            }
        }
    }    /**
     * Muestra un resumen simple de los motivos por los cuales una ruta es ilegal
     * Indica específicamente las zonas restringidas y horarios no permitidos
     * @param ruta La ruta infractora a analizar
     */
    public static void mostrarResumenInfracciones(Ruta ruta) {
        System.out.println("Ruta registrada con infraccion:");
        
        // Verificar infracciones por zonas restringidas
        ArrayList<String> zonasInfractoras = new ArrayList<>();
        for (String zona : ruta.zonasRecorridas) {
            for (String zonaRestringida : zonasRestringidas) {
                if (zona.equalsIgnoreCase(zonaRestringida)) {
                    zonasInfractoras.add(zona);
                    break;
                }
            }
        }
        
        if (!zonasInfractoras.isEmpty()) {
            System.out.print("- Zonas restringidas: ");
            for (int i = 0; i < zonasInfractoras.size(); i++) {
                System.out.print(zonasInfractoras.get(i));
                if (i < zonasInfractoras.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
        
        // Verificar infracciones por horario pico
        int hora = ruta.getHora();
        boolean esHoraPico = (hora >= 7 * 60 && hora < 9 * 60) || (hora >= 17 * 60 && hora < 19 * 60);
        
        if (esHoraPico) {
            int horas = hora / 60;
            int minutos = hora % 60;
            String horaFormateada = (horas < 10 ? "0" : "") + horas + ":" + (minutos < 10 ? "0" : "") + minutos;
            System.out.println(" -Horario no permitido: " + horaFormateada);
        }
        
        System.out.println("Ruta guardada con estado: ILEGAL");
    }
}

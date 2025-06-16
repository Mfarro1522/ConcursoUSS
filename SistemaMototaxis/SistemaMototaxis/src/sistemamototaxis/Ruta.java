package sistemamototaxis;

/**
 * Clase que representa una ruta de mototaxi en el sistema
 * Almacena información sobre el conductor, horario, zonas de recorrido y estado legal
 */
class Ruta {
    private String nombreMototaxista; // Nombre del conductor
    private int hora; // Hora en formato de minutos desde las 00:00
    public String zonasRecorridas[]; // Array de zonas por las que pasa la ruta
    private boolean estado; // true = legal, false = ilegal

   //Constructor para crear una nueva ruta

    public Ruta(String nombreMototaxista, int hora, String[] zonas) {
        this.nombreMototaxista = nombreMototaxista;
        this.hora = hora;
        zonasRecorridas = zonas;
    }    

    //getters y setters
    
    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getNombreMototaxista() {
        return nombreMototaxista;
    }

    public int getHora() {
        return hora;
    }

    
    public boolean isEstado() {
        return estado;
    }    

     // Genera una cadena con todos los datos de la ruta formateados para mostrar
     
    public String getDatos() {
        String msj = estado ? "Legal" : "Ilegal";
        int horas = hora / 60;
        int minutos = hora % 60;
        // Formatear hora con ceros a la izquierda si es necesario
        String horaFormateada = (horas < 10 ? "0" : "") + horas + ":" + (minutos < 10 ? "0" : "") + minutos;
        return "Ruta: \n Mototaxista: " + nombreMototaxista +
               "\n Hora: " + horaFormateada +
               "\n Zonas: " + getZonas() +
               "\n Estado: " + msj;
    }

    
    //Concatena todas las zonas de recorrido en una sola cadena
    
    public String getZonas() {
        String zonas = "";
        for (String z : zonasRecorridas) {
            zonas += z + ", ";
        }
        return zonas;
    }
}
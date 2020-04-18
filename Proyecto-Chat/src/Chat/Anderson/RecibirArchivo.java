package src.Chat.Anderson;

import java.net.*;//UTILIZADA PARA ESTABLECER CONEXION CLIENTE-SERVIDOR-CLIENTE E ENVIO DE MENSAJES Y ARCHIVOS
import java.io.*; //UTILIZADA PARA ESTABLECER UN FLUJO DE DATOS


//------------------------------------------------SE ENCARGA DE CREAR EL DIRECTORIO SI NO EXISTE Y DE RECIBIR LOS ARCHIVOS ENVIADOS---------------------------------------------//
public class RecibirArchivo{

    private ServerSocket servidor = null;
    private int puerto_receptor = 4000;  //INTRODUCIR PUERTO QUE RECIBE LOS ARCHIVOS

    public RecibirArchivo() throws IOException{
        try {
            //----Se valida que exista el directorio comun para anexos en el cliente....
            String sDirectorio = "C:\\anexo_chat\\";
            File f = new File(sDirectorio);

            if (f.exists()) { // Directorio existe
                System.out.println("Directorio existe...");
            } else { //Directorio no existe
                f.mkdir();
                System.out.println("Directorio No existe, se ha creado...");
            }
        }
        catch( Exception e){
            System.out.println("Ha ocurrido un error creando el directorioDirectorio No existe, se ha creado...");
            return;
        }
        // Creamos socket servidor escuchando en el mismo puerto donde se comunica el cliente
        servidor = new ServerSocket( puerto_receptor );
        System.out.println( "Esperando recepcion de archivos..." );
    }

    //-------------------------------METODO ENCARGADO DE RECIBIR LOS ARCHIVOS ENVIADOS-----------------------------//
    public void iniciarServidor(){
        while( true ){
            try{
                // Creamos el socket que atendera el servidor
                Socket cliente = servidor.accept();
                // Creamos flujo de entrada para leer los datos que envia el cliente
                DataInputStream entrada = new DataInputStream( cliente.getInputStream() );
                // Obtenemos el nombre del archivo
                String nombreArchivo = entrada.readUTF().toString();
                // Obtenemos el tamaño del archivo
                int tam = entrada.readInt();
                System.out.println( "Recibiendo archivo "+nombreArchivo );
                // Creamos flujo de salida, este flujo nos sirve para
                // indicar donde guardaremos el archivo
                FileOutputStream salida_archivo = new FileOutputStream( "C:\\anexo_chat\\"+nombreArchivo );
                BufferedOutputStream buffer_salida = new BufferedOutputStream( salida_archivo );
                BufferedInputStream buffer_entrada = new BufferedInputStream( cliente.getInputStream() );
                // Creamos el array de bytes para leer los datos del archivo
                byte[] cantidad_byte = new byte[ tam ];
                // Obtenemos el archivo mediante la lectura de bytes enviados
                for( int i = 0; i < cantidad_byte.length; i++ ){
                	cantidad_byte[ i ] = ( byte )buffer_entrada.read( );
                }
                // Escribimos el archivo
                buffer_salida.write( cantidad_byte );
                // Cerramos flujos
                buffer_salida.flush();
                buffer_entrada.close();
                buffer_salida.close();
                cliente.close();
                System.out.println( "Archivo Recibido "+nombreArchivo );
            }
            catch( Exception e )
            {
                System.out.println( "Recibir: "+e.toString() );
            }
        }
    }
    //--------------------------------------------------------------------------------------------------------------//
}

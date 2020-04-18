package src.Chat.Anderson;

import java.io.IOException;//UTILIZADA PARA LANZAR UNA EXCEPCION RELACIONADA CON LOS FLUJO DE DATOS

public class Main_AppCliente {
	public static void main(String[] args) throws IOException {
		//----------APP DEL CLIENTE-----------//
		AppCliente cliente = new AppCliente();		
		//-----------------------------------//
		
		//----LANZAMOS EL SERVIDOR PARA RESIBIR LOS ARCHIVOS----//
		new RecibirArchivo().iniciarServidor();
		//------------------------------------------------------//
	}
}

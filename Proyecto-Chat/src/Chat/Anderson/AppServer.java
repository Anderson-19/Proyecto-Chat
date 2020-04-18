package src.Chat.Anderson;

import java.awt.Rectangle;   //UTILIZADA PARA CAMBIAR DE DIMENSIONES Y DE POSICIONES A LOS ELEMENTOS DEL FRAME
import java.awt.event.MouseAdapter;//UTILIZADA PARA LOS EVENTOS DE RATON (CLICK)
import java.awt.event.MouseEvent;//UTILIZADA PARA LOS EVENTOS DE RATON (CLICK)
import java.io.IOException;  //UTILIZADA PARA LANZAR UNA EXCEPCION RELACIONADA CON LOS FLUJO DE DATOS
import java.io.*;//--------->//UTILIZADA PARA FLUJO DE DATOS
import java.net.InetAddress; //UTILIZADA PARA CALCULAR LA IP DEL COMPUTADOR DONDE SE EJECUTE
import java.net.ServerSocket;//UTILIZADA PARA ESTABLECER CONEXION CLIENTE-SERVIDOR-CLIENTE
import java.net.Socket;//--->// UTILIZADA PARA EL ENVIO DE MENSAJES Y ARCHIVOS (ESTABLECER CONEXION)
import java.util.ArrayList;  //UTILIZADA PARA IMPLEMENTAR EL JLIST
import java.util.Calendar;   //UTILIZADO PARA CALCULAR FECHA Y HORA
import java.sql.*;//-------->//UTILIZADA PARA BASE DE DATOS
import javax.swing.*;//----->//UTILIZADA PARA LA INTERFAZ GRAFICA
import src.Chat.Anderson.AppCliente.AppCliente_UI.paquete_Envio;


//--------------------------------------SE ENCARGA DE MOSTRAR LA INTERFAZ DEL SERVIDOR Y CONTIENE TODOS SUS PROCESOS--------------------------//
public class AppServer extends JFrame implements Runnable {
	
	//---------VARIABLE IMPORTANTES----------//
	private String localhost = "localhost";	//INTRODUCIR SU LOCALHOST
	private String pass_postgre = "contraseña de postgre";   //INTRODUCIR SU CONTRASEÑA
	private int puerto_emisor = 8080;         	//INTRODUCIR PUERTO ENVIA MENSAJES
	private int puerto_receptor = 3000;       	//INTRODUCIR PUERTO QUE RECIBE LOS MENSAJES
	//--------------------------------------//
	
	private JTextArea mensaje_texto;
	private JLabel label1;
	private JScrollPane sp1;
	private JButton boton;

	public AppServer() {
		super("Servidor");
		setLayout(null);
		
		label1 = new JLabel("Conversacion");
		label1.setBounds(new Rectangle(10,5,100,50));
		label1.setVisible(true);
		add(label1);
		
		mensaje_texto = new JTextArea();
		mensaje_texto.setBounds(new Rectangle(10,40,550,350));
		mensaje_texto.setVisible(true);
		add(mensaje_texto);
		
		sp1 = new JScrollPane(mensaje_texto);
		sp1.setBounds(new Rectangle(10,40,550,350));
		sp1.setVisible(true);
		add(sp1);
		
		boton = new JButton("Salir");
		boton.setBounds(new Rectangle(480,400,80,20));
		boton.setVisible(true);
		add(boton);
		boton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				System.exit(0);
			}
		});
		
		Thread hilo = new Thread(this);
		hilo.start();
	}
	

	//-----------------------METODO QUE SE ENCARGA DE RECIBIR Y ENVIAR MENSAJES AL CLIENTE------------------------//
	@Override
	public void run() {
		try {	
			String nick, ip, mensaje;
			String xorigen, xiporigen, xdestino, xipdestino, xtipo, xcontenido, xfecha, xhora;
			ArrayList<String> listaIp = new	ArrayList<String>();
			ArrayList<String> list_activos = new	ArrayList<String>();
			paquete_Envio mensaje_recibido = null;
			while(true){
				//---------Poner A La Aplicacion A la Escucha--------------//
				ServerSocket server = new ServerSocket(puerto_receptor);
		        //Socket Que Recibe Datos
				Socket conexion = server.accept();
				//------------------------Recepcion De Paquete----------------------------------//
				ObjectInputStream Paquete_datos = new ObjectInputStream(conexion.getInputStream());
				mensaje_recibido = (paquete_Envio)Paquete_datos.readObject();
				nick =  mensaje_recibido.getNick();
				ip = mensaje_recibido.getIp();
				mensaje = mensaje_recibido.getMensaje();
				xorigen = mensaje_recibido.getOrigen();
				xiporigen = mensaje_recibido.getIporigen();
				xdestino = mensaje_recibido.getDestino();
				xipdestino = mensaje_recibido.getIpdestino();
				xtipo = mensaje_recibido.getTipo();
				xcontenido = mensaje_recibido.getContenido();
				xfecha = mensaje_recibido.getFecha();
				xhora = mensaje_recibido.getHora();
				String xdata = xorigen+"^"+xiporigen+"^"+xdestino+"^"+xipdestino+"^"+xtipo+"^"+xcontenido+"^"+xfecha+"^"+xhora;
				System.out.println("======================>La Data es: "+xdata);
				//--------------------Para Saber Si Es La primera Vez Que Se conecta---------------------//
				if(mensaje.equals("IniciarSesion")){
					//-------------Detectar Ip-------------------------------//
					InetAddress localizacion = conexion.getInetAddress();
					String IP = localizacion.getHostAddress();
					System.out.println("Direcion IP: "+IP+"nombre: "+nick);
					listaIp.add(IP);
					String nickip = nick+":"+IP;
					list_activos.add(nickip);
					mensaje_recibido.setIps(listaIp);
					mensaje_recibido.setList_Activos(list_activos);

					for(String direccion : listaIp){
						//System.out.println("Arreglo: "+direccion);
						//-------------------------Socket Que Envia Datos------------------------//
						Socket envio = new Socket(direccion,puerto_emisor);
						ObjectOutputStream paquete_envio = new ObjectOutputStream(envio.getOutputStream());
						paquete_envio.writeObject(mensaje_recibido);
						paquete_envio.close();
						mensaje_texto.append("\n"+"BroadCast Dice: "+mensaje+"\n"+"De: "+nick+"\n"+"IP: "+direccion);
						//---------------------Cierre Del Socket que envia datoss-------------//
						envio.close();
						//----------------Cierre Del Socket que  recibe datos-----------------//
						server.close();
					}
				}else{
					InetAddress localizacion = conexion.getInetAddress();
					String xipo = localizacion.getHostAddress();
					xiporigen = xipo;
					mensaje_texto.append("\n"+"@@@@Dice: "+mensaje+"..."+"De: "+nick+"..."+"IP: "+xipo);
					//System.out.print("Lo que ira a BD:"+xorigen+"-"+xdestino+"-"+xiporigen+"-"+xipdestino+"-"+ xtipo+"-"+xcontenido+"-"+ xfecha+"-"+xhora);
					this.insertarBaseDatos(xorigen, xdestino, xiporigen, xipdestino, xtipo, xcontenido, xfecha, xhora);
					//-------------------------Socket Que Envia Datos------------------------
					Socket envio = new Socket(xipdestino,puerto_emisor);
					ObjectOutputStream paquete_envio = new ObjectOutputStream(envio.getOutputStream());
					paquete_envio.writeObject(mensaje_recibido);
					paquete_envio.close();
					//--Cierre Del Socket que envia datos------------
					envio.close();
					//---Cierre Del Socket que  recibe datos-------------
					server.close();
				}
			}

		} catch (IOException | ClassNotFoundException | SQLException e) {

				System.out.println(e.getMessage());
			}
		}
	//---------------------------------------------------------------------------------------------------------------------------------//
	
	
			//---------CALCULA LA FECHA EN LA QUE SE ENVIO EL MENSAJE O ARCHIVO----------------//
			public String calcularFecha(){
				int dd, mm;
				String dia, mes, ano;
				Calendar c = Calendar.getInstance();
				dd = c.get(Calendar.DATE);
				mm = c.get(Calendar.MONTH)+1;
				if(dd < 10){dia = "0"+Integer.toString(c.get(Calendar.DATE));}else{dia = Integer.toString(c.get(Calendar.DATE));}
				if(mm < 10){mes = "0"+Integer.toString(c.get(Calendar.MONTH)+1);}else{mes = Integer.toString(c.get(Calendar.MONTH)+1);}
				ano = Integer.toString(c.get(Calendar.YEAR));
				return dia+"/"+mes+"/"+ano;
			}	
			//----------------------------------------------------------------------------------//

			//---------CALCULA LA HORA EN LA QUE SE ENVIO EL MENSAJE O ARCHIVO----------------//
			public String calcularHora(){
				int hh,min, seg, ap;
				String hr,mm, sg;
				String ampm, horatotal ;
				Calendar calendario = Calendar.getInstance();
				hh = calendario.get(Calendar.HOUR);
				min = calendario.get(Calendar.MINUTE);
				seg = calendario.get(Calendar.SECOND);
				ap = calendario.get(Calendar.AM_PM);
				if(ap == 1){  ampm = "PM";}else{  ampm = "AM";	}
				if(hh < 10){  hr = "0"+Integer.toString(hh);}else{  hr = Integer.toString(hh);	}
				if(min < 10){ mm = "0"+Integer.toString(min);}else{	mm = Integer.toString(min);	}
				if(seg <10){sg = "0"+Integer.toString (seg);}else{	sg = Integer.toString (seg);}
				horatotal = hr+":"+mm+":"+sg+" "+ampm;
				return horatotal;
			}	
			//---------------------------------------------------------------------------------//

			//----------------------------------INSERTA EN BASE DE DATOS-----------------------------------//
			public void insertarBaseDatos(String v1,String v2,String v3,String v4,String v5,String v6,String v7,String v8) throws SQLException{
				final String url = "jdbc:postgresql://localhost:5432/chat";
				final String user = "postgres";
				final String password = pass_postgre;
				Connection conn = null;
				try {
					conn = DriverManager.getConnection(url, user, password);
					System.out.println("Conectado a PostgreSQL exitosamente");
					Statement st = conn.createStatement();
					st.execute("INSERT INTO infochat VALUES('"+v1+"','"+v2+"','"+v3+"','"+v4+"','"+v5+"','"+v6+"','"+v7+"','"+v8+"')");
					System.out.println("Se insertaron exitosamente los datos");
					conn.close();
				}
				catch (SQLException e) {
					System.out.println(e.getMessage());
					System.out.println("No se pudo conectar a PostgreSQL");
				}
			}		
			//---------------------------------------------------------------------------------------------//
}

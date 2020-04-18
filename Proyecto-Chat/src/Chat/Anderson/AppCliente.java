package src.Chat.Anderson;

import java.awt.event.*;//-------------->//UTILIZADA PARA LOS EVENTOS QUE SE PRESENTAN EN EL PROYECTO
import java.io.*;//--------------------->//UTILIZADA PARA ESTABLECER UN FLUJO DE DATOS
import java.net.InetAddress;//---------->//UTILIZADA PARA CALCULAR LA IP DEL COMPUTADOR DONDE SE EJECUTE
import java.net.ServerSocket;//--------->//UTILIZADA PARA ESTABLECER CONEXION CLIENTE-SERVIDOR-CLIENTE 
import java.net.Socket;//--------------->//UTILIZADA PARA EL ENVIO DE MENSAJES Y ARCHIVOS (ESTABLECER CONEXION)
import java.net.UnknownHostException;//->//UTILIZADA PARA CAPTURAR LAS EXCEPCIONES QUE PRODUCEN LOS HOST
import java.nio.file.Files;//----------->//UTILIZADA PARA SOBREESCRIBIR EL ARCHIVO DE DESTINO SI EXISTE Y LO COPIA
import java.nio.file.Path; //----------->//UTILIZADA PARA MOSTRAR LA DIRECCION A LA QUE SE ENVIAR EL ARCHIVO
import java.nio.file.Paths;//----------->//UTILIZADA PARA MOSTRAR LA DIRECCION A LA QUE SE ENVIAR EL ARCHIVO
import java.nio.file.StandardCopyOption; //UTILIZADA PARA REMPLAZAR O CREAR EL ARCHIVO DE DESTINO SI EXISTE Y LO COPIA
import java.sql.*;//-------------------->//UTILIZADA PARA BASE DE DATOS
import java.util.ArrayList;//----------->//UTILIZADA PARA IMPLEMENTAR EL JLIST
import java.util.Calendar;//------------>//UTILIZADO PARA CALCULAR FECHA Y HORA

//-------------->//UTILIZADA PARA VISUALIZAR LAS CONVERSACIONES ENTRE CLIENTES EN LA APP CLIENTE 
//----------------------------------------------------------GUI--------------------------------------------------------------------//
import javax.swing.*;//---------------------->//UTILIZADA PARA APLICAR TODOS LOS ELEMENTOS DE JAVA SWING VENTANAS, ICONOS, BOTONES, BORDES, LISTAS ETC..
import java.awt.*;//------------------------->//UTILIZADA PARA CAMBIAR DE DIMENSIONES Y DE POSICIONES A LOS ELEMENTOS DEL FRAME

import javax.swing.text.BadLocationException; //LANZA UNA EXCEPTION RELACIONADA CON EL JTEXTPANE
import javax.swing.text.SimpleAttributeSet;   //UTILIZADA PARA OTORGAR ATRIBUTOS A LAS FRASES
import javax.swing.text.StyleConstants;//---->//UTILIZADA PARA EL ESTILO DE LAS LETRAS
import javax.swing.border.Border;//---------->//UTILIZADA PARA APLICAR BORDES
//----------------------------------------------------------------------------------------------------------------------------------//

//-------------------SE ENCARGA DE MOSTRAR LA INTERFAZ DEL LOGIN Y CONTIENE LA INTERFAZ DEL CHAT Y TODOS SUS RUTINAS-----------------------//
public class AppCliente extends JFrame {
		
		//---VARIABLE DE IMPORTANCIA---//
		private String localhost = "localhost";//INTRODUCIR SU LOCALHOST
		private String pass_postgre = "contraseña de postgre";  //INTRODUCIR SU CONTRASEÑA
		private int puerto_mensaje = 3000;         //INTRODUCIR PUERTO POR DONDE SE ENVIARAN LOS MENSAJES
		private int puerto_archivo = 4000;         //INTRODUCIR PUERTO POR DONDE SE ENVIARAN LOS ARCHIVOS
		private int puerto_receptor = 8080;        //INTRODUCIR PUERTO POR DONDE SE RECIBIRAN LOS MENSAJES
		//-----------------------------//
    	private JFrame ventana;
    	protected JTextField texto;
    	private JLabel user;
    	private JButton ok;
    	private JButton cancelar;

		public AppCliente(){//--Frame de logueo o Inicio de Sesion...

			this.ventana = new JFrame("Login");
			this.ventana.setSize(330, 140);
			this.ventana.setLayout(null);
			this.ventana.setLocationRelativeTo(ventana);
			
			this.user = new JLabel("Usuario: ");
			this.user.setBounds(10, 35, 100, 10);
			this.user.setVisible(true);		
		
			this.texto = new JTextField();
			this.texto.setBounds(new Rectangle(60,30,200,25));
			this.texto.setVisible(true);					
			
			this.ok = new JButton("Ok");
			this.ok.setBounds(new Rectangle(70,70,75,20));
			this.ok.setVisible(true);			
			this.ok.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){		
					if(texto.getText().length() == 0){
						JOptionPane.showMessageDialog(null,"Nombre De Usuario No Valido...!","ERROR",JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					AppCliente_UI IC = new AppCliente_UI();
			    	IC.setSize(640, 560);
			    	IC.setLocationRelativeTo(IC);
					IC.setVisible(true);
			    	ventana.dispose();
				}
					
			});
			
			this.cancelar = new JButton("Cancelar");
			this.cancelar.setBounds(new Rectangle(165,70,85,20));
			this.cancelar.setVisible(true);			
			this.cancelar.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					System.exit(0);
				}
			});
			//---------AGREGANDO A LA VENTANA-----------//
			this.ventana.add(user);
			this.ventana.add(texto);
			this.ventana.add(ok);
			this.ventana.add(cancelar);
			this.ventana.setVisible(true);
			//-----------------------------------------//
       }
    
	//----------------------SE ENCARGA DE MOSTRAR LA INTERFAZ DEL CHAT Y TODOS SUS EVENTOS---------------------//
    class AppCliente_UI extends JFrame implements Runnable, Serializable{

    	private JLabel charla1;
    	private JTextPane editor;
		private JScrollPane scrollPane;
    	private JScrollPane sc2;
    	private JLabel cont;
    	private JList<String> contactos;
    	private JComboBox<String> ip;
    	private JTextField mensaje;
    	private JButton enviar;
    	private JButton anexar;
    	private JButton BD;
    	private JLabel user,nick;
		DefaultListModel model = new DefaultListModel();
		private String xori,  xipo;
		private String xdes, xipd = "";
		private String[] xlist;
		//---JFileChooser
		private JTextField JTFileSel;
		private JButton JBFileSel;
		private JLabel JLFileSel;

		public AppCliente_UI(){
    		super("Chat");
    		setLayout(null);

    		charla1 = new JLabel("Conversacion");
    		charla1.setBounds(new Rectangle(10,5,100,20));
    		charla1.setVisible(true);
    		add(charla1);

    		//----------------JtextPane----------------//
			editor = new JTextPane();
			editor.setBounds(new Rectangle(10,25,370,350));
			editor.setVisible(true);
			add(editor);
			scrollPane = new JScrollPane(editor);
			scrollPane.setBounds(new Rectangle(10,25,370,350));
			scrollPane.setVisible(true);
			scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			add(scrollPane, BorderLayout.CENTER);
			//-----------------------------------------//
    		
    		cont = new JLabel("Contactos");
    		cont.setBounds(new Rectangle(390,5,100,20));
    		cont.setVisible(true);
    		add(cont);
    		
    		contactos = new JList<String>();
    		contactos.setBounds(new Rectangle(390,25,180,350));
    		contactos.setVisible(true);
    		add(contactos);

    		sc2 = new JScrollPane(contactos);
     	    sc2.setBounds(390,25,180,350);
     		sc2.setVisible(true);
     		add(sc2);

			contactos.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					JList list = (JList)evt.getSource();
					if (evt.getClickCount() == 2) {
						// Double-click detected
						int index = list.locationToIndex(evt.getPoint());
						System.out.println("Seleccione la opcion:"+index+" del Jlist Contactos..."+contactos.getSelectedValue());
						String cadena = contactos.getSelectedValue();
						xlist =  cadena.split(":");
						xori = nick.getText();
						xipo = "";
						xdes = xlist[0];
						xipd = xlist[1];
						try {
							leerBaseDatos(xori, xdes);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			});

    		mensaje = new JTextField();
    		mensaje.setBounds(new Rectangle(10,385,370,50));
    		mensaje.setVisible(true);
    		add(mensaje);
    		
    		enviar = new JButton("Enviar");
    		enviar.setBounds(new Rectangle(10,445,100,20));
    		enviar.setVisible(true);
    		envioMensaje envio = new envioMensaje();
    		add(enviar);
			enviar.addActionListener(envio);
   		
    		anexar = new JButton("Anexar");
    		anexar.setBounds(new Rectangle(145,445,100,20));
    		anexar.setVisible(true);
    		add(anexar);

			anexar.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					enviarAnexo();
				}
			});
    		
    		BD = new JButton("Salir");
    		BD.setBounds(new Rectangle(280,445,100,20));
    		BD.setVisible(true);
    		add(BD);
    		BD.addMouseListener(new MouseAdapter(){
    			public void mouseClicked(MouseEvent e){
    				System.exit(0);
    			}
    		});
    		
    		user = new JLabel("Usuario:");
    		user.setBounds(new Rectangle(390,380,100,20));
    		user.setVisible(true);
    		add(user);
    		
    		nick = new JLabel();
    		nick.setBounds(new Rectangle(440,380,100,20));
    		nick.setVisible(true);
    		nick.setText(texto.getText());
    		add(nick);
    		
    		ip = new JComboBox<String>();
    		 		
    		//--------------GUI para el JFileChooser
			JTFileSel = new JTextField();
			JTFileSel.setBounds(new Rectangle(10,480,370,20));
			JTFileSel.setVisible(true);
			add(JTFileSel);

			JBFileSel = new JButton("Archivos");
			JBFileSel.setBounds(new Rectangle(390,480,100,20));
			JBFileSel.setVisible(true);
			add(JBFileSel);
			JBFileSel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					seleccionarArchivo();
				}
			});

			JLFileSel = new JLabel();
			JLFileSel.setBounds(new Rectangle(10,500,400,20));
			JLFileSel.setVisible(true);
			add(JLFileSel);

    		Thread hilo = new Thread(this);
    		hilo.start();
    		
    		addWindowListener(new EnvioSenal());
    			
    	}
    	
    	//--------------------------ENVIA UNA SEÑAL DE CONEXION-------------------------------------//
         public class EnvioSenal extends WindowAdapter{
    		public void windowOpened(WindowEvent e){
    			try {
    				Socket conexion = new Socket(localhost, puerto_mensaje);
    				paquete_Envio envio_datos = new paquete_Envio();
    				envio_datos.nick = nick.getText();
    				envio_datos.setMensaje("IniciarSesion");
    				ObjectOutputStream paquete_datos = new ObjectOutputStream(conexion.getOutputStream());
    				paquete_datos.writeObject(envio_datos);
    				conexion.close();
    			} catch (Exception e1) {
    				System.out.print(e1.getMessage());
    			}
    		}
    	} 	
        //------------------------------------------------------------------------------------------//
         
        
      //-------------------------DATOS QUE SE ENVIARAN AL SERVIDOR (GETTERS Y SETTERS)--------------------------//
    	public class paquete_Envio  implements Serializable{
    	  private String nick, ip, mensaje;
		  private String origen, iporigen, destino, ipdestino, tipo, contenido, fecha, hora;
		  private ArrayList<String> Ips;
		  private ArrayList<String> List_Activos;
		  //--------------------------------GETTERS--------------------------------//
		  public String getOrigen() 				 {  return origen;	  		}
		  public String getIporigen() 				 {  return iporigen;		}
		  public String getDestino() 				 {  return destino;		  	}
		  public String getIpdestino() 			   	 {  return ipdestino;		}
		  public String getTipo() 					 {  return tipo;		  	}
		  public String getContenido() 				 {  return contenido;		}
		  public String getFecha() 					 {  return fecha;		  	}
		  public String getHora() 					 {  return hora;			}
		  public ArrayList<String> getList_Activos() {  return List_Activos; 	}
		  public ArrayList<String> getIps() 		 {  return Ips;				}
		  public String getNick() 					 {  return nick;			}
		  public String getIp() 					 {return ip;				}
		  public String getMensaje() 				 {return mensaje;			}
		  //----------------------------------------------------------------------//

		  //----------------------------SETTERS------------------------------------//
		  public void setOrigen(String origen) 						  {  this.origen = origen;		}
		  public void setIporigen(String iporigen) 					  {  this.iporigen = iporigen;	}
		  public void setDestino(String destino) 					  {  this.destino = destino;	}
		  public void setIpdestino(String ipdestino)				  {  this.ipdestino = ipdestino;}
		  public void setTipo(String tipo) 							  {  this.tipo = tipo;		    }
		  public void setContenido(String contenido)				  {  this.contenido = contenido;}
		  public void setFecha(String fecha) 						  {  this.fecha = fecha;		}
		  public void setHora(String hora) 							  {  this.hora = hora;		    }
		  public void setList_Activos(ArrayList<String> list_Activos) { List_Activos = list_Activos;}
		  public void setIps(ArrayList<String> ips) 				  {  this.Ips = ips;			}
    	  public void setNick(String nick) 							  {  this.nick = nick;			}    	
    	  public void setIp(String ip) 								  {  this.ip = ip;				}   		
    	  public void setMensaje(String mensaje) 					  {  this.mensaje = mensaje;	}
		  //-----------------------------------------------------------------------//
    	}  	
    	//--------------------------------------------------------------------------------------------//
    	
    	
    	//---------------------------ENVIA MENSAJES AL SERVIDOR------------------------------//
    	public class envioMensaje  implements ActionListener{
    		@Override
    		public void actionPerformed(ActionEvent e) {

    			if(xdes == "" || xipd == ""){
					JOptionPane.showMessageDialog(null,"Seleccione un usuario destino por favor...!","ERROR",JOptionPane.INFORMATION_MESSAGE);
					return;
				}

    			try {
    				Socket conexion = new Socket(localhost, puerto_mensaje);
    				paquete_Envio envio_datos = new paquete_Envio();
    				envio_datos.setNick(nick.getText());
    				envio_datos.setIp(ip.getSelectedItem().toString());
    				envio_datos.setMensaje(mensaje.getText());
					envio_datos.setOrigen(xori);
					envio_datos.setIporigen(xipo);
					envio_datos.setDestino(xdes);
					envio_datos.setIpdestino(xipd);
					envio_datos.setTipo("MSJ");
					envio_datos.setContenido(mensaje.getText());
					envio_datos.setFecha(calcularFecha());
					envio_datos.setHora(calcularHora());
					//---
    				ObjectOutputStream paquete_datos = new ObjectOutputStream(conexion.getOutputStream());
    				paquete_datos.writeObject(envio_datos);
					try {
						escribirEnEditor(editor, xori, "MSJ", mensaje.getText(), calcularFecha(), calcularHora());
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}					
					conexion.close();
    			} catch (UnknownHostException e1) {
    				e1.printStackTrace();
    			} catch (IOException e1) {
    				System.out.println(e1.getMessage());
    			}
    			Borrar();
    		}
    	} 	
    	//----------------------------------------------------------------------------------------------//


        //------------METODO ENCARGADO SE BUSCAR Y SELECCIONAR EL ARCHIVO------------//
		public void seleccionarArchivo(){
			JFileChooser archivo = null;
			File abrir = null;
			String msj = "";
			try{
				//------Metodo que permite cargar la ventana--------//
				archivo = new JFileChooser();
				archivo.showOpenDialog(this);
				if (!archivo.equals(JFileChooser.CANCEL_OPTION)) {
					abrir = archivo.getSelectedFile();
					System.out.println("Archivo es:"+abrir.getName());
					JTFileSel.setText(abrir.getName());
					JLFileSel.setText(abrir.getPath());
				}else{
					return;
				}
			}catch(Exception e){
				System.out.println("Puede ser aca"+e.getMessage());
				return;
			}
		}
		//----------------------------------------------------------------------------//

		
		//------------------------------ENVIA LOS ARCHIVOS AL DESTINO DESEADO---------------------------------//
		public void enviarArchivo(String fileNombre, String path, String ipdestino ){
			String ruta = path.replace("\'", "\\'");

			try {
				Path origenPath = Paths.get(ruta);
				Path destinoPath = Paths.get("C:\\anexo_chat\\"+fileNombre);
				//sobreescribir el fichero de destino si existe y lo copia
				Files.copy(origenPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (FileNotFoundException ex) {
				System.out.println( ex.getMessage());
			} catch (IOException ex) {
				System.out.println( ex.getMessage());
			}

			try{
				// Creamos la direccion IP de la maquina que recibira el archivo
				InetAddress direccion = InetAddress.getByName( ipdestino );
				// Creamos el Socket con la direccion y elpuerto de comunicacion
				Socket socket = new Socket( direccion, puerto_archivo );
				socket.setSoTimeout( 2500 );
				socket.setKeepAlive( true );
				// Creamos el archivo que vamos a enviar
				File archivo = new File( ruta );
				// Obtenemos el tamaÃ±o del archivo
				int tamanoArchivo = ( int )archivo.length();
				System.out.println("Archivo:"+ archivo.toString()+" Tamano:"+tamanoArchivo);
				// Creamos el flujo de salida
				DataOutputStream flujo_salida = new DataOutputStream( socket.getOutputStream() );
				System.out.println( "Enviando Archivo: "+archivo.getName() );
				// Enviamos el nombre del archivo
				flujo_salida.writeUTF( archivo.getName() );
				// Enviamos el tamaÃ±o del archivo
				flujo_salida.writeInt( tamanoArchivo );

				// Creamos flujo de entrada para realizar la lectura del archivo en bytes
				FileInputStream entrada = new FileInputStream( ruta );
				BufferedInputStream buffer_entrada = new BufferedInputStream( entrada );

				// Creamos el flujo de salida para enviar los datos del archivo en bytes
				BufferedOutputStream buffer_salida = new BufferedOutputStream( socket.getOutputStream());

				// Creamos un array de tipo byte con el tamaÃ±o del archivo
				byte[] cantidad_byte = new byte[ tamanoArchivo ];

				// Leemos el archivo y lo introducimos en el array de bytes
				buffer_entrada.read( cantidad_byte );

				// Realizamos el envio de los bytes que conforman el archivo
				for( int i = 0; i < cantidad_byte.length; i++ ){
					buffer_salida.write( cantidad_byte[ i ] );
				}

				System.out.println( "Archivo Enviado: "+archivo.getName() );
				// Cerramos socket y flujos
				buffer_entrada.close();
				buffer_salida.close();
				socket.close();
			}
			catch( Exception e ){
				System.out.println( e.toString() );
			}
		}
		//---------------------------------------------------------------------------------------------------------//
		
		
		//---------------------------SE ENCARGA DE ENVIAR INFORMACION DE LOS ARCHIVOS------------------------------//
		
		public void enviarAnexo(){
			String msj = JTFileSel.getText();
			String path= JLFileSel.getText();

				if(xdes == "" || xipd == ""){
					JOptionPane.showMessageDialog(null,"Seleccione un usuario destino por favor...!","ERROR",JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if(msj == "" || path == ""){
					JOptionPane.showMessageDialog(null,"Seleccione un archivo antes de enviar un anexo...!","ERROR",JOptionPane.INFORMATION_MESSAGE);
					return;
				}

			try {
					Socket conexion = new Socket(localhost, puerto_mensaje);
					paquete_Envio envio_datos = new paquete_Envio();
					envio_datos.setNick(nick.getText());
					envio_datos.setIp(ip.getSelectedItem().toString());
					envio_datos.setMensaje(mensaje.getText());
					envio_datos.setOrigen(xori);
					envio_datos.setIporigen(xipo);
					envio_datos.setDestino(xdes);
					envio_datos.setIpdestino(xipd);
					envio_datos.setTipo("ANX");
					envio_datos.setContenido(msj);
					envio_datos.setFecha(calcularFecha());
					envio_datos.setHora(calcularHora());
					//---
					ObjectOutputStream paquete_datos = new ObjectOutputStream(conexion.getOutputStream());
					paquete_datos.writeObject(envio_datos);
						try {
							escribirEnEditor(editor, xori, "ANX", msj, calcularFecha(), calcularHora());
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
							conexion.close();
						} catch (UnknownHostException e1) {
							System.out.println(e1.getMessage());
						} catch (IOException e1) {
							System.out.println(e1.getMessage());
						}
					//-------------Enviando el archivo al server...
					enviarArchivo(msj, path, xipd );
					borrar();
				} 	
				//------------------------------------------------------------------------------------------------------------------//
   	
        //---------------------------------------RECIBE DATOS DEL SERVIDOR---------------------------------------//
		@Override
		public void run() {
			try {
				ServerSocket servidor = new ServerSocket(puerto_receptor);
				Socket cliente = null;
				paquete_Envio paquete_recibido = null;
				String xo,xt,xc,xf,xh;
				String[] tempNick;
				String tempValueList;
				int cont=0;
				while(true){
					cliente = servidor.accept();
					ObjectInputStream Mensaje_recibido = new ObjectInputStream(cliente.getInputStream());
					paquete_recibido = (paquete_Envio) Mensaje_recibido.readObject();
					cliente.close();
					if(paquete_recibido.getMensaje().equals("IniciarSesion")){
						ArrayList<String> listaIp = new ArrayList<String>(paquete_recibido.getIps());
						ArrayList<String> lista_activos = new ArrayList<String>(paquete_recibido.getList_Activos());
						listaIp = paquete_recibido.getIps();
						lista_activos = paquete_recibido.getList_Activos();				
						ip.removeAllItems();	  
						contactos.removeAll();    //----Elimina los elementos del JList
						model.removeAllElements();//----Elimina los elementos del modelo del Jlist
						for(int i=0; i<lista_activos.size(); i++) {
							tempValueList =  lista_activos.get(i);
							tempNick = tempValueList.split(":");
							if(!tempNick[0].equals(nick.getText())) { //---Elimina del JList el nombre del usuario local...
								model.add(cont, lista_activos.get(i));
								cont++;
								}}
						contactos.setModel(model);
						for(String direccion: listaIp){
							ip.addItem(direccion);
						}						
					}else{
						xo = paquete_recibido.getOrigen();
						xt = paquete_recibido.getTipo();
						xc = paquete_recibido.getContenido();
						xf = paquete_recibido.getFecha();
						xh = paquete_recibido.getHora();
						escribirEnEditor(editor, xo, xt, xc, xf, xh);}}
			} catch (Exception  e) {
				System.out.println(e.getMessage());}}
		//----------------------------------------------------------------------------------------------------//

		//--BORRA DATOS DEL JTextField DEL MENSAJE--//
		public void Borrar(){
			String Vaciar = "";
			mensaje.setText(Vaciar);
		}
		//------------------------------------------//
		
		//--BORRA DATOS DEL JTextField DEL ARCHIVO--//
		public void borrar(){
			String Vaciar = "";
			JTFileSel.setText(Vaciar);
			JLFileSel.setText(Vaciar);
		}
		//-----------------------------------------//
		
	
		//----------------------------------LEE DE LA BASE DE DATOS-----------------------------------//
		public void leerBaseDatos(String v1,String v2) throws SQLException {
			System.out.println("Entre a BD...origen:"+v1+", destino:"+v2+"Ip Base Datos: "+localhost);
			final String url = "jdbc:postgresql://"+localhost+":5432/chat";
			final String user = "postgres";
			final String password = pass_postgre;
			String xo,xt,xc,xf,xh = "";
			Connection conn = null;
			ResultSet rs = null;

			try {
				conn = DriverManager.getConnection(url, user, password);
				System.out.println("Conectado a PostgreSQL exitosamente");
				Statement st = conn.createStatement();
				rs = st.executeQuery("SELECT * FROM infochat WHERE origen='"+v1+"' AND destino='"+v2+"'");
				System.out.println("Tamano del RS:"+rs.getFetchSize());
					while (rs.next()) {
						xo = rs.getObject("origen").toString();
						xt = rs.getObject("tipo").toString();
						xc = rs.getObject("contenido").toString();
						xf = rs.getObject("fecha").toString();
						xh = rs.getObject("hora").toString();
						try {
							escribirEnEditor(editor, xo, xt, xc, xf, xh);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
						System.out.println("\n"+"  Datos:"+xo+"-"+xt+"-"+xc+"-"+xf+"-"+xh);
					}
				System.out.println("Se obtuvieron exitosamente los datos");
				conn.close();
			}
			catch (SQLException e) {
				System.out.println(e.getMessage());
				System.out.println("No se pudo conectar a PostgreSQL");
			}
		}		
		//------------------------------------------------------------------------------------------------//
		

		//----------------------------------ESCRIBE LOS MENSAJE EN EL EDITOR----------------------------------//
		public void escribirEnEditor(JTextPane edit, String origen,String tipo, String contenido, String fecha, String hora) throws BadLocationException {
			//System.out.println("\n"+"  Datos de:"+nick.getText()+" en escribirEnEditor:"+origen+"-"+tipo+"-"+contenido+"-"+fecha+"-"+hora);
			// Atributos para cada uno de las frases.
			String localUser = nick.getText(); 
			SimpleAttributeSet attrs = new SimpleAttributeSet();
			if (tipo.equals("MSJ")){
				if (localUser.equals(origen)) {  
					// En negrita
					StyleConstants.setBold(attrs, true);
					edit.getStyledDocument().insertString(edit.getStyledDocument().getLength(), origen + ":" + contenido, attrs);
				} else {
					// En cursiva
					StyleConstants.setItalic(attrs, true);
					StyleConstants.setBold(attrs, false);
					edit.getStyledDocument().insertString(edit.getStyledDocument().getLength(), origen + ":" + contenido, attrs);
				}
				// Ponemos el cursor al final del texto
				edit.setCaretPosition(edit.getStyledDocument().getLength());
			}
			if (tipo.equals("ANX")){
				if (localUser.equals(origen)) {	
					StyleConstants.setBold(attrs, true);
					edit.getStyledDocument().insertString(edit.getStyledDocument().getLength(), origen + ": Esto es un archivo anexo. Haz click para verlo..."+contenido, attrs);
				}else{
					// En cursiva
					StyleConstants.setItalic(attrs, true);
					StyleConstants.setBold(attrs, false);
					edit.getStyledDocument().insertString(edit.getStyledDocument().getLength(), origen + ": Esto es un archivo anexo. Haz click para verlo..."+contenido, attrs);
				}
				// Ponemos el cursor al final del texto
				edit.setCaretPosition(edit.getStyledDocument().getLength());
				insertaNuevaLinea(edit);
				Icon anexoIcon = new ImageIcon("c:/anexo_chat/anexo1.png");
				JButton manningButton = new JButton("Ver Anexo", anexoIcon);
				manningButton.setBackground(Color.white);
				Border bored = BorderFactory.createLineBorder(Color.lightGray);
				manningButton.setBorder(bored);
				manningButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						edit.setEditable(false);
						abrirarchivo("c:/anexo_chat/"+contenido);
					}
				});
				edit.insertComponent(manningButton);
			}
			insertaNuevaLinea(edit);
			StyleConstants.setItalic(attrs, true);
			StyleConstants.setBold(attrs, false);
			StyleConstants.setFontSize(attrs,10);
			edit.getStyledDocument().insertString(edit.getStyledDocument().getLength(), "...Fecha/Hora:"+fecha+" "+hora, attrs);
			insertaNuevaLinea(edit);
			insertarLineaDivisoria(edit);
		}
	}
    //----------------------------------------------------------------------------------------------------------------//
    
    //----------------INSERTA UNA LINEA AL EDITOR---------------------//
	private void insertaNuevaLinea(JTextPane editor)throws BadLocationException{
		// Atributos null
		editor.getStyledDocument().insertString(editor.getStyledDocument().getLength(),System.getProperty("line.separator"), null);
	}
	//----------------------------------------------------------------//

	//----------------INSERTA UNA LINEA DIVISORIA ENTRE LOS MENSAJES-------------//
	private void insertarLineaDivisoria(JTextPane editor)  throws BadLocationException{
		SimpleAttributeSet atributo = new SimpleAttributeSet();
		// En negrita
		StyleConstants.setBold(atributo, true);
		StyleConstants.setForeground(atributo, Color.lightGray);
		editor.getStyledDocument().insertString(editor.getStyledDocument().getLength(), "______________________________________________", atributo);
		insertaNuevaLinea(editor);
    }
	//----------------------------------------------------------------//

	//--------------SE ENCARGA DE ABRIR EL ARCHIVO QUE FUE ENVIADO-------//
	public void abrirarchivo(String archivo){
		try {
			File objetofile = new File(archivo);
			Desktop.getDesktop().open(objetofile);
		}catch (IOException ex) {
			System.out.println(ex);
		}
	}
	//-------------------------------------------------------------------//
		
		//-----------CALCULA LA FECHA A LA QUE EJECUTADO EL MENSAJE----------------//
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
		//---------------------------------------------------------------------//
		
		//---------CALCULA LA HORA A LA QUE EJECUTADO EL MENSAJE--------------//
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
		//--------------------------------------------------------------------//
}

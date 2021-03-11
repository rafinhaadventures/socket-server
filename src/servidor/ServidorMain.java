package servidor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


	    public class ServidorMain {

	        private Socket socket;
	        private ServerSocket serverSocket;
	        private DataInputStream bufferDeEntrada = null;
	        private DataOutputStream bufferDeSalida = null;
	        protected BufferedInputStream bufferedInputsStream;
	        List<String> registros = new ArrayList<String>();
	        Scanner escaner = new Scanner(System.in);
	        final String COMANDO_TERMINACION = "salir()";

	        public void levantarConexion(int puerto) {
	            try {
	                serverSocket = new ServerSocket(puerto);
	                mostrarTexto("Esperando conexión entrante en el puerto " + String.valueOf(puerto) + "...");
	                socket = serverSocket.accept();
	                mostrarTexto("Conexión establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n");
	            } catch (Exception e) {
	                mostrarTexto("Error en levantarConexion(): " + e.getMessage());
	                System.exit(0);
	            }
	        }
	        public void flujos() {
	            try {
	                bufferDeEntrada = new DataInputStream(socket.getInputStream());
	                bufferDeSalida = new DataOutputStream(socket.getOutputStream());
	                bufferDeSalida.flush();
	            } catch (IOException e) {
	                mostrarTexto("Error en la apertura de flujos");
	            }
	        }

	        public void recibirDatos() {
	            String st = "";
	            String salida = "Listado de Nombres:\n";
	            try {
	                do {
	                    st = (String) bufferDeEntrada.readUTF();
	                    if (st.equals("listar")) {
	                    	if (registros == null || registros.size() == 0 ) enviar("No hay registros");
	                    	else {
		                    	for(String aux : registros) {
		                    		salida = salida + aux + "\n";
		                    	}
		                    	enviar(salida);
	                    	}
	                    } else if(st.startsWith("insertar ")) {
	                    	registros.add(st.replace("insertar ", ""));
	                    	enviar("Insertado con exito\n");
	                    }
	                    else {
	                    	enviar("Comando no valido");
	                    }
	                } while (!st.equals(COMANDO_TERMINACION));
	            } catch (IOException e) {
	                cerrarConexion();
	            }
	        }


	        public void enviar(String s) {
	            try {
	                bufferDeSalida.writeUTF(s);
	                bufferDeSalida.flush();
	            } catch (IOException e) {
	                mostrarTexto("Error en enviar(): " + e.getMessage());
	            }
	        }

	        public static void mostrarTexto(String s) {
	            System.out.print(s);
	        }

	        public void escribirDatos() {
	            while (true) {
	                enviar(escaner.nextLine());   
	            }
	        }
	        
	        public void cerrarConexion() {
	            try {
	                bufferDeEntrada.close();
	                bufferDeSalida.close();
	                socket.close();
	            } catch (IOException e) {
	              mostrarTexto("Excepción en cerrarConexion(): " + e.getMessage());
	            } finally {
	                mostrarTexto("Conversación finalizada....");
	                System.exit(0);

	            }
	        }

	        public void ejecutarConexion(int puerto) {
	            Thread hilo = new Thread(new Runnable() {
	                @Override
	                public void run() {
	                    while (true) {
	                        try {
	                            levantarConexion(puerto);
	                            flujos();
	                            recibirDatos();
	                        } finally {
	                            cerrarConexion();
	                        }
	                    }
	                }
	            });
	            hilo.start();
	        }

	        public static void main(String[] args) throws IOException {
	            ServidorMain s = new ServidorMain();
	            Scanner sc = new Scanner(System.in);

	            String puerto = "5050";
	            s.ejecutarConexion(Integer.parseInt(puerto));
	            s.escribirDatos();
	        }

}
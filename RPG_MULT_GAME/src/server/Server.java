package server;

import java.io.*;
import java.net.*;
import java.util.*;

import game.entities.Entity;
import game.entities.Player;

/**
 *
 * @author wolmir
 */
public class Server extends Thread {
	public static int numeroJogadores = 0;
	public static String[] nomes = new String[6];
	public static void main(String args[]) {
		
		clientes = new Vector();
		try {
			ServerSocket s = new ServerSocket(8090);

//			Loop principal.
			while (true) {
				System.out.print("Esperando alguem se conectar...");
				Socket conexao = s.accept();
				System.out.println(" Conectou!");
				numeroJogadores++;
				
				
				Thread t = new Server(conexao);
				t.start();
				
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}

	}

	private static Vector clientes;
	private Socket conexao;
	private String meuNome;
	private int myId;

	public Server(Socket s) {
		conexao = s;
	}

	public void run() {
		try {
			myId = numeroJogadores;
			BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			PrintStream saida = new PrintStream(conexao.getOutputStream());
			
			
			//GARANTIR QUE USUARIO INSIRA UM NOME
			meuNome = entrada.readLine();
			//GUARDANDO O NOME DE TODOS.
			nomes[numeroJogadores-1] = meuNome;
			if (meuNome == null) {
				return;
			}
			
			clientes.add(saida);
			sendToYou(saida, ""+numeroJogadores);
			
			if(numeroJogadores > 1) {
				for(int i = 0; i < numeroJogadores -1; i++) {
					sendToYou(saida, ""+nomes[i]);
				}
				atualizarListaJogadores(saida);
			}
			
			String linha = entrada.readLine();

			while (linha != null && !(linha.trim().equals(""))) {
				
				sendToAll(saida, linha);
				linha = entrada.readLine();
			}
			sendToAll(saida, "do chat!");
			clientes.remove(saida);
			conexao.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}

//	enviar uma mensagem para todos, menos para o próprio
	public void sendToYou(PrintStream saida, String linha) throws IOException {
		Enumeration e = clientes.elements();
		while (e.hasMoreElements()) {
			PrintStream chat = (PrintStream) e.nextElement();
			if (chat == saida) {
				chat.println(linha);
			}
		}
	}
	public void sendToAll(PrintStream saida, String linha) throws IOException {
		Enumeration e = clientes.elements();
		while (e.hasMoreElements()) {
			PrintStream chat = (PrintStream) e.nextElement();
			if (chat != saida) {
				
				chat.println((myId-1)+ "," + linha);
			}
		}
	}
	
	public void atualizarListaJogadores(PrintStream saida) {
		Enumeration e = clientes.elements();
		while (e.hasMoreElements()) {
			PrintStream chat = (PrintStream) e.nextElement();
			if (chat != saida) {
				chat.println("ListPlayerUpdate");
				chat.println(numeroJogadores);
				chat.println(nomes[numeroJogadores-1]);
			}
		}
	}
	
}
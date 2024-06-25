package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import game.entities.Player;
import game.main.Game;

public class Client extends Thread {
	private static boolean done = false;

	private Game game;
	private Socket conexao;


	public Client(Socket s, Game game) {
		conexao = s;
		this.game = game;
	}

//	execução da thread
	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			String linha;
			while (true) {
				linha = entrada.readLine();
				
				//FAZENDO UMA SERIE DE CONDIÇÕES PARA ALGUNS COMANDOS ESPECIFICOS, O ELSE SERÁ MOVIMENTAÇÕES
				if(linha.equals("ListPlayerUpdate")) {
					//ATUALIZANDO A LISTA PARA OS JOGADORES QUE JA ESTÃO NO GAME;
					int id = Integer.parseInt(entrada.readLine());
					Player newPlayer = new Player(id, 0, 0, 6, 12, null, null);
					newPlayer.setName(entrada.readLine());
					Game.players.add(newPlayer);
				}else {
					String[] msg = linha.split(",");
					if(msg[1].equals("walk")) {
						//AQUI EU SEPARO AS INFORMAÇÕES, E PASSO O X E Y
						for(int i = 0; i < Game.players.size(); i++) {
							if(Game.players.get(i).id == Integer.parseInt(msg[0])) {
								Game.players.get(i).setX(Integer.parseInt(msg[2]));
								Game.players.get(i).setY(Integer.parseInt(msg[3]));
							}
						}
						
					}else if(msg[1].equals("disconnect")) {
						for(int i = 0; i < Game.players.size(); i++) {
							if(Game.players.get(i).id == Integer.parseInt(msg[0])) {
								Game.players.remove(Game.players.get(i));
							}
						}
						
					}
				}
				
				//MESAGEM QUE FOI RECEBIDA
				//System.out.println(linha);
			}

		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
		done = true;
	}
}
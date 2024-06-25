package game.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Importa��o dos Packages

import javax.swing.JFrame;

import game.entities.Entity;
import game.entities.Player;
import server.Client;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	// Variables
	// Janela e Run Game
	public static JFrame frame;
	private boolean isRunning = true;
	private Thread thread;
	public static final int WIDTH = 240, HEIGHT = 160, SCALE = 3;
	// Imagens e Gr�ficos
	private BufferedImage image;
	private Graphics g;
	// objetos
	public static List<Player> players;
	public static List<Entity> entities;
	public static Random random;

	public Player player;
	//VARIAVEIS
	String meuNome;
	// Construtor
	public Game() throws IOException {
		random = new Random();

		// Para que os eventos de teclado funcionem
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();

		players = new ArrayList<Player>();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		Socket conexao = new Socket("localhost", 8090);
		PrintStream saida = new PrintStream(conexao.getOutputStream());
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));

		// INSERIR NOME PARA O PLAYER
		System.out.print("Entre com o seu nome: ");
		meuNome = teclado.readLine();
		saida.println(meuNome);

		// CARREGANDO PLAYERS PARA QUEM ESTÁ CHEGANDO
		int n = Integer.parseInt(entrada.readLine());
		for (int i = 0; i < n - 1; i++) {
			Player playerCarregado = new Player(i + 1, 0, 0, 6, 12, null, null);
			playerCarregado.setName(entrada.readLine());
			players.add(playerCarregado);
		}

		// CRIANDO UMA THREAD PARA CUIDADR DESSE CLIENTE, TAMBEM ESTOU PASSANDO A CLASSE
		// GAME.
		Thread t = new Client(conexao, this);
		t.start();

		// CRIANDO O JOGADOR
		player = new Player(n, 0, 0, 6, 12, null, saida);
		player.setName(meuNome);
		players.add(player);
	}

	// Cria��o da Janela
	public void initFrame() {
		frame = new JFrame("Multiplayer");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			//ACÕES ANTES DE DISCONECTAR
            @Override
            public void windowClosing(WindowEvent e) {
            	if(player != null) {
            		player.disconnect();
            	}
                frame.dispose();
            }
        });
	}

	// Threads
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop() {		
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Game game = new Game();
		game.start();
	}

	public void tick() {
		for (int i = 0; i < players.size(); i++) {
			Player e = players.get(i);
			e.tick();
		}
	}

	// O que ser� mostrado em tela
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		g = image.getGraphics();// Renderizar imagens na tela
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

		for (int i = 0; i < players.size(); i++) {
			Player e = players.get(i);
			e.render(g);
		}

		g.dispose();// Limpar dados de imagem n�o usados
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.setColor(Color.white);
		g.drawString("Jogadores: " + players.size(), 0, 10);
		for (int i = 0; i < players.size(); i++) {
			Player e = players.get(i);
			e.renderText(g);
		}
		
		bs.show();
	}

	// Controle de FPS
	public void run() {
		// Variables
		long lastTime = System.nanoTime();// Usa o tempo atual do computador em nano segundos, bem mais preciso
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;// Calculo exato de Ticks
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		// Ruuner Game
		while (isRunning == true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				// System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}

		stop();

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Esquerda e Direita
		if (e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}if (e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}if (e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
		}if (e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Esquerda e Direita

		if (e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}if (e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}if (e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}if (e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener, MouseListener {
	public static final String FILE = "input/questions.csv";
	// app width height
	private static final int WIDTH = 600;
	private static final int HEIGHT = 600;
	// menu file
	private static final String NEW_GAME = "New game";
	private static final String END_GAME = "Quit";
	public static final String SHOW_REPLAY = "Play Again?";
        public static final String ABOUT = "About";
        public static final String RESET_GAME = "Reset Game";
        public static final String SCORE = "Score Table";
        
        
	// state of the program, 1 is playing
	private int state = 0;
        private int playing_again = 0;
	// generator for word array
	public Random rGen = new Random();
	// Char array that houses the random phrases that was converted from the
	// word array
	private static char[] wordArr;
	// word aray tha-t houses the 20 words from the file
	private static String[] words;
        //array storing tips from the file
        private static String[] tips;
        //array storing the timer values for the words
        private static Integer[] counters;
        //storing not in word
        private static String[] notinwords;
        //array storing user guesses
	private static char[] guesses;
	// counts the number of hangman body parts
	public static int bodypartsnum = 0;
	// holds the letters that the user guesses
	private static String numGuesses = "";
	// holds the random word that the word array generates to prevent multiple
	// calls
	public static String word;
        public static String tip;
        public static String notinword;
        public int last_r; //last value of random index
        public static int counter;
        public Boolean score_saved = false;
        public Timer timer;
        

	// all my panels - mainpanel holds left/right/bottom(keyboard)
	public static JPanel mainPanel, leftPanel, rightPanel, bottomPanel,
			belowPanel;
        
        


	
	public GUI() {
		// pass title to super class
		super("Hang Man");
		// set size of the jframe
		setSize(WIDTH, HEIGHT);
		// populate word array
		readCSV();
		// close Jframe on exit
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// main panel houses three panels - left, right and bottom(keyboard).
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(3, 0));
		mainPanel.setBackground(Color.WHITE);

		rightPanel = new JPanel();
		leftPanel = new JPanel();
		leftPanel.setBackground(Color.WHITE);
		rightPanel.setBackground(Color.WHITE);
		// add the left/right panel
		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(4, 4));
		bottomPanel.setBackground(Color.GRAY);
		// add the bottom panel which contains Jbuttons
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		belowPanel = new JPanel();
		belowPanel.setBackground(Color.GREEN);
		// add last panel which houses replay/exit button
		add(mainPanel);
		add(belowPanel, BorderLayout.AFTER_LAST_LINE);

		// set visibility to false until game is over
		belowPanel.setVisible(false);

		// create menu bar
		createMenuBar();
		// create keyboard buttons
		createButtons(bottomPanel);
		// create replay/exit buttons
		replayButtons(belowPanel);
		// add mouselistener
		addMouseListener(this);
                
                
                state = 1;
                play();

        
                
	}
        





	// method creates two jbutton for replay/exit and adds actionlisteners
	public void replayButtons(JPanel belowPanel) {
		JButton playAgain = new JButton(SHOW_REPLAY);
		playAgain.setSize(80, 80);
		playAgain.setActionCommand(SHOW_REPLAY);
		playAgain.addActionListener(this);
		JButton exit = new JButton(END_GAME);
		exit.setActionCommand(END_GAME);
		exit.addActionListener(this);
		exit.setSize(80, 80);
		belowPanel.add(playAgain);
		belowPanel.add(exit);
	}

	// method creates an array of jbuttons with actionlisteners to use as a
	// keyboard
        
	public void createButtons(JPanel bottomPanel) {

		JButton[] buttons = new JButton[26];
		String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
				"W", "X", "Y", "Z" };

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JButton(letters[i]);
			buttons[i].setSize(40, 40);
			buttons[i].setActionCommand(letters[i]);
			buttons[i].addActionListener(this);

			bottomPanel.add(buttons[i]);
		}

	}

	// method creates menu and menuitems
	public void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// create file menu
		JMenu fileMenu = new JMenu("File");
                JMenu helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
                menuBar.add(helpMenu);

		// add menu items
                createMenuItem(helpMenu, ABOUT);
		createMenuItem(fileMenu, NEW_GAME);
                createMenuItem(fileMenu, RESET_GAME);
                createMenuItem(fileMenu, SCORE);
                createMenuItem(fileMenu, END_GAME);

	}
	//method creates menu items with action listeners
	public void createMenuItem(JMenu menu, String itemName) {
		JMenuItem menuItem = new JMenuItem(itemName);
		menuItem.addActionListener(this);
		menu.add(menuItem);
	}

	public void paint(Graphics g) {
		super.paint(g);

		// set the font
		Font font = new Font("Arial", Font.BOLD | Font.BOLD, 24);
		g.setFont(font);
		g.setColor(Color.BLACK);
	
		// if user is playing the game
		if (state == 1) {
			
			
			
			String result = "";
			for (int i = 0; i < guesses.length; i++) {

				result += guesses[i] + " ";
			
			
			}
			
			g.drawString("GUESSES", 25, 300);
                        g.drawString(result, 25, 320);
			g.drawString(numGuesses, 25, 350);
			System.out.println(wordArr);
                        System.out.println("bodyparts: "+bodypartsnum);
			// if user misses a letter - display body parts
                        gameInfo(g);
			
			hangman(g);

			}
		}

	

	private void hangman(Graphics g) {
            Toolkit t = Toolkit.getDefaultToolkit();
            Image i = t.getImage("img/hangman1.png");
            
            //hangman position
            int img_x = 370;
            int img_y = 140;
            //hanmgman size
            int img_sx = 230;
            int img_sy = 230;
            g.drawImage(i,img_x,img_y,img_sx,img_sy,this);
            
		if (bodypartsnum >= 1) {
                   
			if (bodypartsnum >= 2) {

                        i = t.getImage("img/hangman2.png");
			}

			if (bodypartsnum >= 3) {
			
                        i = t.getImage("img/hangman3.png");
			}
			if (bodypartsnum >= 4) {
				// right arm
                        i = t.getImage("img/hangman4.png");
			}
			if (bodypartsnum >= 5) {
				// left foot
                        i = t.getImage("img/hangman5.png");
			}
			if (bodypartsnum >= 6) {
				// right foot
                        i = t.getImage("img/hangman6.png");
			}
                        if (bodypartsnum >= 7) {
				// right foot
                        i = t.getImage("img/hangman7.png");
			}
                       
               g.drawImage(i,img_x,img_y,img_sx,img_sy,this);
		}
                                   

		
	}
        

        
        
        

	private void gameInfo(Graphics g) {

                 g.setFont(new Font("Arial", Font.PLAIN, 21));
                 
                 g.drawString("Tip: "+tip, 25, 80);
                 g.drawString("Not in string: "+notinword,25,110);
                 g.drawString("Countdown: "+counter,160,200);
                        
		if (!winner() && counter != 0) {
                    g.setColor(new Color(0,0,255));
                    g.drawString("Status: Playing",25,140);
			
		} 
                
                else if (winner() && bodypartsnum < 6) {
                    
                        
                        
			System.out.println("Win parts:"+bodypartsnum);
			
                        
                        g.setColor(new Color(0,255,0));
                        g.drawString("Status: Win",25,140);
                        if(score_saved == false){
                            after_win after_win = new after_win();
                            after_win.setVisible(true);
                            score_saved = true;
                        }
                        
			//bottomPanel.setVisible(false);
			//belowPanel.setVisible(true);
                        
			
			//draw lost message and enable belowpanel
		} if (bodypartsnum > 6 || counter == 0) {
                       //bottomPanel.setVisible(false);
			//belowPanel.setVisible(true);
                        System.out.println("lost parts:"+bodypartsnum);
                        g.setColor(new Color(255,0,0));
                        g.drawString("Status: You lost",25,140);
			//g.drawString("You Lost!!", 25, 80);
			
                        

		}
                

	}

	// generate a random word and return via char array
	public String getword() {
		
            if(playing_again==0){
                readCSV();
		int n = words.length;
		int r = rGen.nextInt(n);
                last_r = r;
		String word = words[r];
                counter = counters[r]; //set counters and tip accordingly
                tip = tips[r];
                notinword = notinwords[r];
                
                System.out.println(tip);
                System.out.println(counter);
                
                return word;
            }
                
                counter = counters[last_r];
		return words[last_r];
	}

	// method determines whether guesses array match the randphrase array 
	public boolean winner() {
		if (Arrays.equals(guesses, wordArr)) {
                    
                        
			return true;
		}
                
                //else its still playing
                else {
			return false;
		}

	}

	// method reads from a file and writes to arraylist which is converted back
	// to an array
	public void readCSV() {
		// create a bufferedReader
		BufferedReader reader = null;
		// create a list array and store the values from the text file
		List<String> wordList = new ArrayList<String>();
                List<String> tipList = new ArrayList<String>();
                List<String> notinwordList = new ArrayList<String>();
                List<Integer> counterList = new ArrayList<Integer>();
                
		try {
			reader = new BufferedReader(new FileReader(FILE));
			String s = null;

			while ((s = reader.readLine()) != null) {
                                
                                s = s.toLowerCase();
                                String[] values = s.split(",");
				wordList.add(values[0]);
                                tipList.add(values[1]);
                                notinwordList.add(values[2]);
                                
                                counterList.add(Integer.parseInt(values[3]));

			}

		} catch (IOException e) {

			System.out.println(e.getMessage());
			System.exit(-1);
		} finally {
			try {
				// close the file
				reader.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}
		// convert from lists to array and assign 
		words = wordList.toArray(new String[wordList.size()]);
                tips =  tipList.toArray(new String[tipList.size()]);
                notinwords = notinwordList.toArray(new String[notinwordList.size()]);
                counters = counterList.toArray(new Integer[counterList.size()]);

	}
	
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
  
        if(command.equals(ABOUT)){
       
        about win_about = new about();
        win_about.setVisible(true);
        }
        
        if(command.equals(SCORE)){
       
        scoreboard scoreboard = new scoreboard();
        scoreboard.setVisible(true);
        
        }
        
        
         
         
         
	 if (command.equals(NEW_GAME)) {
			// once the user has pressed play, change state and call to play
			// method
                        bodypartsnum = 0;
			state = 1; //playing state
                        playing_again = 0; //but not playing same game
                        score_saved = false;
			play();
                        
                       // guesses = new char[guesses.length]; 
                        numGuesses = "";
			repaint();

		}
         if(command.equals(RESET_GAME)){
             score_saved = false;
            state = 1;
            bodypartsnum = 0;
            playing_again = 1;        
            numGuesses = "";
            play();
            repaint();
            
             
         }
         
         
		
	 else if (command.length() == 1 && state == 1) {
			// pass action event to letters if the user has pressed play and the
			// event is generated
			// by the Jbutton array (length of the string is one)
			letters(command);
		}
		//reset status and replay game
		else if (command.equals(SHOW_REPLAY)) {

			bodypartsnum = 0;
			numGuesses = "";
			bottomPanel.setVisible(true);
                        belowPanel.setVisible(false);
			state = 1;
                        playing_again = 0;
			play();
			repaint();		

		} else if (command.equals(END_GAME)) {
			state = 2;
			System.exit(-1);
		}

		 repaint();
	}

	// method receives actionevent from JButtons and compares it to randphrase
	// array
	public void letters(String command) {

		System.out.println(command);
                
               if(bodypartsnum<7 && counter > 0){
		if (word.contains(command.toLowerCase())) {
			for (int i = 0; i < wordArr.length; i++) {
				if (command.toLowerCase().charAt(0) == wordArr[i]) {
					guesses[i] = command.toLowerCase().charAt(0);

				}

			}
			// if letter does not match - bodycounter increases
		} else if (!word.contains(command.toLowerCase())) {
//			JOptionPane.showMessageDialog(null, "Sorry " + command
//					+ " is not part of the word");
			bodypartsnum++;
		}

		// concatenation user guesses
		numGuesses += command;
		if (bodypartsnum < 7 && !winner()) {
			numGuesses += ",";
		}
		repaint();
            }
        }

	// method generates the '_' on the guesses array so it's display to the user
	private void play() {

		// store random word
		word = getword();
                
               timer = new Timer(1000, e -> {
               if (counter > 0 && !winner()) {
                   counter--;
                   System.out.println(counter);
                   repaint(); 
               } else if(winner()){
               
              // JOptionPane.showMessageDialog(null, "Winer");
               timer.stop();
               
               
               }
               
               else{
                   ((Timer) (e.getSource())).stop();
                   counter = 0;
                   JOptionPane.showMessageDialog(null, "Time is up!");
                   
               }
           });
           timer.setInitialDelay(0);

           timer.start();

                
		// convert random word to char array
		wordArr = word.toCharArray();
		//create an array to hold and display user input
		guesses = new char[wordArr.length];
		//populate the array with dashes first
		for (int i = 0; i < guesses.length; i++) {
			guesses[i] = '_';
			}

	}

	public void mouseClicked(MouseEvent e) {
            
               PointerInfo a = MouseInfo.getPointerInfo();
               Point point = new Point(a.getLocation());
               SwingUtilities.convertPointFromScreen(point,e.getComponent());
               int x=(int) point.getX();
               int y=(int) point.getY();
               System.out.print("\nX: "+x);
               System.out.print(" Y "+y);
	}

	public void mouseEntered(MouseEvent e) {
         
                
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public static void main(String[] args) {
		GUI hangman = new GUI();
                
		hangman.setVisible(true);
                
                
        }  
}
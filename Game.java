import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Game{
	
	private EButton[] buttons; //Array of all 81 buttons
	private Board theGame; //Instance of board for game
	private JFrame window; 
	private JPanel gameGrid; //Grid that holds frames
	private JPanel frameGrid; //Frames
	private JLabel statusBar, scoreIndicator; //Text indicators
	private boolean mode; //true = full view, false = subView
	private final int GAME_DIM = 600; //Game dimensions
	private boolean gameWon;
	private Scheme base;

	//GameOver variables
	private JFrame endGame;
	private JLabel endGameLabel;
	private JButton endGameMenu, endGamePlayAgain;
	private JPanel endGamePanel1, endGamePanel2;
	
	/** Buttons that hold information on which field and tile it is. */
	private class EButton extends JButton{
		private int parentField; //index of parent field in game from 0 to 9
		private int content; //index of tile in its parent frame from 0 to 9
		public EButton(int f, int t){
			parentField = f;
			content = t;
		}
		public int index() { //Gives the index on a 9x9 grid of the button. 
			return (parentField * 9 + content);
		}
	}

	/**
	* Swaps the turn of the player and changes the turnIndocator to indicate who is playing.
	*/
	private void changeTurn() {
		theGame.changeActivePlayer();
		scoreIndicator.setText(base.player1name + ": " + theGame.getP1Score() + 
			"       " + base.player2name + ": " + theGame.getP2Score() +
			"       Fields left in play: " + theGame.getWinnableFields());
	}

	/**
	* Changes the text in the status bar.
	*/
	private void setStatus(String s){
		statusBar.setText(s);
	}

	/** Whenever a button is pressed, do this run a turn sequence */
	private class ButtonListener implements ActionListener{
		//What the button will do.
		public void actionPerformed(ActionEvent e){
			EButton butt = (EButton)e.getSource(); //Loads the button information with data values
			Field clickedField = theGame.getField(butt.parentField); //Gets the field in question
			Tile clickedTile = theGame.getTile(butt.parentField,butt.content); //Gets the tile in question
			
			
			/** THIS IS FOR TESTING ENDGAME. PLEASE KEEP COMMENTED OUT
			if (butt.index() == 0) {
				gameOver();
			}*/
			
			
			setStatus("Please play in the green field, " + (theGame.getActivePlayer()? base.player2name : base.player1name)); //Set owner); //Resets the Status text
			if(gameWon) {
				theGame.reset();
				gameWon = false;
				setStatus("Welcome to #TheGame! Press any square to start, " + base.player1name);
				recolor();
				return;
			}
			//If players could have chosen any field. 
			if(theGame.getFieldInPlay() == -1) {
				if(clickedTile.getOwner() == 0) { //The tile is free
					clickedTile.setOwner(theGame.getActivePlayer()? 2 : 1); //Set owner
					if (clickedField.checkIfWon()) { //Check if field is won
						theGame.incScore(theGame.getActivePlayer()); //Increases the score of the player who won
						if(theGame.checkIfWon()){ //Check if game is won if field is won
							gameOver();
							return;	
						} 
						setStatus((theGame.getActivePlayer()? base.player2name : base.player1name) + " won a field.");
					}
					else if (clickedField.isFull() && clickedField.getOwner() == 0) { //Check if field was catsgamed
						theGame.decWinnableFields(); //Decreases winnable fields due to cats game
						if(theGame.checkIfWon()){ //Check if game is won if field is won
							gameOver();
							return;
						} 
					}
					theGame.setLastIndex(butt.index());
					changeTurn();
					if (!(theGame.getField(butt.content).isFull())) { //Set the next field to play in to the previous player's tile
						theGame.setFieldInPlay(butt.content);                            
					}
					else { //The field the next player was sent is full
						setStatus("You can play anywhere, " + (theGame.getActivePlayer()? base.player2name : base.player1name));
						theGame.setFieldInPlay(-1);
					}
				}
				else { //Clicked Tile is not free
					setStatus("That's not a free tile, " + (theGame.getActivePlayer()? base.player2name : base.player1name));
				}
			}

			//Checks if the player clicked a button in the correct field
			else if(theGame.getFieldInPlay() == butt.parentField) {
				if(clickedTile.getOwner() == 0) { //The tile is free
					clickedTile.setOwner(theGame.getActivePlayer()? 2 : 1); //Set owner
					if (clickedField.checkIfWon()) { //Check if field is won
						theGame.incScore(theGame.getActivePlayer()); //Increases the score of the player who won
						if(theGame.checkIfWon()){ //Check if game is won if field is won
							gameOver();
							return;	
						} 
						setStatus((theGame.getActivePlayer()? base.player2name : base.player1name) + " won a field.");
					}
					else if (clickedField.isFull() && clickedField.getOwner() == 0) { //Check if field was catsgamed
						theGame.decWinnableFields(); //Decreases winnable fields due to cats game
						if(theGame.checkIfWon()){ //Check if game is won if field is won
							gameOver();
							return;
						} 
					}
					theGame.setLastIndex(butt.index());
					changeTurn();
					if (!(theGame.getField(butt.content).isFull())) { //Set the next field to play in to the previous player's tile
						theGame.setFieldInPlay(butt.content);                            
					}
					else { //The field the next player was sent is full
						setStatus("You can play anywhere, " + (theGame.getActivePlayer()? base.player2name : base.player1name));
						theGame.setFieldInPlay(-1);
					}
				}
				else { //Clicked Tile is not free
					setStatus("That's not a free tile, " + (theGame.getActivePlayer()? base.player2name : base.player1name));
				}
			}
			else {
				setStatus("That's not the right field, " + (theGame.getActivePlayer()? base.player2name : base.player1name));
			}
			recolor();
		}
	}
	
	/**
	* Function that does all duties associated with the end of the game. Allows
	* users to choose to quit and go to the main menu or play the game again
	*/
	public void gameOver(){
		//Prints winner to status of game
		setStatus((theGame.getActivePlayer()? base.player2name : base.player1name) + " won! Press any button to play again");
		gameWon = true;
		
		//Initializes JFrames and JPanels for endGame pop-up
		endGame = new JFrame("Game Over");
		endGamePanel1 = new JPanel(new BorderLayout());
		endGamePanel2 = new JPanel(new FlowLayout());

		//Creates text for JLabel in endGame pop-up
		endGameLabel = new JLabel((theGame.getActivePlayer()? base.player2name : base.player1name) + " won! Would you like to play again?");
		
		//Initializes buttons for endGame pop-up
		endGameMenu = new JButton("Menu");
		endGamePlayAgain = new JButton("Play Again");
		
		//Adds components to panels for endGame pop-up
		endGamePanel1.add(endGameLabel);
		endGamePanel2.add(endGameMenu);
		endGamePanel2.add(endGamePlayAgain);
		
		//Creates a layout for endGame pop-up
		endGame.setLayout(new FlowLayout());
		endGame.add(endGamePanel1);
		endGame.add(endGamePanel2);
		
		//Sets dimensions and centers the endGame pop-up
		endGame.setSize(300,100);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		endGame.setLocation(dim.width/2-endGame.getSize().width/2, dim.height/2-endGame.getSize().height/2);
		endGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		endGame.setVisible(true);
		
		//Action listeners for buttons of endGame pop-up
		endGameMenuClicked menuClicked = new endGameMenuClicked();
		endGameMenu.addActionListener(menuClicked);
		
		endGamePlayAgainClicked playAgainClicked = new endGamePlayAgainClicked();
		endGamePlayAgain.addActionListener(playAgainClicked);
		
		//Recolors tiles to default
		recolor();
	}
	
	/** ActionListener for endGameMenu button */
	public class endGameMenuClicked implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//Main menu function call here
		}
	}
	
	/** ActionListener for endGamePlayAgain button */
	public class endGamePlayAgainClicked implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//PlayAgain function call here
		}
	}
	
	/**
	* Sets up all the Buttons assuming aa 9x9 grid
	*/
	public void initButtons(){
		buttons = new EButton[81];
		for(int i = 0; i < 81; i++){
			buttons[i] = new EButton(i/9, i%9);
			buttons[i].addActionListener(new ButtonListener());
		}
	}

	public void initFrame(){
		initButtons();
		window = new JFrame();

		fullView();
		statusBar = new JLabel("Welcome to #TheGame! Press any square to start, " + base.player1name);
		scoreIndicator = new JLabel(base.player1name + ": " + theGame.getP1Score() + 
			"       " + base.player2name + ": " + theGame.getP2Score() +
			"       Fields left in play: " + theGame.getWinnableFields());
			
		scoreIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
		statusBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoreIndicator.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		statusBar.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		gameGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
		window.add(statusBar);
		window.add(gameGrid);
		window.add(scoreIndicator);

		window.setLayout(new BoxLayout(window.getContentPane(),BoxLayout.Y_AXIS));
		window.pack();
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);
		window.setVisible(true);
	}
	
	/**
	* Sets grid to 9x9 view
	*/
	public void fullView(){
		mode = true;
		gameGrid = new JPanel(new GridLayout(3,3,10,10));
		for(int i = 0; i < 9; i++) {
			frameGrid = new JPanel(new GridLayout(3,3,5,5));
			for(int j = 0; j < 9; j++) {
				frameGrid.add(buttons[i*9+j]);
			}
			gameGrid.add(frameGrid);
		}
		recolor();
		gameGrid.setPreferredSize(new Dimension(GAME_DIM, GAME_DIM));

	}
	
	///////////////////////////////////////
	public void subView(int f){
		mode = false;
	}
	///////////////////////////////////////
	
	/**
	* Recolors all buttons. Will recolor for either view
	*/
	private void recolor(){
		for(EButton e: buttons){
			e.setOpaque(true);
			//Color all blank
			e.setBackground(base.tile);
			//Color the field in play
			if(e.parentField == theGame.getFieldInPlay() || theGame.getFieldInPlay() == -1) {
				e.setBackground(base.field);
			}
			int t = theGame.getField(e.parentField).getTile(e.content).getOwner();
			//Color taken spaces
			if(t == 1) e.setBackground(base.player1);
			if(t == 2) e.setBackground(base.player2);
			//Color last played
			if(e.index() == theGame.getLastIndex()) {
				e.setBackground(theGame.getActivePlayer()? base.player1new : base.player2new);
			}
		}
		gameGrid.setBackground(base.board);
		for(Component c : gameGrid.getComponents()) {
			c.setBackground(base.board);
		}
	}
	
	///////////////////////////////////////
	public void endView() {} //End
	///////////////////////////////////////
	
	public Game(){
		theGame = new Board();
		gameWon = false;
		base = new Scheme();
		initFrame();
	}
	
	public static void main(String[] args){
		Game g = new Game();
	}
}

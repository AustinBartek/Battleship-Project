package battleship.ui;

import java.awt.CardLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import battleship.App;
import battleship.board.Board;

/* WHAT I WANT:
1. Intro screen with a start button and a join game button and a nice little graphic.
2. A Board setup menu where you can align all of your ships and place them
3. A game screen that reacts to online play

*/

public class BattleshipWindow extends JFrame {
    private final JPanel screens = new JPanel(new CardLayout());
    private JPanel currentPanel;
    private BoardPanel playerGame, opponentGame;

    public BattleshipWindow() {
        screens.add(new IntroPanel(this), "mainMenu");
        screens.add(new BoardEditorPanel(this), "editor");

        this.add(screens);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowActivated(WindowEvent e) {
            }
            @Override
            public void windowClosed(WindowEvent e) {
                if (App.isOnline()) {
                    App.disconnect();
                }
            }
            @Override
            public void windowClosing(WindowEvent e) {
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            @Override
            public void windowIconified(WindowEvent e) {
            }
            @Override
            public void windowOpened(WindowEvent e) {
            }
        });
    }
    
    public void startBoardEditor() {
        goToScreen("editor");
    }

    public void goToMainMenu() {
        goToScreen("mainMenu");
    }

    public void startJoinScreen() {
        goToScreen("join");
    }

    public void goToScreen(String name) {
        CardLayout cardLayout = (CardLayout) screens.getLayout();
        cardLayout.show(screens, name);
        currentPanel = (JPanel) screens.getComponent(0);

        System.out.println(cardLayout);
    }

    public void addGameScreen(Board board) {
        if (playerGame != null) {
            screens.remove(playerGame);
        }
        playerGame = new BoardPanel(board, false, this);
        screens.add(playerGame, "playerGame");
    }

    public void addOpponentGameScreen(Board board) {
        if (opponentGame != null) {
            screens.remove(opponentGame);
        }
        opponentGame = new BoardPanel(board, true, this);
        screens.add(opponentGame, "opponentGame");
    }
}

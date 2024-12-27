package battleship.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class IntroPanel extends JPanel {
    private final BattleshipWindow window;

    public IntroPanel(BattleshipWindow owner) {
        window = owner;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 750));
        setBackground(new Color(50, 175, 200));

        JButton startButton = makeButton("START");
        JButton joinButton = makeButton("JOIN");

        startButton.addActionListener((ActionEvent e) -> {
            window.startBoardEditor();
        });
        joinButton.addActionListener((ActionEvent e) -> {
            window.startJoinScreen();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 25));
        buttonPanel.add(startButton);
        buttonPanel.add(joinButton);
        buttonPanel.setBackground(new Color(0, 100, 125));

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public final JButton makeButton(String text) {
        Font buttonFont = new Font(Font.MONOSPACED, Font.BOLD, 40);
        Color fontColor = new Color(25, 125, 175);

        JButton newButton = new JButton(text);
        newButton.setFocusable(false);
        newButton.setPreferredSize(new Dimension(200, 100));
        newButton.setFont(buttonFont);
        newButton.setBackground(new Color(75, 200, 225));
        newButton.setForeground(fontColor);
        newButton.setOpaque(true);
        newButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        return newButton;
    }
}

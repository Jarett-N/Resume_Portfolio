/* Jarett Nelson
 * Assingment 6, Spring 2024
 * CS245
 * The purpose of Instructions.java is to create the variety of text files that will pop up when a button is clicked,
 *  it also has a button that will print the text to the console
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Instructions extends JFrame implements ActionListener {
    JPanel panel = new JPanel();
    JTextArea info = new JTextArea();
    JButton button = new JButton("Print");
    Panel bPanel = new Panel();
    int select;

    //holds the layout and switch for the new window that pops up when you click any of the buttons
    Instructions(int e){
        //sets frame
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 800);
        frame.setTitle("Instructions");
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.getContentPane().setForeground(Color.CYAN);
        frame.setVisible(true);
        frame.setLayout(null);


        info.setBounds(0,0,600,800);
        info.setFont(new Font("Arial", Font.PLAIN, 25));
        info.setLineWrap(true);
        panel.setBounds(0,0,600,800);


        //sets text based on piece or empty square
        switch(e){
            case 0: info.setText("King: It moves one square in any direction. Its the most valuable piece," +
                    " and if it is ever attacked it must be countered immediately, and if it is impossible, the game" +
                    " is lost. Piece is also involved in Castling." + '\n'+'\n'+ "Castling: Once per game, as long as; " +
                    "the king and the rook in question has not moved, there are no pieces between the king and the rook" +
                    ", and the king is not in check or pass through an attacked square. You may move the king 2 squares" +
                    " towards the rook, and then the rook is moved into the space the king crossed.");
                select = 0;
                break;
            case 1: info.setText("Queen: It can move any number of pieces horizontally, or vertically," +
                    " and can move any number of squares diagonally, but cannot jump over pieces.");
                select = 1;
                break;
            case 2: info.setText("Bishop: It can move any number of squares diagonally, but cannot jump" +
                    " over pieces.");
                select = 2;
                break;
            case 3: info.setText("Knight: It moves in a pattern of moving two squares horizontally" +
                    " and one either direction vertically, or, two squares vertically and one either direction " +
                    "horizontally.");
                select = 3;
                break;
            case 4: info.setText("Rook: It can move any number of pieces horizontally, " +
                    "or vertically, but cannot jump over pieces; this piece is also involved in Castling." +'\n'+'\n' +
                    "Castling: Once per game, as long as; the king and the rook in question has not moved, there are" +
                    " no pieces between the king and the rook, and the king is not in check or pass through an attacked" +
                    " square. You may move the king 2 squares towards the rook, and then the rook is moved into the space" +
                    " the king crossed.");
                select = 4;
                break;
            case 5: info.setText("Pawn: It moves forward one space at a time, unless at its starting" +
                    " position, in which case it can move either two or one. It can capture pieces diagonally forward" +
                    " to it by jumping to that pieces position and replacing it. This piece is involved in both En " +
                    "Passant, and Promotion." + '\n'+'\n'+ "En Passant: When you move a pawn past another opposing" +
                    " pawn in an adjacent column the opposing pawn can take their immediate turn to take the piece," +
                    " or lose the chance to do so if it is not taken the exact next turn." + '\n'+'\n'+ "Promotion: When" +
                    " a pawn has crossed the board to the farthest opposing row, when it reaches that row it is turned" +
                    " into another piece with a choice of a, Queen, Rook, Bishop, or Knight. This new piece can exceed" +
                    " the normal amount of pieces on the board (i.e 2 queens, 3 knights etc.)");
                select = 5;
                break;
            case 6:
                info.setFont(new Font("Arial", Font.PLAIN, 12));
                info.setText("Chess is a game played by two players on a 8 by 8 grid board with light  and dark" +
                        " squares, that is either won or drawn in the end. There are two sets, one set for each player," +
                        " named White and Black; they each individually have 16 pieces. Which are a King, Queen, Bishop," +
                        " Knight, Rook, and Pawn. Each has its own moveset and separate rules. \n" + "King: It moves one" +
                        " square in any direction. Is the most valuable piece, and if it is ever attacked it must be " +
                        "countered immediately, and if it is impossible, the game is lost. Piece is also involved in " +
                        "Castling.\n" + "Rook: It can move any number of pieces horizontally, or vertically, but cannot" +
                        " jump over pieces; this piece is also involved in Castling.\n" + "Bishop: It can move any number" +
                        " of squares diagonally, but cannot jump over pieces.\n" + "Queen: It combines both Rook’s and" +
                        " Bishop’s movement, but cannot jump over pieces.\n" + "Knight: It moves in a pattern of moving" +
                        " two squares horizontally and one either direction vertically, or, two squares vertically and" +
                        " one either direction horizontally.\n" + "Pawn: It moves forward one space at a time, unless at" +
                        " its starting position, in which case it can move either two or one. It can capture pieces " +
                        "diagonally forward to it by jumping to that pieces position and replacing it. This piece is " +
                        "involved in both En Passant, and Promotion.\n" + "Alternate moves for certain pieces include" +
                        " “Castling”, “En Passant”, and “Promotion”. \n" + "Castling: Once per game, as long as; the " +
                        "king and the rook in question has not moved, there are no pieces between the king and the rook, " +
                        "and the king is not in check or pass through an attacked square. You may move the king 2 squares" +
                        " towards the rook, and then the rook is moved into the space the king crossed.\n" + "\tEn Passant:" +
                        " When you move a pawn past another opposing pawn in an adjacent column the opposing pawn can take" +
                        " their immediate turn to take the piece, or lose the chance to do so if it is not taken the " +
                        "exact next turn.\n" + "\tPromotion: When a pawn has crossed the board to the farthest opposing" +
                        " row, when it reaches that row it is turned into another piece with a choice of a, Queen, Rook," +
                        " Bishop, or Knight. This new piece can exceed the normal amount of pieces on the board " +
                        "(i.e 2 queens, 3 knights etc.)\n" + "The game can be won by checkmate, resignation, or a “win on" +
                        " time”, as well as by “Draw”, by a variety of ways.\n" + "Winning:\n" + "Check and Checkmate: " +
                        "check is the term used when a king is under attack, and means that the next term must get it " +
                        "out of danger. This is done in 3 ways, moving the king, capturing the attacking piece, and " +
                        "putting a piece in between the king and the attacking piece.\n" + "Resignation: A player " +
                        "concedes the game to an opponent, and if the opponent has a way to checkmate, then this is" +
                        " a win for the opponent; otherwise the game ends in a draw if no checkmate is found.\n" + "Win" +
                        " on Time: A player wins on time if the game has clocks running, and their opponent runs out" +
                        " of time. This ends the game and the player with time remaining on their clock wins.\n" +
                        " Draw:\n" + "\tStalemate: If a player has no legal moves, but isn’t in check, and the position" +
                        " is stalemate, the game is drawn.\n" + "Dead Position: If neither of the players is able" +
                        " to checkmate the other by any sequence of legal moves.\n" + "Draw by Agreement: When both" +
                        " players verbally agree to have the game end in draw, if offered by one player and the other" +
                        " agrees, the party in agreement has one move to gain checkmate, otherwise the game ends in" +
                        " draw.\t\n" + "Threefold Repetition: It is an agreement where both players are not able to" +
                        " avoid repeating moves without taking a heavy disadvantage. Generally it is when both repeat" +
                        " three times and then one player offers the draw. There is another version called the Fivefold" +
                        " repetition, where if five repetitions are found the arbiter ends the game.\n" + "Fifty-move" +
                        " Rule: if in 50 previous turns, neither player has moved a pawn or taken a piece, a draw can" +
                        " be offered up.\n" + "Draw on time: In a game with time, if one player runs out of time, but" +
                        " their opponent has no sequence of moves that would allow them to win, the game ends in a" +
                        " draw.\n" + "Draw by resignation: a game is drawn if a player resigns and their opponent " +
                        "does not have a sequence of moves to get a checkmate.");
                select = 6;
                break;


        }
        //creates the panel
        bPanel.setBounds(700,500,100,50);
        bPanel.setLayout(new GridLayout(1,1));
        bPanel.setBackground(Color.GRAY);
        bPanel.setForeground(Color.WHITE);

        //creates the button
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.addActionListener(this);

        //adds it to the frame
        bPanel.add(button);
        panel.add(info);
        frame.add(panel);
        frame.add(bPanel);
    }

    //this holds all the reactions for button presses
    @Override
    public void actionPerformed(ActionEvent e) {
        //sets print based on each piece or empty square
        if(e.getSource() == button){
            switch(select){
                case 0: System.out.println("King: It moves one square in any direction. Its the most valuable piece," +
                        " and if it is ever attacked it must be countered immediately, and if it is impossible, the game" +
                        " is lost. Piece is also involved in Castling." + '\n'+'\n'+ "Castling: Once per game, as long as; " +
                        "the king and the rook in question has not moved, there are no pieces between the king and the rook" +
                        ", and the king is not in check or pass through an attacked square. You may move the king 2 squares" +
                        " towards the rook, and then the rook is moved into the space the king crossed.");
                    break;
                case 1:System.out.println("Queen: It can move any number of pieces horizontally, or vertically," +
                        " and can move any number of squares diagonally, but cannot jump over pieces.");
                    break;
                case 2: System.out.println("Bishop: It can move any number of squares diagonally, but cannot jump" +
                        " over pieces.");
                    break;
                case 3: System.out.println("Knight: It moves in a pattern of moving two squares horizontally" +
                        " and one either direction vertically, or, two squares vertically and one either direction " +
                        "horizontally.");
                    break;
                case 4: System.out.println("Rook: It can move any number of pieces horizontally, " +
                        "or vertically, but cannot jump over pieces; this piece is also involved in Castling." +'\n'+'\n' +
                        "Castling: Once per game, as long as; the king and the rook in question has not moved, there are" +
                        " no pieces between the king and the rook, and the king is not in check or pass through an attacked" +
                        " square. You may move the king 2 squares towards the rook, and then the rook is moved into the space" +
                        " the king crossed.");
                    break;
                case 5: System.out.println("Pawn: It moves forward one space at a time, unless at its starting" +
                        " position, in which case it can move either two or one. It can capture pieces diagonally forward" +
                        " to it by jumping to that pieces position and replacing it. This piece is involved in both En " +
                        "Passant, and Promotion." + '\n'+'\n'+ "En Passant: When you move a pawn past another opposing" +
                        " pawn in an adjacent column the opposing pawn can take their immediate turn to take the piece," +
                        " or lose the chance to do so if it is not taken the exact next turn." + '\n'+'\n'+ "Promotion: When" +
                        " a pawn has crossed the board to the farthest opposing row, when it reaches that row it is turned" +
                        " into another piece with a choice of a, Queen, Rook, Bishop, or Knight. This new piece can exceed" +
                        " the normal amount of pieces on the board (i.e 2 queens, 3 knights etc.)");
                    break;
                case 6:
                    System.out.println("Chess is a game played by two players on a 8 by 8 grid board with light  and dark" +
                            " squares, that is either won or drawn in the end. There are two sets, one set for each player," +
                            " named White and Black; they each individually have 16 pieces. Which are a King, Queen, Bishop," +
                            " Knight, Rook, and Pawn. Each has its own moveset and separate rules. \n" + "King: It moves one" +
                            " square in any direction. Is the most valuable piece, and if it is ever attacked it must be " +
                            "countered immediately, and if it is impossible, the game is lost. Piece is also involved in " +
                            "Castling.\n" + "Rook: It can move any number of pieces horizontally, or vertically, but cannot" +
                            " jump over pieces; this piece is also involved in Castling.\n" + "Bishop: It can move any number" +
                            " of squares diagonally, but cannot jump over pieces.\n" + "Queen: It combines both Rook’s and" +
                            " Bishop’s movement, but cannot jump over pieces.\n" + "Knight: It moves in a pattern of moving" +
                            " two squares horizontally and one either direction vertically, or, two squares vertically and" +
                            " one either direction horizontally.\n" + "Pawn: It moves forward one space at a time, unless at" +
                            " its starting position, in which case it can move either two or one. It can capture pieces " +
                            "diagonally forward to it by jumping to that pieces position and replacing it. This piece is " +
                            "involved in both En Passant, and Promotion.\n" + "Alternate moves for certain pieces include" +
                            " “Castling”, “En Passant”, and “Promotion”. \n" + "Castling: Once per game, as long as; the " +
                            "king and the rook in question has not moved, there are no pieces between the king and the rook, " +
                            "and the king is not in check or pass through an attacked square. You may move the king 2 squares" +
                            " towards the rook, and then the rook is moved into the space the king crossed.\n" + "\tEn Passant:" +
                            " When you move a pawn past another opposing pawn in an adjacent column the opposing pawn can take" +
                            " their immediate turn to take the piece, or lose the chance to do so if it is not taken the " +
                            "exact next turn.\n" + "\tPromotion: When a pawn has crossed the board to the farthest opposing" +
                            " row, when it reaches that row it is turned into another piece with a choice of a, Queen, Rook," +
                            " Bishop, or Knight. This new piece can exceed the normal amount of pieces on the board " +
                            "(i.e 2 queens, 3 knights etc.)\n" + "The game can be won by checkmate, resignation, or a “win on" +
                            " time”, as well as by “Draw”, by a variety of ways.\n" + "Winning:\n" + "Check and Checkmate: " +
                            "check is the term used when a king is under attack, and means that the next term must get it " +
                            "out of danger. This is done in 3 ways, moving the king, capturing the attacking piece, and " +
                            "putting a piece in between the king and the attacking piece.\n" + "Resignation: A player " +
                            "concedes the game to an opponent, and if the opponent has a way to checkmate, then this is" +
                            " a win for the opponent; otherwise the game ends in a draw if no checkmate is found.\n" + "Win" +
                            " on Time: A player wins on time if the game has clocks running, and their opponent runs out" +
                            " of time. This ends the game and the player with time remaining on their clock wins.\n" +
                            " Draw:\n" + "\tStalemate: If a player has no legal moves, but isn’t in check, and the position" +
                            " is stalemate, the game is drawn.\n" + "Dead Position: If neither of the players is able" +
                            " to checkmate the other by any sequence of legal moves.\n" + "Draw by Agreement: When both" +
                            " players verbally agree to have the game end in draw, if offered by one player and the other" +
                            " agrees, the party in agreement has one move to gain checkmate, otherwise the game ends in" +
                            " draw.\t\n" + "Threefold Repetition: It is an agreement where both players are not able to" +
                            " avoid repeating moves without taking a heavy disadvantage. Generally it is when both repeat" +
                            " three times and then one player offers the draw. There is another version called the Fivefold" +
                            " repetition, where if five repetitions are found the arbiter ends the game.\n" + "Fifty-move" +
                            " Rule: if in 50 previous turns, neither player has moved a pawn or taken a piece, a draw can" +
                            " be offered up.\n" + "Draw on time: In a game with time, if one player runs out of time, but" +
                            " their opponent has no sequence of moves that would allow them to win, the game ends in a" +
                            " draw.\n" + "Draw by resignation: a game is drawn if a player resigns and their opponent " +
                            "does not have a sequence of moves to get a checkmate.");
                    break;
            }
        }
    }
}

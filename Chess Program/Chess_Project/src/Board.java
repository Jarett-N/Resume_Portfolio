/* Jarett Nelson
 * Assingment 6, Spring 2024
 * CS245
 * The purpose of Board.java is to create, set positions for, set up rules for, and runs all the pieces in chess.
 * It is the place where the board itself is created.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class Board extends JFrame implements ActionListener{

    //block of stuff to set up things later in the
    JFrame frame = new JFrame();
    JPanel[][] board = new JPanel[8][8];
    JButton[][] instructions = new JButton[8][8];
    LinkedList<Integer> list = new LinkedList<>();
    Random ran = new Random();
    ColorView[][] colorView = new  ColorView[8][8];
    boolean stop;


    //White piece buttons
    Piece wKing = new Piece(true);
    Piece wQueen = new Piece(true);
    Piece wBishop1 = new Piece(true);
    Piece wBishop2 = new Piece(true);
    Piece wKnight1 = new Piece(true);
    Piece wKnight2 = new Piece(true);
    Piece wRook1 = new Piece(true);
    Piece wRook2 = new Piece(true);
    Piece wPawn1 = new Piece(true);
    Piece wPawn2 = new Piece(true);
    Piece wPawn3 = new Piece(true);
    Piece wPawn4 = new Piece(true);
    Piece wPawn5 = new Piece(true);
    Piece wPawn6 = new Piece(true);
    Piece wPawn7 = new Piece(true);
    Piece wPawn8 = new Piece(true);

    //Black piece buttons
    Piece bKing = new Piece(false);
    Piece bQueen = new Piece(false);
    Piece bBishop1 = new Piece(false);
    Piece bBishop2 = new Piece(false);
    Piece bKnight1 = new Piece(false);
    Piece bKnight2 = new Piece(false);
    Piece bRook1 = new Piece(false);
    Piece bRook2 = new Piece(false);
    Piece bPawn1 = new Piece(false);
    Piece bPawn2 = new Piece(false);
    Piece bPawn3 = new Piece(false);
    Piece bPawn4 = new Piece(false);
    Piece bPawn5 = new Piece(false);
    Piece bPawn6 = new Piece(false);
    Piece bPawn7 = new Piece(false);
    Piece bPawn8 = new Piece(false);



    //sets up the board in a grid layout and adds the colors
    Board(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,800);
        frame.setTitle("Chess");
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.getContentPane().setForeground(Color.CYAN);
        frame.setVisible(true);
        frame.setLayout(new GridLayout(8,8));

        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = new JPanel();
                if((i%2) == 0){
                    if((j%2) == 0){
                        board[i][j].setBackground(new Color(233,220,201));
                    }
                    else{
                        board[i][j].setBackground(new Color(54, 69, 79));
                    }
                }
                else{
                    if((j%2) == 0){
                        board[i][j].setBackground(new Color(54, 69, 79));
                    }
                    else{
                        board[i][j].setBackground(new Color(233,220,201));
                    }
                }
                //creates buttons for the panels
                instructions[i][j] = new JButton();
                instructions[i][j].setOpaque(false);
                instructions[i][j].setContentAreaFilled(false);
                instructions[i][j].setBorderPainted(false);
                instructions[i][j].addActionListener(e -> new Instructions(6));


                board[i][j].setLayout(new GridLayout(1,1));
                board[i][j].add(instructions[i][j]);
                frame.add(board[i][j]);

                colorView[i][j] = new ColorView();
                colorView[i][j].setColor(0);
            }
        }

        getUnique();

        // white kings piece information
        wKing.getPiece().addActionListener(this);
        wKing.getPiece().setText("K");
        wKing.setXY(list.get(0));
        board[wKing.getX()][wKing.getY()].remove(instructions[wKing.getX()][wKing.getY()]);
        board[wKing.getX()][wKing.getY()].add(wKing.getPiece());
        colorView[wKing.getX()][wKing.getY()].setColor(1);

        // white queens piece information
        wQueen.getPiece().addActionListener(this);
        wQueen.getPiece().setText("Q");
        wQueen.setXY(list.get(1));
        board[wQueen.getX()][wQueen.getY()].remove(instructions[wQueen.getX()][wQueen.getY()]);
        board[wQueen.getX()][wQueen.getY()].add(wQueen.getPiece());
        colorView[wQueen.getX()][wQueen.getY()].setColor(1);

        // white bishop1's piece information
        wBishop1.getPiece().addActionListener(this);
        wBishop1.getPiece().setText("B");
        wBishop1.setXY(list.get(2));
        board[wBishop1.getX()][wBishop1.getY()].remove(instructions[wBishop1.getX()][wBishop1.getY()]);
        board[wBishop1.getX()][wBishop1.getY()].add(wBishop1.getPiece());
        colorView[wBishop1.getX()][wBishop1.getY()].setColor(1);

        // white bishop2's piece information
        wBishop2.getPiece().addActionListener(this);
        wBishop2.getPiece().setText("B");
        wBishop2.setXY(list.get(3));
        board[wBishop2.getX()][wBishop2.getY()].remove(instructions[wBishop2.getX()][wBishop2.getY()]);
        board[wBishop2.getX()][wBishop2.getY()].add(wBishop2.getPiece());
        colorView[wBishop2.getX()][wBishop2.getY()].setColor(1);

        // white Knight1's piece information
        wKnight1.getPiece().addActionListener(this);
        wKnight1.getPiece().setText("KN");
        wKnight1.setXY(list.get(4));
        board[wKnight1.getX()][wKnight1.getY()].remove(instructions[wKnight1.getX()][wKnight1.getY()]);
        board[wKnight1.getX()][wKnight1.getY()].add(wKnight1.getPiece());
        colorView[wKnight1.getX()][wKnight1.getY()].setColor(1);

        // white Knight2's piece information
        wKnight2.getPiece().addActionListener(this);
        wKnight2.getPiece().setText("KN");
        wKnight2.setXY(list.get(5));
        board[wKnight2.getX()][wKnight2.getY()].remove(instructions[wKnight2.getX()][wKnight2.getY()]);
        board[wKnight2.getX()][wKnight2.getY()].add(wKnight2.getPiece());
        colorView[wKnight2.getX()][wKnight2.getY()].setColor(1);

        // white Rook1's piece information
        wRook1.getPiece().addActionListener(this);
        wRook1.getPiece().setText("R");
        wRook1.setXY(list.get(6));
        board[wRook1.getX()][wRook1.getY()].remove(instructions[wRook1.getX()][wRook1.getY()]);
        board[wRook1.getX()][wRook1.getY()].add(wRook1.getPiece());
        colorView[wRook1.getX()][wRook1.getY()].setColor(1);

        // white Rook2's piece information
        wRook2.getPiece().addActionListener(this);
        wRook2.getPiece().setText("R");
        wRook2.setXY(list.get(7));
        board[wRook2.getX()][wRook2.getY()].remove(instructions[wRook2.getX()][wRook2.getY()]);
        board[wRook2.getX()][wRook2.getY()].add(wRook2.getPiece());
        colorView[wRook2.getX()][wRook2.getY()].setColor(1);

        // white Pawn1's piece information
        wPawn1.getPiece().addActionListener(this);
        wPawn1.getPiece().setText("P1");
        wPawn1.setXY(list.get(8));
        board[wPawn1.getX()][wPawn1.getY()].remove(instructions[wPawn1.getX()][wPawn1.getY()]);
        board[wPawn1.getX()][wPawn1.getY()].add(wPawn1.getPiece());
        colorView[wPawn1.getX()][wPawn1.getY()].setColor(1);

        // white Pawn2's piece information
        wPawn2.getPiece().addActionListener(this);
        wPawn2.getPiece().setText("P2");
        wPawn2.setXY(list.get(9));
        board[wPawn2.getX()][wPawn2.getY()].remove(instructions[wPawn2.getX()][wPawn2.getY()]);
        board[wPawn2.getX()][wPawn2.getY()].add(wPawn2.getPiece());
        colorView[wPawn2.getX()][wPawn2.getY()].setColor(1);

        // white Pawn3's piece information
        wPawn3.getPiece().addActionListener(this);
        wPawn3.getPiece().setText("P3");
        wPawn3.setXY(list.get(10));
        board[wPawn3.getX()][wPawn3.getY()].remove(instructions[wPawn3.getX()][wPawn3.getY()]);
        board[wPawn3.getX()][wPawn3.getY()].add(wPawn3.getPiece());
        colorView[wPawn3.getX()][wPawn3.getY()].setColor(1);


        // white Pawn4's piece information
        wPawn4.getPiece().addActionListener(this);
        wPawn4.getPiece().setText("P4");
        wPawn4.setXY(list.get(11));
        board[wPawn4.getX()][wPawn4.getY()].remove(instructions[wPawn4.getX()][wPawn4.getY()]);
        board[wPawn4.getX()][wPawn4.getY()].add(wPawn4.getPiece());
        colorView[wPawn4.getX()][wPawn4.getY()].setColor(1);


        // white Pawn5's piece information
        wPawn5.getPiece().addActionListener(this);
        wPawn5.getPiece().setText("P5");
        wPawn5.setXY(list.get(12));
        board[wPawn5.getX()][wPawn5.getY()].remove(instructions[wPawn5.getX()][wPawn5.getY()]);
        board[wPawn5.getX()][wPawn5.getY()].add(wPawn5.getPiece());
        colorView[wPawn5.getX()][wPawn5.getY()].setColor(1);


        // white Pawn6's piece information
        wPawn6.getPiece().addActionListener(this);
        wPawn6.getPiece().setText("P6");
        wPawn6.setXY(list.get(13));
        board[wPawn6.getX()][wPawn6.getY()].remove(instructions[wPawn6.getX()][wPawn6.getY()]);
        board[wPawn6.getX()][wPawn6.getY()].add(wPawn6.getPiece());
        colorView[wPawn6.getX()][wPawn6.getY()].setColor(1);


        // white Pawn7's piece information
        wPawn7.getPiece().addActionListener(this);
        wPawn7.getPiece().setText("P7");
        wPawn7.setXY(list.get(14));
        board[wPawn7.getX()][wPawn7.getY()].remove(instructions[wPawn7.getX()][wPawn7.getY()]);
        board[wPawn7.getX()][wPawn7.getY()].add(wPawn7.getPiece());
        colorView[wPawn7.getX()][wPawn7.getY()].setColor(1);


        // white Pawn8's piece information
        wPawn8.getPiece().addActionListener(this);
        wPawn8.getPiece().setText("P8");
        wPawn8.setXY(list.get(15));
        board[wPawn8.getX()][wPawn8.getY()].remove(instructions[wPawn8.getX()][wPawn8.getY()]);
        board[wPawn8.getX()][wPawn8.getY()].add(wPawn8.getPiece());
        colorView[wPawn8.getX()][wPawn8.getY()].setColor(1);


        //set black king
        bKing.getPiece().addActionListener(this);
        bKing.getPiece().setText("K");
        bKing.setXY(list.get(16));
        board[bKing.getX()][bKing.getY()].remove(instructions[bKing.getX()][bKing.getY()]);
        board[bKing.getX()][bKing.getY()].add(bKing.getPiece());
        colorView[bKing.getX()][bKing.getY()].setColor(2);


        // black queens piece information
        bQueen.getPiece().addActionListener(this);
        bQueen.getPiece().setText("Q");
        bQueen.setXY(list.get(17));
        board[bQueen.getX()][bQueen.getY()].remove(instructions[bQueen.getX()][bQueen.getY()]);
        board[bQueen.getX()][bQueen.getY()].add(bQueen.getPiece());
        colorView[bQueen.getX()][bQueen.getY()].setColor(2);

        // black bishop1's piece information
        bBishop1.getPiece().addActionListener(this);
        bBishop1.getPiece().setText("B");
        bBishop1.setXY(list.get(18));
        board[bBishop1.getX()][bBishop1.getY()].remove(instructions[bBishop1.getX()][bBishop1.getY()]);
        board[bBishop1.getX()][bBishop1.getY()].add(bBishop1.getPiece());
        colorView[bBishop1.getX()][bBishop1.getY()].setColor(2);

        // black bishop2's piece information
        bBishop2.getPiece().addActionListener(this);
        bBishop2.getPiece().setText("B");
        bBishop2.setXY(list.get(19));
        board[bBishop2.getX()][bBishop2.getY()].remove(instructions[bBishop2.getX()][bBishop2.getY()]);
        board[bBishop2.getX()][bBishop2.getY()].add(bBishop2.getPiece());
        colorView[bBishop2.getX()][bBishop2.getY()].setColor(2);

        // black Knight1's piece information
        bKnight1.getPiece().addActionListener(this);
        bKnight1.getPiece().setText("KN");
        bKnight1.setXY(list.get(20));
        board[bKnight1.getX()][bKnight1.getY()].remove(instructions[bKnight1.getX()][bKnight1.getY()]);
        board[bKnight1.getX()][bKnight1.getY()].add(bKnight1.getPiece());
        colorView[bKnight1.getX()][bKnight1.getY()].setColor(2);

        // black Knight2's piece information
        bKnight2.getPiece().addActionListener(this);
        bKnight2.getPiece().setText("KN");
        bKnight2.setXY(list.get(21));
        board[bKnight2.getX()][bKnight2.getY()].remove(instructions[bKnight2.getX()][bKnight2.getY()]);
        board[bKnight2.getX()][bKnight2.getY()].add(bKnight2.getPiece());
        colorView[bKnight2.getX()][bKnight2.getY()].setColor(2);

        // black rook1's piece information
        bRook1.getPiece().addActionListener(this);
        bRook1.getPiece().setText("R");
        bRook1.setXY(list.get(22));
        board[bRook1.getX()][bRook1.getY()].remove(instructions[bRook1.getX()][bRook1.getY()]);
        board[bRook1.getX()][bRook1.getY()].add(bRook1.getPiece());
        colorView[bRook1.getX()][bRook1.getY()].setColor(2);

        // black rook2's piece information
        bRook2.getPiece().addActionListener(this);
        bRook2.getPiece().setText("R");
        bRook2.setXY(list.get(23));
        board[bRook2.getX()][bRook2.getY()].remove(instructions[bRook2.getX()][bRook2.getY()]);
        board[bRook2.getX()][bRook2.getY()].add(bRook2.getPiece());
        colorView[bRook2.getX()][bRook2.getY()].setColor(2);

        // black pawn1's piece information
        bPawn1.getPiece().addActionListener(this);
        bPawn1.getPiece().setText("P1");
        bPawn1.setXY(list.get(24));
        board[bPawn1.getX()][bPawn1.getY()].remove(instructions[bPawn1.getX()][bPawn1.getY()]);
        board[bPawn1.getX()][bPawn1.getY()].add(bPawn1.getPiece());
        colorView[bPawn1.getX()][bPawn1.getY()].setColor(2);

        // black pawn2's piece information
        bPawn2.getPiece().addActionListener(this);
        bPawn2.getPiece().setText("P2");
        bPawn2.setXY(list.get(25));
        board[bPawn2.getX()][bPawn2.getY()].remove(instructions[bPawn2.getX()][bPawn2.getY()]);
        board[bPawn2.getX()][bPawn2.getY()].add(bPawn2.getPiece());
        colorView[bPawn2.getX()][bPawn2.getY()].setColor(2);

        // black pawn3's piece information
        bPawn3.getPiece().addActionListener(this);
        bPawn3.getPiece().setText("P3");
        bPawn3.setXY(list.get(26));
        board[bPawn3.getX()][bPawn3.getY()].remove(instructions[bPawn3.getX()][bPawn3.getY()]);
        board[bPawn3.getX()][bPawn3.getY()].add(bPawn3.getPiece());
        colorView[bPawn3.getX()][bPawn3.getY()].setColor(2);

        // black pawn4's piece information
        bPawn4.getPiece().addActionListener(this);
        bPawn4.getPiece().setText("P4");
        bPawn4.setXY(list.get(27));
        board[bPawn4.getX()][bPawn4.getY()].remove(instructions[bPawn4.getX()][bPawn4.getY()]);
        board[bPawn4.getX()][bPawn4.getY()].add(bPawn4.getPiece());
        colorView[bPawn4.getX()][bPawn4.getY()].setColor(2);

        // black pawn5's piece information
        bPawn5.getPiece().addActionListener(this);
        bPawn5.getPiece().setText("P5");
        bPawn5.setXY(list.get(28));
        board[bPawn5.getX()][bPawn5.getY()].remove(instructions[bPawn5.getX()][bPawn5.getY()]);
        board[bPawn5.getX()][bPawn5.getY()].add(bPawn5.getPiece());
        colorView[bPawn5.getX()][bPawn5.getY()].setColor(2);

        // black pawn6's piece information
        bPawn6.getPiece().addActionListener(this);
        bPawn6.getPiece().setText("P6");
        bPawn6.setXY(list.get(29));
        board[bPawn6.getX()][bPawn6.getY()].remove(instructions[bPawn6.getX()][bPawn6.getY()]);
        board[bPawn6.getX()][bPawn6.getY()].add(bPawn6.getPiece());
        colorView[bPawn6.getX()][bPawn6.getY()].setColor(2);

        // black pawn7's piece information
        bPawn7.getPiece().addActionListener(this);
        bPawn7.getPiece().setText("P7");
        bPawn7.setXY(list.get(30));
        board[bPawn7.getX()][bPawn7.getY()].remove(instructions[bPawn7.getX()][bPawn7.getY()]);
        board[bPawn7.getX()][bPawn7.getY()].add(bPawn7.getPiece());
        colorView[bPawn7.getX()][bPawn7.getY()].setColor(2);

        // black pawn8's piece information
        bPawn8.getPiece().addActionListener(this);
        bPawn8.getPiece().setText("P8");
        bPawn8.setXY(list.get(31));
        board[bPawn8.getX()][bPawn8.getY()].remove(instructions[bPawn8.getX()][bPawn8.getY()]);
        board[bPawn8.getX()][bPawn8.getY()].add(bPawn8.getPiece());
        colorView[bPawn8.getX()][bPawn8.getY()].setColor(2);
    }

    //it gets 32 unique numbers for the pieces
    void getUnique(){
        HashSet<Integer> set = new HashSet<>();

        while(set.size() != 32){
            set.add(ran.nextInt(0,64));
        }
        list = new LinkedList<>(set);
        Collections.shuffle(list);
    }

    //it resets the board color to the black and white grids
    void resetBoardColor(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if((i%2) == 0){
                    if((j%2) == 0){
                        board[i][j].setBackground(new Color(233,220,201));
                    }
                    else{
                        board[i][j].setBackground(new Color(54, 69, 79));
                    }
                }
                else{
                    if((j%2) == 0){
                        board[i][j].setBackground(new Color(54, 69, 79));
                    }
                    else{
                        board[i][j].setBackground(new Color(233,220,201));
                    }
                }
            }
        }
    }

    //
    @Override
    public void actionPerformed(ActionEvent e) {

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == wPawn1.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(wPawn1.getX()-1 != -1){
                if(colorView[wPawn1.getX()-1][wPawn1.getY()].getColor() == 0) {
                    board[wPawn1.getX() - 1][wPawn1.getY()].setBackground(new Color(235, 25, 35));
                }
                if(wPawn1.getY()-1 != -1){
                    if(colorView[wPawn1.getX()-1][wPawn1.getY()-1].getColor() == 2) {
                        board[wPawn1.getX()-1][wPawn1.getY()-1].setBackground(new Color(235, 25, 35));
                    }
                }
                if(wPawn1.getY()+1 != 8){
                    if(colorView[wPawn1.getX()-1][wPawn1.getY()+1].getColor() == 2) {
                        board[wPawn1.getX()-1][wPawn1.getY()+1].setBackground(new Color(235, 25, 35));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == wPawn2.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(wPawn2.getX()-1 != -1){
                if(colorView[wPawn2.getX()-1][wPawn2.getY()].getColor() == 0) {
                    board[wPawn2.getX()-1][wPawn2.getY()].setBackground(new Color(235, 25, 55));
                }
                if(wPawn2.getY()-1 != -1){
                    if(colorView[wPawn2.getX()-1][wPawn2.getY()-1].getColor() == 2) {
                        board[wPawn2.getX()-1][wPawn2.getY()-1].setBackground(new Color(235, 25, 55));
                    }
                }
                if(wPawn2.getY()+1 != 8){
                    if(colorView[wPawn2.getX()-1][wPawn2.getY()+1].getColor() == 2) {
                        board[wPawn2.getX()-1][wPawn2.getY()+1].setBackground(new Color(235, 25, 55));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == wPawn3.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(wPawn3.getX()-1 != -1){
                if(colorView[wPawn3.getX()-1][wPawn3.getY()].getColor() == 0) {
                    board[wPawn3.getX()-1][wPawn3.getY()].setBackground(new Color(235, 25, 75));
                }
                if(wPawn3.getY()-1 != -1){
                    if(colorView[wPawn3.getX()-1][wPawn3.getY()-1].getColor() == 2) {
                        board[wPawn3.getX()-1][wPawn3.getY()-1].setBackground(new Color(235, 25, 75));
                    }
                }
                if(wPawn3.getY()+1 != 8){
                    if(colorView[wPawn3.getX()-1][wPawn3.getY()+1].getColor() == 2) {
                        board[wPawn3.getX()-1][wPawn3.getY()+1].setBackground(new Color(235, 25, 75));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == wPawn4.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(wPawn4.getX()-1 != -1){
                if(colorView[wPawn4.getX()-1][wPawn4.getY()].getColor() == 0){
                    board[wPawn4.getX()-1][wPawn4.getY()].setBackground(new Color(235, 25, 95));
                }
                if(wPawn4.getY()-1 != -1){
                    if(colorView[wPawn4.getX()-1][wPawn4.getY()-1].getColor() == 2) {
                        board[wPawn4.getX()-1][wPawn4.getY()-1].setBackground(new Color(235, 25, 95));
                    }
                }
                if(wPawn4.getY()+1 != 8){
                    if(colorView[wPawn4.getX()-1][wPawn4.getY()+1].getColor() == 2) {
                        board[wPawn4.getX()-1][wPawn4.getY()+1].setBackground(new Color(235, 25, 95));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == wPawn5.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(wPawn5.getX()-1 != -1){
                if(colorView[wPawn5.getX()-1][wPawn5.getY()].getColor() == 0) {
                    board[wPawn5.getX() - 1][wPawn5.getY()].setBackground(new Color(235, 25, 115));
                }
                if(wPawn5.getY()-1 != -1){
                    if(colorView[wPawn5.getX()-1][wPawn5.getY()-1].getColor() == 2) {
                        board[wPawn5.getX()-1][wPawn5.getY()-1].setBackground(new Color(235, 25, 115));
                    }
                }
                if(wPawn5.getY()+1 != 8){
                    if(colorView[wPawn5.getX()-1][wPawn5.getY()+1].getColor() == 2) {
                        board[wPawn5.getX()-1][wPawn5.getY()+1].setBackground(new Color(235, 25, 115));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == wPawn6.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(wPawn6.getX()-1 != -1){
                if(colorView[wPawn6.getX()-1][wPawn6.getY()].getColor() == 0) {
                    board[wPawn6.getX() - 1][wPawn6.getY()].setBackground(new Color(235, 25, 135));
                }
                if(wPawn6.getY()-1 != -1){
                    if(colorView[wPawn6.getX()-1][wPawn6.getY()-1].getColor() == 2) {
                        board[wPawn6.getX()-1][wPawn6.getY()-1].setBackground(new Color(235, 25, 135));
                    }
                }
                if(wPawn6.getY()+1 != 8){
                    if(colorView[wPawn6.getX()-1][wPawn6.getY()+1].getColor() == 2) {
                        board[wPawn6.getX()-1][wPawn6.getY()+1].setBackground(new Color(235, 25, 135));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == wPawn7.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(wPawn7.getX()-1 != -1){
                if(colorView[wPawn7.getX()-1][wPawn7.getY()].getColor() == 0) {
                    board[wPawn7.getX() - 1][wPawn7.getY()].setBackground(new Color(235, 25, 155));
                }
                if(wPawn7.getY()-1 != -1){
                    if(colorView[wPawn7.getX()-1][wPawn7.getY()-1].getColor() == 2) {
                        board[wPawn7.getX()-1][wPawn7.getY()-1].setBackground(new Color(235, 25, 155));
                    }
                }
                if(wPawn7.getY()+1 != 8){
                    if(colorView[wPawn7.getX()-1][wPawn7.getY()+1].getColor() == 2) {
                        board[wPawn7.getX()-1][wPawn7.getY()+1].setBackground(new Color(235, 25, 155));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == wPawn8.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(wPawn8.getX()-1 != -1){
                if(colorView[wPawn8.getX()-1][wPawn8.getY()].getColor() == 0) {
                    board[wPawn8.getX()-1][wPawn8.getY()].setBackground(new Color(235,25,175));
                }
                if(wPawn8.getY()-1 != -1){
                    if(colorView[wPawn8.getX()-1][wPawn8.getY()-1].getColor() == 2) {
                        board[wPawn8.getX()-1][wPawn8.getY()-1].setBackground(new Color(235, 25, 175));
                    }
                }
                if(wPawn8.getY()+1 != 8){
                    if(colorView[wPawn8.getX()-1][wPawn8.getY()+1].getColor() == 2) {
                        board[wPawn8.getX()-1][wPawn8.getY()+1].setBackground(new Color(235, 25, 175));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == bPawn1.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(bPawn1.getX()+1 != 8){
                if(colorView[bPawn1.getX()+1][bPawn1.getY()].getColor() == 0) {
                    board[bPawn1.getX() + 1][bPawn1.getY()].setBackground(new Color(235, 25, 35));
                }
                if(bPawn1.getY()-1 != -1){
                    if(colorView[bPawn1.getX()+1][bPawn1.getY()-1].getColor() == 1) {
                        board[bPawn1.getX() + 1][bPawn1.getY()-1].setBackground(new Color(235, 25, 35));
                    }
                }
                if(bPawn1.getY()+1 != 8){
                    if(colorView[bPawn1.getX()+1][bPawn1.getY()+1].getColor() == 1) {
                        board[bPawn1.getX() + 1][bPawn1.getY()+1].setBackground(new Color(235, 25, 35));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == bPawn2.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(bPawn2.getX()+1 != 8){
                if(colorView[bPawn2.getX()+1][bPawn2.getY()].getColor() == 0) {
                    board[bPawn2.getX() + 1][bPawn2.getY()].setBackground(new Color(235, 45, 35));
                }
                if(bPawn2.getY()-1 != -1){
                    if(colorView[bPawn2.getX()+1][bPawn2.getY()-1].getColor() == 1) {
                        board[bPawn2.getX() + 1][bPawn2.getY()-1].setBackground(new Color(235, 45, 35));
                    }
                }
                if(bPawn2.getY()+1 != 8){
                    if(colorView[bPawn2.getX()+1][bPawn2.getY()+1].getColor() == 1) {
                        board[bPawn2.getX() + 1][bPawn2.getY()+1].setBackground(new Color(235, 45, 35));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == bPawn3.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(bPawn3.getX()+1 != 8){
                if(colorView[bPawn3.getX()+1][bPawn3.getY()].getColor() == 0) {
                    board[bPawn3.getX() + 1][bPawn3.getY()].setBackground(new Color(235, 65, 35));
                }
                if(bPawn3.getY()-1 != -1){
                    if(colorView[bPawn3.getX()+1][bPawn3.getY()-1].getColor() == 1) {
                        board[bPawn3.getX() + 1][bPawn3.getY()-1].setBackground(new Color(235, 65, 35));
                    }
                }
                if(bPawn3.getY()+1 != 8){
                    if(colorView[bPawn3.getX()+1][bPawn3.getY()+1].getColor() == 1) {
                        board[bPawn3.getX() + 1][bPawn3.getY()+1].setBackground(new Color(235, 65, 35));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == bPawn4.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(bPawn4.getX()+1 != 8){
                if(colorView[bPawn4.getX()+1][bPawn4.getY()].getColor() == 0) {
                    board[bPawn4.getX() + 1][bPawn4.getY()].setBackground(new Color(235, 85, 35));
                }
                if(bPawn4.getY()-1 != -1){
                    if(colorView[bPawn4.getX()+1][bPawn4.getY()-1].getColor() == 1) {
                        board[bPawn4.getX() + 1][bPawn4.getY()-1].setBackground(new Color(235, 85, 35));
                    }
                }
                if(bPawn4.getY()+1 != 8){
                    if(colorView[bPawn4.getX()+1][bPawn4.getY()+1].getColor() == 1) {
                        board[bPawn4.getX() + 1][bPawn4.getY()+1].setBackground(new Color(235, 85, 35));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == bPawn5.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(bPawn5.getX()+1 != 8){
                if(colorView[bPawn5.getX()+1][bPawn5.getY()].getColor() == 0) {
                    board[bPawn5.getX() + 1][bPawn5.getY()].setBackground(new Color(235, 105, 35));
                }
                if(bPawn5.getY()-1 != -1){
                    if(colorView[bPawn5.getX()+1][bPawn5.getY()-1].getColor() == 1) {
                        board[bPawn5.getX() + 1][bPawn5.getY()-1].setBackground(new Color(235, 105, 35));
                    }
                }
                if(bPawn5.getY()+1 != 8){
                    if(colorView[bPawn5.getX()+1][bPawn5.getY()+1].getColor() == 1) {
                        board[bPawn5.getX() + 1][bPawn5.getY()+1].setBackground(new Color(235, 105, 35));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == bPawn6.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(bPawn6.getX()+1 != 8){
                if(colorView[bPawn6.getX()+1][bPawn6.getY()].getColor() == 0) {
                    board[bPawn6.getX() + 1][bPawn6.getY()].setBackground(new Color(235, 125, 35));
                }
                if(bPawn6.getY()-1 != -1){
                    if(colorView[bPawn6.getX()+1][bPawn6.getY()-1].getColor() == 1) {
                        board[bPawn6.getX() + 1][bPawn6.getY()-1].setBackground(new Color(235, 125, 35));
                    }
                }
                if(bPawn6.getY()+1 != 8){
                    if(colorView[bPawn6.getX()+1][bPawn6.getY()+1].getColor() == 1) {
                        board[bPawn6.getX() + 1][bPawn6.getY()+1].setBackground(new Color(235, 125, 35));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == bPawn7.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(bPawn7.getX()+1 != 8){
                if(colorView[bPawn7.getX()+1][bPawn7.getY()].getColor() == 0) {
                    board[bPawn7.getX() + 1][bPawn7.getY()].setBackground(new Color(235, 145, 35));
                }
                if(bPawn7.getY()-1 != -1){
                    if(colorView[bPawn7.getX()+1][bPawn7.getY()-1].getColor() == 1) {
                        board[bPawn7.getX() + 1][bPawn7.getY()-1].setBackground(new Color(235, 145, 35));
                    }
                }
                if(bPawn7.getY()+1 != 8){
                    if(colorView[bPawn7.getX()+1][bPawn7.getY()+1].getColor() == 1) {
                        board[bPawn7.getX() + 1][bPawn7.getY()+1].setBackground(new Color(235, 145, 35));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the pawns movement, with one in front if there are no pieces/another
        //grid space, if there is a opposite color on either immediate forward diagonal, it can move there as well
        if(e.getSource() == bPawn8.getPiece()){
            new Instructions(5);
            resetBoardColor();

            if(bPawn8.getX()+1 != 8){
                if(colorView[bPawn8.getX()+1][bPawn8.getY()].getColor() == 0) {
                    board[bPawn8.getX() + 1][bPawn8.getY()].setBackground(new Color(235, 165, 35));
                }
                if(bPawn8.getY()-1 != -1){
                    if(colorView[bPawn8.getX()+1][bPawn8.getY()-1].getColor() == 1) {
                        board[bPawn8.getX() + 1][bPawn8.getY()-1].setBackground(new Color(235, 165, 35));
                    }
                }
                if(bPawn8.getY()+1 != 8){
                    if(colorView[bPawn8.getX()+1][bPawn8.getY()+1].getColor() == 1) {
                        board[bPawn8.getX() + 1][bPawn8.getY()+1].setBackground(new Color(235, 165, 35));
                    }
                }
            }
        }

        //it sets up the highlighting grids for the kings movement if there are any open spaces or opposite colors in
        //all grids immediately around it
        if(e.getSource() == wKing.getPiece()) {
            new Instructions(0);
            resetBoardColor();
            if (wKing.getX() - 1 > -1 && wKing.getY() - 1 > -1) {
                if(colorView[wKing.getX() - 1][wKing.getY() - 1].getColor() == 2 ||
                        colorView[wKing.getX() - 1][wKing.getY() - 1].getColor() == 0){
                    board[wKing.getX() - 1][wKing.getY() - 1].setBackground(new Color(82, 41, 99));
                }
            }
            if (wKing.getX() - 1 > -1 && wKing.getY() + 1 < 8) {
                if(colorView[wKing.getX() - 1][wKing.getY() + 1].getColor() == 2 ||
                        colorView[wKing.getX() - 1][wKing.getY() + 1].getColor() == 0) {
                    board[wKing.getX() - 1][wKing.getY() + 1].setBackground(new Color(82, 41, 99));
                }
            }
            if (wKing.getX() + 1 < 8 && wKing.getY() - 1 > -1) {
                if(colorView[wKing.getX() + 1][wKing.getY() - 1].getColor() == 2 ||
                        colorView[wKing.getX() + 1][wKing.getY() - 1].getColor() == 0) {
                    board[wKing.getX() + 1][wKing.getY() - 1].setBackground(new Color(82, 41, 99));
                }
            }
            if (wKing.getX() + 1 < 8 && wKing.getY() + 1 < 8) {
                if(colorView[wKing.getX() + 1][wKing.getY() + 1].getColor() == 2 ||
                        colorView[wKing.getX() + 1][wKing.getY() + 1].getColor() == 0) {
                    board[wKing.getX() + 1][wKing.getY() + 1].setBackground(new Color(82, 41, 99));
                }
            }
            if (wKing.getX() + 1 < 8) {
                if(colorView[wKing.getX() + 1][wKing.getY()].getColor() == 2 ||
                        colorView[wKing.getX() + 1][wKing.getY()].getColor() == 0) {
                    board[wKing.getX() + 1][wKing.getY()].setBackground(new Color(82, 41, 99));
                }
            }
            if (wKing.getX() - 1 > -1) {
                if(colorView[wKing.getX() - 1][wKing.getY()].getColor() == 2 ||
                        colorView[wKing.getX() - 1][wKing.getY()].getColor() == 0) {
                    board[wKing.getX() - 1][wKing.getY()].setBackground(new Color(82, 41, 99));
                }
            }
            if (wKing.getY() + 1 < 8) {
                if(colorView[wKing.getX()][wKing.getY() + 1].getColor() == 2 ||
                        colorView[wKing.getX()][wKing.getY() + 1].getColor() == 0) {
                    board[wKing.getX()][wKing.getY() + 1].setBackground(new Color(82, 41, 99));
                }
            }
            if (wKing.getY() - 1 > -1) {
                if(colorView[wKing.getX()][wKing.getY() - 1].getColor() == 2 ||
                        colorView[wKing.getX()][wKing.getY() - 1].getColor() == 0) {
                    board[wKing.getX()][wKing.getY() - 1].setBackground(new Color(82, 41, 99));
                }
            }
        }

        //it sets up the highlighting grids for the kings movement if there are any open spaces or opposite colors in
        //all grids immediately around it
        if(e.getSource() == bKing.getPiece()) {
            new Instructions(0);
            resetBoardColor();
            if (bKing.getX() - 1 > -1 && bKing.getY() - 1 > -1) {
                if(colorView[bKing.getX() - 1][bKing.getY() - 1].getColor() == 1 ||
                        colorView[bKing.getX() - 1][bKing.getY() - 1].getColor() == 0){
                    board[bKing.getX() - 1][bKing.getY() - 1].setBackground(new Color(112, 41, 99));
                }
            }
            if (bKing.getX() - 1 > -1 && bKing.getY() + 1 < 8) {
                if(colorView[bKing.getX() - 1][bKing.getY() + 1].getColor() == 1 ||
                        colorView[bKing.getX() - 1][bKing.getY() + 1].getColor() == 0) {
                    board[bKing.getX() - 1][bKing.getY() + 1].setBackground(new Color(112, 41, 99));
                }
            }
            if (bKing.getX() + 1 < 8 && bKing.getY() - 1 > -1) {
                if(colorView[bKing.getX() + 1][bKing.getY() - 1].getColor() == 1 ||
                        colorView[bKing.getX() + 1][bKing.getY() - 1].getColor() == 0) {
                    board[bKing.getX() + 1][bKing.getY() - 1].setBackground(new Color(112, 41, 99));
                }
            }
            if (bKing.getX() + 1 < 8 && bKing.getY() + 1 < 8) {
                if(colorView[bKing.getX() + 1][bKing.getY() + 1].getColor() == 1 ||
                        colorView[bKing.getX() + 1][bKing.getY() + 1].getColor() == 0) {
                    board[bKing.getX() + 1][bKing.getY() + 1].setBackground(new Color(112, 41, 99));
                }
            }
            if (bKing.getX() + 1 < 8) {
                if(colorView[bKing.getX() + 1][bKing.getY()].getColor() == 1 ||
                        colorView[bKing.getX() + 1][bKing.getY()].getColor() == 0) {
                    board[bKing.getX() + 1][bKing.getY()].setBackground(new Color(112, 41, 99));
                }
            }
            if (bKing.getX() - 1 > -1) {
                if(colorView[bKing.getX() - 1][bKing.getY()].getColor() == 1 ||
                        colorView[bKing.getX() - 1][bKing.getY()].getColor() == 0) {
                    board[bKing.getX() - 1][bKing.getY()].setBackground(new Color(112, 41, 99));
                }
            }
            if (bKing.getY() + 1 < 8) {
                if(colorView[bKing.getX()][bKing.getY() + 1].getColor() == 1 ||
                        colorView[bKing.getX()][bKing.getY() + 1].getColor() == 0) {
                    board[bKing.getX()][bKing.getY() + 1].setBackground(new Color(112, 41, 99));
                }
            }
            if (bKing.getY() - 1 > -1) {
                if(colorView[bKing.getX()][bKing.getY() - 1].getColor() == 1 ||
                        colorView[bKing.getX()][bKing.getY() - 1].getColor() == 0) {
                    board[bKing.getX()][bKing.getY() - 1].setBackground(new Color(112, 41, 99));
                }
            }
        }

        //it sets up the highlighting grids for the queens movement, it uses both the rooks and the bishops movements
        //it stops before any pieces that are the same color, on any pieces that are opposite, and/or on any open spaces
        //until it hits the border
        if(e.getSource() == wQueen.getPiece()){
            new Instructions(1);
            resetBoardColor();
            int i;
            for (i = 1; i < 9; i++) {
                if (wQueen.getX() - i > -1 && wQueen.getY() - i > -1) {
                    if (colorView[wQueen.getX() - i][wQueen.getY() - i].getColor() == 0) {
                        board[wQueen.getX() - i][wQueen.getY() - i].setBackground(new Color(222, 49, 99));
                    } else if (colorView[wQueen.getX() - i][wQueen.getY() - i].getColor() == 2) {
                        board[wQueen.getX() - i][wQueen.getY() - i].setBackground(new Color(222, 49, 99));
                        break;
                    } else break;
                } else break;
            }
            for(i = 1; i < 9; i++){
                if (wQueen.getX()-i > -1 && wQueen.getY()+i < 8){
                    if (colorView[wQueen.getX() - i][wQueen.getY() + i].getColor() == 0) {
                        board[wQueen.getX() - i][wQueen.getY() + i].setBackground(new Color(222, 49, 99));
                    }
                    else if (colorView[wQueen.getX() - i][wQueen.getY() + i].getColor() == 2) {
                        board[wQueen.getX() - i][wQueen.getY() + i].setBackground(new Color(222, 49, 99));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (wQueen.getX()+i < 8 && wQueen.getY()-i > -1){
                    if (colorView[wQueen.getX() + i][wQueen.getY() - i].getColor() == 0) {
                        board[wQueen.getX() + i][wQueen.getY() - i].setBackground(new Color(222, 49, 99));
                    }
                    else if (colorView[wQueen.getX() + i][wQueen.getY() - i].getColor() == 2) {
                        board[wQueen.getX() + i][wQueen.getY() - i].setBackground(new Color(222, 49, 99));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (wQueen.getX()+i < 8 && wQueen.getY()+i < 8){
                    if (colorView[wQueen.getX() + i][wQueen.getY() + i].getColor() == 0) {
                        board[wQueen.getX() + i][wQueen.getY() + i].setBackground(new Color(222, 49, 99));
                    }
                    else if (colorView[wQueen.getX() + i][wQueen.getY() + i].getColor() == 2) {
                        board[wQueen.getX() + i][wQueen.getY() + i].setBackground(new Color(222, 49, 99));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(wQueen.getX()+i < 8){
                    if (colorView[wQueen.getX() + i][wQueen.getY()].getColor() == 0) {
                        board[wQueen.getX() + i][wQueen.getY()].setBackground(new Color(222, 49, 99));
                    }
                    else if (colorView[wQueen.getX() + i][wQueen.getY()].getColor() == 2) {
                        board[wQueen.getX() + i][wQueen.getY()].setBackground(new Color(222, 49, 99));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(wQueen.getX()-i > -1){
                    if (colorView[wQueen.getX() - i][wQueen.getY()].getColor() == 0) {
                        board[wQueen.getX() - i][wQueen.getY()].setBackground(new Color(222, 49, 99));
                    }
                    else if (colorView[wQueen.getX() - i][wQueen.getY()].getColor() == 2) {
                        board[wQueen.getX() - i][wQueen.getY()].setBackground(new Color(222, 49, 99));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++) {
                if (wQueen.getY() + i < 8) {
                    if (colorView[wQueen.getX()][wQueen.getY() + i].getColor() == 0) {
                        board[wQueen.getX()][wQueen.getY() + i].setBackground(new Color(222, 49, 99));
                    }
                    else if (colorView[wQueen.getX()][wQueen.getY() + i].getColor() == 2) {
                        board[wQueen.getX()][wQueen.getY() + i].setBackground(new Color(222, 49, 99));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(wQueen.getY()-i > -1) {
                    if (colorView[wQueen.getX()][wQueen.getY() - i].getColor() == 0) {
                        board[wQueen.getX()][wQueen.getY() - i].setBackground(new Color(222, 49, 99));
                    }
                    else if (colorView[wQueen.getX()][wQueen.getY() - i].getColor() == 2) {
                        board[wQueen.getX()][wQueen.getY() - i].setBackground(new Color(222, 49, 99));
                        break;
                    }
                    else break;
                }
                else break;
            }

        }

        //it sets up the highlighting grids for the queens movement, it uses both the rooks and the bishops movements
        //it stops before any pieces that are the same color, on any pieces that are opposite, and/or on any open spaces
        //until it hits the border
        if(e.getSource() == bQueen.getPiece()){
            new Instructions(1);
            resetBoardColor();
            int i;
            for (i = 1; i < 9; i++) {
                if (bQueen.getX() - i > -1 && bQueen.getY() - i > -1) {
                    if (colorView[bQueen.getX() - i][bQueen.getY() - i].getColor() == 0) {
                        board[bQueen.getX() - i][bQueen.getY() - i].setBackground(new Color(104, 7, 7));
                    } else if (colorView[bQueen.getX() - i][bQueen.getY() - i].getColor() == 1) {
                        board[bQueen.getX() - i][bQueen.getY() - i].setBackground(new Color(104, 7, 7));
                        break;
                    } else break;
                } else break;
            }
            for(i = 1; i < 9; i++){
                if (bQueen.getX()-i > -1 && bQueen.getY()+i < 8){
                    if (colorView[bQueen.getX() - i][bQueen.getY() + i].getColor() == 0) {
                        board[bQueen.getX() - i][bQueen.getY() + i].setBackground(new Color(104, 7, 7));
                    }
                    else if (colorView[bQueen.getX() - i][bQueen.getY() + i].getColor() == 1) {
                        board[bQueen.getX() - i][bQueen.getY() + i].setBackground(new Color(104, 7, 7));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (bQueen.getX()+i < 8 && bQueen.getY()-i > -1){
                    if (colorView[bQueen.getX() + i][bQueen.getY() - i].getColor() == 0) {
                        board[bQueen.getX() + i][bQueen.getY() - i].setBackground(new Color(104, 7, 7));
                    }
                    else if (colorView[bQueen.getX() + i][bQueen.getY() - i].getColor() == 1) {
                        board[bQueen.getX() + i][bQueen.getY() - i].setBackground(new Color(104, 7, 7));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (bQueen.getX()+i < 8 && bQueen.getY()+i < 8){
                    if (colorView[bQueen.getX() + i][bQueen.getY() + i].getColor() == 0) {
                        board[bQueen.getX() + i][bQueen.getY() + i].setBackground(new Color(104, 7, 7));
                    }
                    else if (colorView[bQueen.getX() + i][bQueen.getY() + i].getColor() == 1) {
                        board[bQueen.getX() + i][bQueen.getY() + i].setBackground(new Color(104, 7, 7));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(bQueen.getX()+i < 8){
                    if (colorView[bQueen.getX() + i][bQueen.getY()].getColor() == 0) {
                        board[bQueen.getX() + i][bQueen.getY()].setBackground(new Color(104, 7, 7));
                    }
                    else if (colorView[bQueen.getX() + i][bQueen.getY()].getColor() == 1) {
                        board[bQueen.getX() + i][bQueen.getY()].setBackground(new Color(104, 7, 7));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(bQueen.getX()-i > -1){
                    if (colorView[bQueen.getX() - i][bQueen.getY()].getColor() == 0) {
                        board[bQueen.getX() - i][bQueen.getY()].setBackground(new Color(104, 7, 7));
                    }
                    else if (colorView[bQueen.getX() - i][bQueen.getY()].getColor() == 1) {
                        board[bQueen.getX() - i][bQueen.getY()].setBackground(new Color(104, 7, 7));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++) {
                if (bQueen.getY() + i < 8) {
                    if (colorView[bQueen.getX()][bQueen.getY() + i].getColor() == 0) {
                        board[bQueen.getX()][bQueen.getY() + i].setBackground(new Color(104, 7, 7));
                    }
                    else if (colorView[bQueen.getX()][bQueen.getY() + i].getColor() == 1) {
                        board[bQueen.getX()][bQueen.getY() + i].setBackground(new Color(104, 7, 7));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(bQueen.getY()-i > -1) {
                    if (colorView[bQueen.getX()][bQueen.getY() - i].getColor() == 0) {
                        board[bQueen.getX()][bQueen.getY() - i].setBackground(new Color(104, 7, 7));
                    }
                    else if (colorView[bQueen.getX()][bQueen.getY() - i].getColor() == 1) {
                        board[bQueen.getX()][bQueen.getY() - i].setBackground(new Color(104, 7, 7));
                        break;
                    }
                    else break;
                }
                else break;
            }

        }


        //it sets up the highlighting grids for the bishops movement, it highlights any grids in a diagonal pattern
        //going up right and left, and down left and right, it stops before any pieces that are the same color,
        //on any pieces that are the opposite color, and/or on any empty grid spots until it hits the border
        if(e.getSource() == wBishop1.getPiece()){
            new Instructions(2);
            resetBoardColor();
            int i;
            for (i = 1; i < 9; i++) {
                if (wBishop1.getX() - i > -1 && wBishop1.getY() - i > -1) {
                    if (colorView[wBishop1.getX() - i][wBishop1.getY() - i].getColor() == 0) {
                        board[wBishop1.getX() - i][wBishop1.getY() - i].setBackground(new Color(12, 63, 68));
                    } else if (colorView[wBishop1.getX() - i][wBishop1.getY() - i].getColor() == 2) {
                        board[wBishop1.getX() - i][wBishop1.getY() - i].setBackground(new Color(12, 63, 68));
                        break;
                    } else break;
                } else break;
            }
            for(i = 1; i < 9; i++){
                if (wBishop1.getX()-i > -1 && wBishop1.getY()+i < 8){
                    if (colorView[wBishop1.getX() - i][wBishop1.getY() + i].getColor() == 0) {
                        board[wBishop1.getX() - i][wBishop1.getY() + i].setBackground(new Color(12, 63, 68));
                    }
                    else if (colorView[wBishop1.getX() - i][wBishop1.getY() + i].getColor() == 2) {
                        board[wBishop1.getX() - i][wBishop1.getY() + i].setBackground(new Color(12, 63, 68));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (wBishop1.getX()+i < 8 && wBishop1.getY()-i > -1){
                    if (colorView[wBishop1.getX() + i][wBishop1.getY() - i].getColor() == 0) {
                        board[wBishop1.getX() + i][wBishop1.getY() - i].setBackground(new Color(12, 63, 68));
                    }
                    else if (colorView[wBishop1.getX() + i][wBishop1.getY() - i].getColor() == 2) {
                        board[wBishop1.getX() + i][wBishop1.getY() - i].setBackground(new Color(12, 63, 68));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (wBishop1.getX()+i < 8 && wBishop1.getY()+i < 8){
                    if (colorView[wBishop1.getX() + i][wBishop1.getY() + i].getColor() == 0) {
                        board[wBishop1.getX() + i][wBishop1.getY() + i].setBackground(new Color(12, 63, 68));
                    }
                    else if (colorView[wBishop1.getX() + i][wBishop1.getY() + i].getColor() == 2) {
                        board[wBishop1.getX() + i][wBishop1.getY() + i].setBackground(new Color(12, 63, 68));
                        break;
                    }
                    else break;
                }
                else break;
            }
        }

        //it sets up the highlighting grids for the bishops movement, it highlights any grids in a diagonal pattern
        //going up right and left, and down left and right, it stops before any pieces that are the same color,
        //on any pieces that are the opposite color, and/or on any empty grid spots until it hits the border
        if(e.getSource() == wBishop2.getPiece()){
            new Instructions(2);
            resetBoardColor();
            int i;
            for (i = 1; i < 9; i++) {
                if (wBishop2.getX() - i > -1 && wBishop2.getY() - i > -1) {
                    if (colorView[wBishop2.getX() - i][wBishop2.getY() - i].getColor() == 0) {
                        board[wBishop2.getX() - i][wBishop2.getY() - i].setBackground(new Color(6, 84, 225));
                    } else if (colorView[wBishop2.getX() - i][wBishop2.getY() - i].getColor() == 2) {
                        board[wBishop2.getX() - i][wBishop2.getY() - i].setBackground(new Color(6, 84, 225));
                        break;
                    } else break;
                } else break;
            }
            for(i = 1; i < 9; i++){
                if (wBishop2.getX()-i > -1 && wBishop2.getY()+i < 8){
                    if (colorView[wBishop2.getX() - i][wBishop2.getY() + i].getColor() == 0) {
                        board[wBishop2.getX() - i][wBishop2.getY() + i].setBackground(new Color(6, 84, 225));
                    }
                    else if (colorView[wBishop2.getX() - i][wBishop2.getY() + i].getColor() == 2) {
                        board[wBishop2.getX() - i][wBishop2.getY() + i].setBackground(new Color(6, 84, 225));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (wBishop2.getX()+i < 8 && wBishop2.getY()-i > -1){
                    if (colorView[wBishop2.getX() + i][wBishop2.getY() - i].getColor() == 0) {
                        board[wBishop2.getX() + i][wBishop2.getY() - i].setBackground(new Color(6, 84, 225));
                    }
                    else if (colorView[wBishop2.getX() + i][wBishop2.getY() - i].getColor() == 2) {
                        board[wBishop2.getX() + i][wBishop2.getY() - i].setBackground(new Color(6, 84, 225));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (wBishop2.getX()+i < 8 && wBishop2.getY()+i < 8){
                    if (colorView[wBishop2.getX() + i][wBishop2.getY() + i].getColor() == 0) {
                        board[wBishop2.getX() + i][wBishop2.getY() + i].setBackground(new Color(6, 84, 225));
                    }
                    else if (colorView[wBishop2.getX() + i][wBishop2.getY() + i].getColor() == 2) {
                        board[wBishop2.getX() + i][wBishop2.getY() + i].setBackground(new Color(6, 84, 225));
                        break;
                    }
                    else break;
                }
                else break;
            }
        }

        //it sets up the highlighting grids for the bishops movement, it highlights any grids in a diagonal pattern
        //going up right and left, and down left and right, it stops before any pieces that are the same color,
        //on any pieces that are the opposite color, and/or on any empty grid spots until it hits the border
        if(e.getSource() == bBishop1.getPiece()){
            new Instructions(2);
            resetBoardColor();
            int i;
            for (i = 1; i < 9; i++) {
                if (bBishop1.getX() - i > -1 && bBishop1.getY() - i > -1) {
                    if (colorView[bBishop1.getX() - i][bBishop1.getY() - i].getColor() == 0) {
                        board[bBishop1.getX() - i][bBishop1.getY() - i].setBackground(new Color(8, 8, 129));
                    } else if (colorView[bBishop1.getX() - i][bBishop1.getY() - i].getColor() == 1) {
                        board[bBishop1.getX() - i][bBishop1.getY() - i].setBackground(new Color(8, 8, 129));
                        break;
                    } else break;
                } else break;
            }
            for(i = 1; i < 9; i++){
                if (bBishop1.getX()-i > -1 && bBishop1.getY()+i < 8){
                    if (colorView[bBishop1.getX() - i][bBishop1.getY() + i].getColor() == 0) {
                        board[bBishop1.getX() - i][bBishop1.getY() + i].setBackground(new Color(8, 8, 129));
                    }
                    else if (colorView[bBishop1.getX() - i][bBishop1.getY() + i].getColor() == 1) {
                        board[bBishop1.getX() - i][bBishop1.getY() + i].setBackground(new Color(8, 8, 129));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (bBishop1.getX()+i < 8 && bBishop1.getY()-i > -1){
                    if (colorView[bBishop1.getX() + i][bBishop1.getY() - i].getColor() == 0) {
                        board[bBishop1.getX() + i][bBishop1.getY() - i].setBackground(new Color(8, 8, 129));
                    }
                    else if (colorView[bBishop1.getX() + i][bBishop1.getY() - i].getColor() == 1) {
                        board[bBishop1.getX() + i][bBishop1.getY() - i].setBackground(new Color(8, 8, 129));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (bBishop1.getX()+i < 8 && bBishop1.getY()+i < 8){
                    if (colorView[bBishop1.getX() + i][bBishop1.getY() + i].getColor() == 0) {
                        board[bBishop1.getX() + i][bBishop1.getY() + i].setBackground(new Color(8, 8, 129));
                    }
                    else if (colorView[bBishop1.getX() + i][bBishop1.getY() + i].getColor() == 1) {
                        board[bBishop1.getX() + i][bBishop1.getY() + i].setBackground(new Color(8, 8, 129));
                        break;
                    }
                    else break;
                }
                else break;
            }
        }

        //it sets up the highlighting grids for the bishops movement, it highlights any grids in a diagonal pattern
        //going up right and left, and down left and right, it stops before any pieces that are the same color,
        //on any pieces that are the opposite color, and/or on any empty grid spots until it hits the border
        if(e.getSource() == bBishop2.getPiece()){
            new Instructions(2);
            resetBoardColor();
            int i;
            for (i = 1; i < 9; i++) {
                if (bBishop2.getX() - i > -1 && bBishop2.getY() - i > -1) {
                    if (colorView[bBishop2.getX() - i][bBishop2.getY() - i].getColor() == 0) {
                        board[bBishop2.getX() - i][bBishop2.getY() - i].setBackground(new Color(0, 118, 253));
                    } else if (colorView[bBishop2.getX() - i][bBishop2.getY() - i].getColor() == 1) {
                        board[bBishop2.getX() - i][bBishop2.getY() - i].setBackground(new Color(0, 118, 253));
                        break;
                    } else break;
                } else break;
            }
            for(i = 1; i < 9; i++){
                if (bBishop2.getX()-i > -1 && bBishop2.getY()+i < 8){
                    if (colorView[bBishop2.getX() - i][bBishop2.getY() + i].getColor() == 0) {
                        board[bBishop2.getX() - i][bBishop2.getY() + i].setBackground(new Color(0, 118, 253));
                    }
                    else if (colorView[bBishop2.getX() - i][bBishop2.getY() + i].getColor() == 1) {
                        board[bBishop2.getX() - i][bBishop2.getY() + i].setBackground(new Color(0, 118, 253));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (bBishop2.getX()+i < 8 && bBishop2.getY()-i > -1){
                    if (colorView[bBishop2.getX() + i][bBishop2.getY() - i].getColor() == 0) {
                        board[bBishop2.getX() + i][bBishop2.getY() - i].setBackground(new Color(0, 118, 253));
                    }
                    else if (colorView[bBishop2.getX() + i][bBishop2.getY() - i].getColor() == 1) {
                        board[bBishop2.getX() + i][bBishop2.getY() - i].setBackground(new Color(0, 118, 253));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if (bBishop2.getX()+i < 8 && bBishop2.getY()+i < 8){
                    if (colorView[bBishop2.getX() + i][bBishop2.getY() + i].getColor() == 0) {
                        board[bBishop2.getX() + i][bBishop2.getY() + i].setBackground(new Color(0, 118, 253));
                    }
                    else if (colorView[bBishop2.getX() + i][bBishop2.getY() + i].getColor() == 1) {
                        board[bBishop2.getX() + i][bBishop2.getY() + i].setBackground(new Color(0, 118, 253));
                        break;
                    }
                    else break;
                }
                else break;
            }
        }

        //it sets up the highlighting grids for the rooks movement, it moves up, down, left, and right stopping before
        //pieces of the same color, on pieces of the opposite color, and/or open space until it hits the border
        if(e.getSource() == wRook1.getPiece()){
            new Instructions(4);
            resetBoardColor();

            int i;
            for(i = 1; i < 9; i++){
                if(wRook1.getX()+i < 8){
                    if (colorView[wRook1.getX() + i][wRook1.getY()].getColor() == 0) {
                        board[wRook1.getX() + i][wRook1.getY()].setBackground(new Color(185, 106, 5));
                    }
                    else if (colorView[wRook1.getX() + i][wRook1.getY()].getColor() == 2) {
                        board[wRook1.getX() + i][wRook1.getY()].setBackground(new Color(185, 106, 5));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(wRook1.getX()-i > -1){
                    if (colorView[wRook1.getX() - i][wRook1.getY()].getColor() == 0) {
                        board[wRook1.getX() - i][wRook1.getY()].setBackground(new Color(185, 106, 5));
                    }
                    else if (colorView[wRook1.getX() - i][wRook1.getY()].getColor() == 2) {
                        board[wRook1.getX() - i][wRook1.getY()].setBackground(new Color(185, 106, 5));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++) {
                if (wRook1.getY() + i < 8) {
                    if (colorView[wRook1.getX()][wRook1.getY() + i].getColor() == 0) {
                        board[wRook1.getX()][wRook1.getY() + i].setBackground(new Color(185, 106, 5));
                    }
                    else if (colorView[wRook1.getX()][wRook1.getY() + i].getColor() == 2) {
                        board[wRook1.getX()][wRook1.getY() + i].setBackground(new Color(185, 106, 5));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(wRook1.getY()-i > -1) {
                    if (colorView[wRook1.getX()][wRook1.getY() - i].getColor() == 0) {
                        board[wRook1.getX()][wRook1.getY() - i].setBackground(new Color(185, 106, 5));
                    }
                    else if (colorView[wRook1.getX()][wRook1.getY() - i].getColor() == 2) {
                        board[wRook1.getX()][wRook1.getY() - i].setBackground(new Color(185, 106, 5));
                        break;
                    }
                    else break;
                }
                else break;
            }
        }

        //it sets up the highlighting grids for the rooks movement, it moves up, down, left, and right stopping before
        //pieces of the same color, on pieces of the opposite color, and/or open space until it hits the border
        if(e.getSource() == wRook2.getPiece()){
            new Instructions(4);
            resetBoardColor();

            int i;
            for(i = 1; i < 9; i++){
                if(wRook2.getX()+i < 8){
                    if (colorView[wRook2.getX() + i][wRook2.getY()].getColor() == 0) {
                        board[wRook2.getX() + i][wRook2.getY()].setBackground(new Color(220, 180, 80));
                    }
                    else if (colorView[wRook2.getX() + i][wRook2.getY()].getColor() == 2) {
                        board[wRook2.getX() + i][wRook2.getY()].setBackground(new Color(220, 180, 80));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(wRook2.getX()-i > -1){
                    if (colorView[wRook2.getX() - i][wRook2.getY()].getColor() == 0) {
                        board[wRook2.getX() - i][wRook2.getY()].setBackground(new Color(220, 180, 80));
                    }
                    else if (colorView[wRook2.getX() - i][wRook2.getY()].getColor() == 2) {
                        board[wRook2.getX() - i][wRook2.getY()].setBackground(new Color(220, 180, 80));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++) {
                if (wRook2.getY() + i < 8) {
                    if (colorView[wRook2.getX()][wRook2.getY() + i].getColor() == 0) {
                        board[wRook2.getX()][wRook2.getY() + i].setBackground(new Color(220, 180, 80));
                    }
                    else if (colorView[wRook2.getX()][wRook2.getY() + i].getColor() == 2) {
                        board[wRook2.getX()][wRook2.getY() + i].setBackground(new Color(220, 180, 80));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(wRook2.getY()-i > -1) {
                    if (colorView[wRook2.getX()][wRook2.getY() - i].getColor() == 0) {
                        board[wRook2.getX()][wRook2.getY() - i].setBackground(new Color(220, 180, 80));
                    }
                    else if (colorView[wRook2.getX()][wRook2.getY() - i].getColor() == 2) {
                        board[wRook2.getX()][wRook2.getY() - i].setBackground(new Color(220, 180, 80));
                        break;
                    }
                    else break;
                }
                else break;
            }
        }

        //it sets up the highlighting grids for the rooks movement, it moves up, down, left, and right stopping before
        //pieces of the same color, on pieces of the opposite color, and/or open space until it hits the border
        if(e.getSource() == bRook1.getPiece()){
            new Instructions(4);
            resetBoardColor();

            int i;
            for(i = 1; i < 9; i++){
                if(bRook1.getX()+i < 8){
                    if (colorView[bRook1.getX() + i][bRook1.getY()].getColor() == 0) {
                        board[bRook1.getX() + i][bRook1.getY()].setBackground(new Color(80, 59, 13));
                    }
                    else if (colorView[bRook1.getX() + i][bRook1.getY()].getColor() == 1) {
                        board[bRook1.getX() + i][bRook1.getY()].setBackground(new Color(80, 59, 13));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(bRook1.getX()-i > -1){
                    if (colorView[bRook1.getX() - i][bRook1.getY()].getColor() == 0) {
                        board[bRook1.getX() - i][bRook1.getY()].setBackground(new Color(80, 59, 13));
                    }
                    else if (colorView[bRook1.getX() - i][bRook1.getY()].getColor() == 1) {
                        board[bRook1.getX() - i][bRook1.getY()].setBackground(new Color(80, 59, 13));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++) {
                if (bRook1.getY() + i < 8) {
                    if (colorView[bRook1.getX()][bRook1.getY() + i].getColor() == 0) {
                        board[bRook1.getX()][bRook1.getY() + i].setBackground(new Color(80, 59, 13));
                    }
                    else if (colorView[bRook1.getX()][bRook1.getY() + i].getColor() == 1) {
                        board[bRook1.getX()][bRook1.getY() + i].setBackground(new Color(80, 59, 13));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(bRook1.getY()-i > -1) {
                    if (colorView[bRook1.getX()][bRook1.getY() - i].getColor() == 0) {
                        board[bRook1.getX()][bRook1.getY() - i].setBackground(new Color(80, 59, 13));
                    }
                    else if (colorView[bRook1.getX()][bRook1.getY() - i].getColor() == 1) {
                        board[bRook1.getX()][bRook1.getY() - i].setBackground(new Color(80, 59, 13));
                        break;
                    }
                    else break;
                }
                else break;
            }
        }

        //it sets up the highlighting grids for the rooks movement, it moves up, down, left, and right stopping before
        //pieces of the same color, on pieces of the opposite color, and/or open space until it hits the border
        if(e.getSource() == bRook2.getPiece()){
            new Instructions(4);
            resetBoardColor();

            int i;
            for(i = 1; i < 9; i++){
                if(bRook2.getX()+i < 8){
                    if (colorView[bRook2.getX() + i][bRook2.getY()].getColor() == 0) {
                        board[bRook2.getX() + i][bRook2.getY()].setBackground(new Color(169, 168, 25));
                    }
                    else if (colorView[bRook2.getX() + i][bRook2.getY()].getColor() == 1) {
                        board[bRook2.getX() + i][bRook2.getY()].setBackground(new Color(169, 168, 25));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(bRook2.getX()-i > -1){
                    if (colorView[bRook2.getX() - i][bRook2.getY()].getColor() == 0) {
                        board[bRook2.getX() - i][bRook2.getY()].setBackground(new Color(169, 168, 25));
                    }
                    else if (colorView[bRook2.getX() - i][bRook2.getY()].getColor() == 1) {
                        board[bRook2.getX() - i][bRook2.getY()].setBackground(new Color(169, 168, 25));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++) {
                if (bRook2.getY() + i < 8) {
                    if (colorView[bRook2.getX()][bRook2.getY() + i].getColor() == 0) {
                        board[bRook2.getX()][bRook2.getY() + i].setBackground(new Color(169, 168, 25));
                    }
                    else if (colorView[bRook2.getX()][bRook2.getY() + i].getColor() == 1) {
                        board[bRook2.getX()][bRook2.getY() + i].setBackground(new Color(169, 168, 25));
                        break;
                    }
                    else break;
                }
                else break;
            }
            for(i = 1; i < 9; i++){
                if(bRook2.getY()-i > -1) {
                    if (colorView[bRook2.getX()][bRook2.getY() - i].getColor() == 0) {
                        board[bRook2.getX()][bRook2.getY() - i].setBackground(new Color(169, 168, 25));
                    }
                    else if (colorView[bRook2.getX()][bRook2.getY() - i].getColor() == 1) {
                        board[bRook2.getX()][bRook2.getY() - i].setBackground(new Color(169, 168, 25));
                        break;
                    }
                    else break;
                }
                else break;
            }
        }

        //it sets up the highlighting grids for the knights movement, the knights movement goes left, right, up and down
        //by 2, then it goes perpendicular to that movement by 1 each direction and highlights that square if it is an
        //open space or has a piece of an opposite color
        if(e.getSource() == wKnight1.getPiece()){
            new Instructions(3);
            resetBoardColor();

            //checks if each move is on board and highlights
            if(wKnight1.getX()+2 < 8 && wKnight1.getY()+1 < 8){
                if (colorView[wKnight1.getX()+2][wKnight1.getY()+1].getColor() == 2 ||
                        colorView[wKnight1.getX()+2][wKnight1.getY()+1].getColor() == 0 ) {


                    board[wKnight1.getX() + 2][wKnight1.getY() + 1].setBackground(new Color(98, 8, 143));
                }
            }
            if(wKnight1.getX()+2 < 8 && wKnight1.getY()-1 > -1){
                if (colorView[wKnight1.getX() + 2][wKnight1.getY() - 1].getColor() == 2 ||
                        colorView[wKnight1.getX() + 2][wKnight1.getY() - 1].getColor() == 0 ) {
                    board[wKnight1.getX() + 2][wKnight1.getY() - 1].setBackground(new Color(98, 8, 143));
                }
            }
            if(wKnight1.getX()-2 > -1 && wKnight1.getY()+1 < 8){
                if (colorView[wKnight1.getX() - 2][wKnight1.getY() + 1].getColor() == 2 ||
                        colorView[wKnight1.getX() - 2][wKnight1.getY() + 1].getColor() == 0 ) {
                    board[wKnight1.getX() - 2][wKnight1.getY() + 1].setBackground(new Color(898, 8, 143));
                }
            }
            if(wKnight1.getX()-2 > -1 && wKnight1.getY()-1 > -1){
                if (colorView[wKnight1.getX() - 2][wKnight1.getY() - 1].getColor() == 2 ||
                        colorView[wKnight1.getX() - 2][wKnight1.getY() - 1].getColor() == 0 ) {
                    board[wKnight1.getX() - 2][wKnight1.getY() - 1].setBackground(new Color(98, 8, 143));
                }
            }
            if(wKnight1.getX()+1 < 8 && wKnight1.getY()+2 < 8){
                if (colorView[wKnight1.getX() + 1][wKnight1.getY() + 2].getColor() == 2 ||
                        colorView[wKnight1.getX() + 1][wKnight1.getY() + 2].getColor() == 0 ) {
                    board[wKnight1.getX() + 1][wKnight1.getY() + 2].setBackground(new Color(98, 8, 143));
                }
            }
            if(wKnight1.getX()+1 < 8 && wKnight1.getY()-2 > -1){
                if (colorView[wKnight1.getX() + 1][wKnight1.getY() - 2].getColor() == 2 ||
                        colorView[wKnight1.getX() + 1][wKnight1.getY() - 2].getColor() == 0 ) {
                    board[wKnight1.getX() + 1][wKnight1.getY() - 2].setBackground(new Color(98, 8, 143));
                }
            }
            if(wKnight1.getX()-1 > -1 && wKnight1.getY()+2 < 8){
                if (colorView[wKnight1.getX() - 1][wKnight1.getY() + 2].getColor() == 2 ||
                        colorView[wKnight1.getX() - 1][wKnight1.getY() + 2].getColor() == 0 ) {
                    board[wKnight1.getX() - 1][wKnight1.getY() + 2].setBackground(new Color(98, 8, 143));
                }
            }
            if(wKnight1.getX()-1 > -1 && wKnight1.getY()-2 > -1){
                if (colorView[wKnight1.getX() - 1][wKnight1.getY() - 2].getColor() == 2 ||
                        colorView[wKnight1.getX() - 1][wKnight1.getY() - 2].getColor() == 0 ) {
                    board[wKnight1.getX() - 1][wKnight1.getY() - 2].setBackground(new Color(98, 8, 143));
                }
            }
        }

        //it sets up the highlighting grids for the knights movement, the knights movement goes left, right, up and down
        //by 2, then it goes perpendicular to that movement by 1 each direction and highlights that square if it is an
        //open space or has a piece of an opposite color
        if(e.getSource() == wKnight2.getPiece()){
            new Instructions(3);
            resetBoardColor();

            //checks if each move is on board and highlights
            if(wKnight2.getX()+2 < 8 && wKnight2.getY()+1 < 8){
                if (colorView[wKnight2.getX()+2][wKnight2.getY()+1].getColor() == 2 ||
                        colorView[wKnight2.getX()+2][wKnight2.getY()+1].getColor() == 0 ) {


                    board[wKnight2.getX() + 2][wKnight2.getY() + 1].setBackground(new Color(92, 6, 234));
                }
            }
            if(wKnight2.getX()+2 < 8 && wKnight2.getY()-1 > -1){
                if (colorView[wKnight2.getX() + 2][wKnight2.getY() - 1].getColor() == 2 ||
                        colorView[wKnight2.getX() + 2][wKnight2.getY() - 1].getColor() == 0 ) {
                    board[wKnight2.getX() + 2][wKnight2.getY() - 1].setBackground(new Color(92, 6, 234));
                }
            }
            if(wKnight2.getX()-2 > -1 && wKnight2.getY()+1 < 8){
                if (colorView[wKnight2.getX() - 2][wKnight2.getY() + 1].getColor() == 2 ||
                        colorView[wKnight2.getX() - 2][wKnight2.getY() + 1].getColor() == 0 ) {
                    board[wKnight2.getX() - 2][wKnight2.getY() + 1].setBackground(new Color(92, 6, 234));
                }
            }
            if(wKnight2.getX()-2 > -1 && wKnight2.getY()-1 > -1){
                if (colorView[wKnight2.getX() - 2][wKnight2.getY() - 1].getColor() == 2 ||
                        colorView[wKnight2.getX() - 2][wKnight2.getY() - 1].getColor() == 0 ) {
                    board[wKnight2.getX() - 2][wKnight2.getY() - 1].setBackground(new Color(92, 6, 234));
                }
            }
            if(wKnight2.getX()+1 < 8 && wKnight2.getY()+2 < 8){
                if (colorView[wKnight2.getX() + 1][wKnight2.getY() + 2].getColor() == 2 ||
                        colorView[wKnight2.getX() + 1][wKnight2.getY() + 2].getColor() == 0 ) {
                    board[wKnight2.getX() + 1][wKnight2.getY() + 2].setBackground(new Color(92, 6, 234));
                }
            }
            if(wKnight2.getX()+1 < 8 && wKnight2.getY()-2 > -1){
                if (colorView[wKnight2.getX() + 1][wKnight2.getY() - 2].getColor() == 2 ||
                        colorView[wKnight2.getX() + 1][wKnight2.getY() - 2].getColor() == 0 ) {
                    board[wKnight2.getX() + 1][wKnight2.getY() - 2].setBackground(new Color(92, 6, 234));
                }
            }
            if(wKnight2.getX()-1 > -1 && wKnight2.getY()+2 < 8){
                if (colorView[wKnight2.getX() - 1][wKnight2.getY() + 2].getColor() == 2 ||
                        colorView[wKnight2.getX() - 1][wKnight2.getY() + 2].getColor() == 0 ) {
                    board[wKnight2.getX() - 1][wKnight2.getY() + 2].setBackground(new Color(92, 6, 234));
                }
            }
            if(wKnight2.getX()-1 > -1 && wKnight2.getY()-2 > -1){
                if (colorView[wKnight2.getX() - 1][wKnight2.getY() - 2].getColor() == 2 ||
                        colorView[wKnight2.getX() - 1][wKnight2.getY() - 2].getColor() == 0 ) {
                    board[wKnight2.getX() - 1][wKnight2.getY() - 2].setBackground(new Color(92, 6, 234));
                }
            }
        }

        //it sets up the highlighting grids for the knights movement, the knights movement goes left, right, up and down
        //by 2, then it goes perpendicular to that movement by 1 each direction and highlights that square if it is an
        //open space or has a piece of an opposite color
        if(e.getSource() == bKnight1.getPiece()){
            new Instructions(3);
            resetBoardColor();

            //checks if each move is on board and highlights
            if(bKnight1.getX()+2 < 8 && bKnight1.getY()+1 < 8){
                if (colorView[bKnight1.getX()+2][bKnight1.getY()+1].getColor() == 1 ||
                        colorView[bKnight1.getX()+2][bKnight1.getY()+1].getColor() == 0 ) {


                    board[bKnight1.getX() + 2][bKnight1.getY() + 1].setBackground(new Color(113, 89, 217));
                }
            }
            if(bKnight1.getX()+2 < 8 && bKnight1.getY()-1 > -1){
                if (colorView[bKnight1.getX() + 2][bKnight1.getY() - 1].getColor() == 1 ||
                        colorView[bKnight1.getX() + 2][bKnight1.getY() - 1].getColor() == 0 ) {
                    board[bKnight1.getX() + 2][bKnight1.getY() - 1].setBackground(new Color(113, 89, 217));
                }
            }
            if(bKnight1.getX()-2 > -1 && bKnight1.getY()+1 < 8){
                if (colorView[bKnight1.getX() - 2][bKnight1.getY() + 1].getColor() == 1 ||
                        colorView[bKnight1.getX() - 2][bKnight1.getY() + 1].getColor() == 0 ) {
                    board[bKnight1.getX() - 2][bKnight1.getY() + 1].setBackground(new Color(113, 89, 217));
                }
            }
            if(bKnight1.getX()-2 > -1 && bKnight1.getY()-1 > -1){
                if (colorView[bKnight1.getX() - 2][bKnight1.getY() - 1].getColor() == 1 ||
                        colorView[bKnight1.getX() - 2][bKnight1.getY() - 1].getColor() == 0 ) {
                    board[bKnight1.getX() - 2][bKnight1.getY() - 1].setBackground(new Color(113, 89, 217));
                }
            }
            if(bKnight1.getX()+1 < 8 && bKnight1.getY()+2 < 8){
                if (colorView[bKnight1.getX() + 1][bKnight1.getY() + 2].getColor() == 1 ||
                        colorView[bKnight1.getX() + 1][bKnight1.getY() + 2].getColor() == 0 ) {
                    board[bKnight1.getX() + 1][bKnight1.getY() + 2].setBackground(new Color(113, 89, 217));
                }
            }
            if(bKnight1.getX()+1 < 8 && bKnight1.getY()-2 > -1){
                if (colorView[bKnight1.getX() + 1][bKnight1.getY() - 2].getColor() == 1 ||
                        colorView[bKnight1.getX() + 1][bKnight1.getY() - 2].getColor() == 0 ) {
                    board[bKnight1.getX() + 1][bKnight1.getY() - 2].setBackground(new Color(113, 89, 217));
                }
            }
            if(bKnight1.getX()-1 > -1 && bKnight1.getY()+2 < 8){
                if (colorView[bKnight1.getX() - 1][bKnight1.getY() + 2].getColor() == 1 ||
                        colorView[bKnight1.getX() - 1][bKnight1.getY() + 2].getColor() == 0 ) {
                    board[bKnight1.getX() - 1][bKnight1.getY() + 2].setBackground(new Color(113, 89, 217));
                }
            }
            if(bKnight1.getX()-1 > -1 && bKnight1.getY()-2 > -1){
                if (colorView[bKnight1.getX() - 1][bKnight1.getY() - 2].getColor() == 1 ||
                        colorView[bKnight1.getX() - 1][bKnight1.getY() - 2].getColor() == 0 ) {
                    board[bKnight1.getX() - 1][bKnight1.getY() - 2].setBackground(new Color(113, 89, 217));
                }
            }
        }

        //it sets up the highlighting grids for the knights movement, the knights movement goes left, right, up and down
        //by 2, then it goes perpendicular to that movement by 1 each direction and highlights that square if it is an
        //open space or has a piece of an opposite color
        if(e.getSource() == bKnight2.getPiece()) {
            new Instructions(3);
            resetBoardColor();

            //checks if each move is on board and highlights
            if (bKnight2.getX() + 2 < 8 && bKnight2.getY() + 1 < 8) {
                if (colorView[bKnight2.getX() + 2][bKnight2.getY() + 1].getColor() == 1 ||
                        colorView[bKnight2.getX() + 2][bKnight2.getY() + 1].getColor() == 0) {


                    board[bKnight2.getX() + 2][bKnight2.getY() + 1].setBackground(new Color(171, 130, 218));
                }
            }
            if (bKnight2.getX() + 2 < 8 && bKnight2.getY() - 1 > -1) {
                if (colorView[bKnight2.getX() + 2][bKnight2.getY() - 1].getColor() == 1 ||
                        colorView[bKnight2.getX() + 2][bKnight2.getY() - 1].getColor() == 0) {
                    board[bKnight2.getX() + 2][bKnight2.getY() - 1].setBackground(new Color(171, 130, 218));
                }
            }
            if (bKnight2.getX() - 2 > -1 && bKnight2.getY() + 1 < 8) {
                if (colorView[bKnight2.getX() - 2][bKnight2.getY() + 1].getColor() == 1 ||
                        colorView[bKnight2.getX() - 2][bKnight2.getY() + 1].getColor() == 0) {
                    board[bKnight2.getX() - 2][bKnight2.getY() + 1].setBackground(new Color(171, 130, 218));
                }
            }
            if (bKnight2.getX() - 2 > -1 && bKnight2.getY() - 1 > -1) {
                if (colorView[bKnight2.getX() - 2][bKnight2.getY() - 1].getColor() == 1 ||
                        colorView[bKnight2.getX() - 2][bKnight2.getY() - 1].getColor() == 0) {
                    board[bKnight2.getX() - 2][bKnight2.getY() - 1].setBackground(new Color(171, 130, 218));
                }
            }
            if (bKnight2.getX() + 1 < 8 && bKnight2.getY() + 2 < 8) {
                if (colorView[bKnight2.getX() + 1][bKnight2.getY() + 2].getColor() == 1 ||
                        colorView[bKnight2.getX() + 1][bKnight2.getY() + 2].getColor() == 0) {
                    board[bKnight2.getX() + 1][bKnight2.getY() + 2].setBackground(new Color(171, 130, 218));
                }
            }
            if (bKnight2.getX() + 1 < 8 && bKnight2.getY() - 2 > -1) {
                if (colorView[bKnight2.getX() + 1][bKnight2.getY() - 2].getColor() == 1 ||
                        colorView[bKnight2.getX() + 1][bKnight2.getY() - 2].getColor() == 0) {
                    board[bKnight2.getX() + 1][bKnight2.getY() - 2].setBackground(new Color(171, 130, 218));
                }
            }
            if (bKnight2.getX() - 1 > -1 && bKnight2.getY() + 2 < 8) {
                if (colorView[bKnight2.getX() - 1][bKnight2.getY() + 2].getColor() == 1 ||
                        colorView[bKnight2.getX() - 1][bKnight2.getY() + 2].getColor() == 0) {
                    board[bKnight2.getX() - 1][bKnight2.getY() + 2].setBackground(new Color(171, 130, 218));
                }
            }
            if (bKnight2.getX() - 1 > -1 && bKnight2.getY() - 2 > -1) {
                if (colorView[bKnight2.getX() - 1][bKnight2.getY() - 2].getColor() == 1 ||
                        colorView[bKnight2.getX() - 1][bKnight2.getY() - 2].getColor() == 0) {
                    board[bKnight2.getX() - 1][bKnight2.getY() - 2].setBackground(new Color(171, 130, 218));
                }
            }
        }
    }
}
/* Jarett Nelson
 * Assingment 6, Spring 2024
 * CS245
 * The purpose of Piece.java is to create the piece constructor, it actually creates the pieces and the layouts for them
 * it also holds the actual points that different grid spots mean.
 */

import javax.swing.*;
import java.awt.*;

public class Piece extends JFrame {
    JButton piece;
    boolean color;

    //this sets up the layout for each piece as well as holds the location
    Piece(boolean color){
        piece = new JButton();
        this.color = color;
        if(color){
            piece.setForeground(new Color(255,255,240));
        }else{
            piece.setForeground(Color.BLACK);
        }
        piece.setFont(new Font("Times New Roman", Font.PLAIN, 40));
        piece.setFocusable(false);
        piece.setOpaque(false);
        piece.setContentAreaFilled(false);
        piece.setBorderPainted(false);
    }

    boolean getColor(){
        return color;
    }

    //holds all the x and y values for each grid position in one switch
    JButton getPiece(){
        return piece;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
    int x;
    int y;
    //sets x and y values
    void setXY(int e){
        switch (e) {
            case 0:
                x = 0;
                y = 0;
                break;
            case 1:
                x = 0;
                y = 1;
                break;
            case 2:
                x = 0;
                y = 2;
                break;
            case 3:
                x = 0;
                y = 3;
                break;
            case 4:
                x = 0;
                y = 4;
                break;
            case 5:
                x = 0;
                y = 5;
                break;
            case 6:
                x = 0;
                y = 6;
                break;
            case 7:
                x = 0;
                y = 7;
                break;
            case 8:
                x = 1;
                y = 0;
                break;
            case 9:
                x = 1;
                y = 1;
                break;
            case 10:
                x = 1;
                y = 2;
                break;
            case 11:
                x = 1;
                y = 3;
                break;
            case 12:
                x = 1;
                y = 4;
                break;
            case 13:
                x = 1;
                y = 5;
                break;
            case 14:
                x = 1;
                y = 6;
                break;
            case 15:
                x = 1;
                y = 7;
                break;
            case 16:
                x = 2;
                y = 0;
                break;
            case 17:
                x = 2;
                y = 1;
                break;
            case 18:
                x = 2;
                y = 2;
                break;
            case 19:
                x = 2;
                y = 3;
                break;
            case 20:
                x = 2;
                y = 4;
                break;
            case 21:
                x = 2;
                y = 5;
                break;
            case 22:
                x = 2;
                y = 6;
                break;
            case 23:
                x = 2;
                y = 7;
                break;
            case 24:
                x = 3;
                y = 0;
                break;
            case 25:
                x = 3;
                y = 1;
                break;
            case 26:
                x = 3;
                y = 2;
                break;
            case 27:
                x = 3;
                y = 3;
                break;
            case 28:
                x = 3;
                y = 4;
                break;
            case 29:
                x = 3;
                y = 5;
                break;
            case 30:
                x = 3;
                y = 6;
                break;
            case 31:
                x = 3;
                y = 7;
                break;
            case 32:
                x = 4;
                y = 0;
                break;
            case 33:
                x = 4;
                y = 1;
                break;
            case 34:
                x = 4;
                y = 2;
                break;
            case 35:
                x = 4;
                y = 3;
                break;
            case 36:
                x = 4;
                y = 4;
                break;
            case 37:
                x = 4;
                y = 5;
                break;
            case 38:
                x = 4;
                y = 6;
                break;
            case 39:
                x = 4;
                y = 7;
                break;
            case 40:
                x = 5;
                y = 0;
                break;
            case 41:
                x = 5;
                y = 1;
                break;
            case 42:
                x = 5;
                y = 2;
                break;
            case 43:
                x = 5;
                y = 3;
                break;
            case 44:
                x = 5;
                y = 4;
                break;
            case 45:
                x = 5;
                y = 5;
                break;
            case 46:
                x = 5;
                y = 6;
                break;
            case 47:
                x = 5;
                y = 7;
                break;
            case 48:
                x = 6;
                y = 0;
                break;
            case 49:
                x = 6;
                y = 1;
                break;
            case 50:
                x = 6;
                y = 2;
                break;
            case 51:
                x = 6;
                y = 3;
                break;
            case 52:
                x = 6;
                y = 4;
                break;
            case 53:
                x = 6;
                y = 5;
                break;
            case 54:
                x = 6;
                y = 6;
                break;
            case 55:
                x = 6;
                y = 7;
                break;
            case 56:
                x = 7;
                y = 0;
                break;
            case 57:
                x = 7;
                y = 1;
                break;
            case 58:
                x = 7;
                y = 2;
                break;
            case 59:
                x = 7;
                y = 3;
                break;
            case 60:
                x = 7;
                y = 4;
                break;
            case 61:
                x = 7;
                y = 5;
                break;
            case 62:
                x = 7;
                y = 6;
                break;
            case 63:
                x = 7;
                y = 7;
        }
    }
}

/**
 * Triangle.java -- Implements a simple triangle with hard-coded shape.
 *                  Supports color, location, and size specification, 
 */
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.BufferUtils;

import java.nio.*;
import java.util.ArrayList;
import java.io.*;

class Rectangle extends Shape
{

    protected int     shaderPgm;    // shader id for this triangle

    public Rectangle( ArrayList<Shape> shapes, float xLoc, float yLoc, float width, float height, int shaderProgram )
    {
        shaderPgm = shaderProgram;

        
        float dx1[] = { -width/2, width/2, width/2 };
        float dy1[] = { 0.0f, 0.0f, height };
        
        float dx2[] = { -width/2, width/2, -width/2 };
        float dy2[] = { 0.0f, 0.0f, -height };
        
        MyTriangle tri1 = new MyTriangle( dx1, dy1, shaderPgm );
        tri1.setLocation( xLoc, yLoc );
        tri1.setColor( 1, 1, 0 );
        shapes.add(tri1);
        
        MyTriangle tri2 = new MyTriangle( dx2, dy2, shaderPgm );
        tri2.setLocation( xLoc, yLoc + height);
        tri2.setColor( 1, 1, 0);
        shapes.add(tri2);
        

    }
      
    	@Override
    	void redraw() {
    		// TODO Auto-generated method stub
    		
    	}

}



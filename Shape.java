/* 
 * Shape Class
 * Authors: Dan Bergeron & Stephen Dunn
 * Date: 8/8/2014
 */
import java.nio.*;
import org.lwjgl.BufferUtils;

abstract public class Shape
{   
    protected float xLoc, yLoc;
    protected float xSize = 1.0f, ySize = 1.0f;
    protected float red, green, blue;
    protected FloatBuffer colorBuf;
    
    public Shape()
    {
    }
    
    void setLocation(float x, float y)
    {
        xLoc = x; yLoc = y;
    }
    
    float getX() 
    { 
        return xLoc; 
    }
    float getY() 
    { 
        return yLoc; 
    }
    
    void setColor(float r, float g, float b)
    {
        red = r; green = g; blue = b;
        //colorBuf = FloatBuffer.allocate( 3 );
        colorBuf = BufferUtils.createFloatBuffer( 4 );
        colorBuf.put( r ).put( g ).put( b ).put( 1 ).flip();
    }
    
    void setSize(float xs, float ys)
    {
        xSize = xs; ySize = ys;
    }
    
    abstract void redraw();

}



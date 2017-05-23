/**
 * Triangle.java -- Implements a simple triangle with hard-coded shape.
 *                  Supports color, location, and size specification, 
 *
 * @author rdb
 * 09/06/2015 - derived loosely from earlier JOGL demos for OpenGL 2.
 */
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.BufferUtils;

import java.nio.*;
import java.io.*;

class Triangle extends Shape
{
    //------------------- instance variables ------------------------
    protected int     nVerts;
    protected float[] coords; 

    //---------- GLSL related class/instance variables
    protected int     attrLoc_vpos;   // VAO id for vPosition   

    static protected int unif_model = -1;  // uniform id for model matrix
    static protected int unif_vColor = -1; // uniform var for vertex color

    protected int     bufferId;       // VBO id for vertices
    protected int     vaoId; // not sure this needs to be instance var
    protected int     shaderPgm;    // shader id for this triangle

    //---------- Triangle related instance variables
    // Inherited from shape:
    // xLoc, yLoc, xSize, ySize
    
    //-------------------- constructor -----------------------------
    /**
     * The instance needs to know the shaderProgram to be used, in order
     *    to implement color, location and size changes.
     * It might have been better to have the shaderProgram be provided
     *    by a class variable in a GLSL support class.
     *
     * @param shaderProgram the shader program to be used.
     *
     */
    public Triangle( int shaderProgram )
    {
        shaderPgm = shaderProgram;

        float dx[] = { -0.25f, 0.25f, 0.0f };
        float dy[] = { 0.0f, 0.0f, 0.5f };

        setLocation( 0, 0 );
        setColor( 1, 1, 0 );   // Yellow is default color

        nVerts = 3;
        coords = new float[ nVerts * 2 ];
        
        int c = 0;
        for ( int i = 0; i < nVerts; i++ )
        {
            coords[ c++ ] = dx[ i ];
            coords[ c++ ] = dy[ i ];
        }
        makeBuffers();
    
        if ( unif_vColor == -1 )
        {
            unif_vColor = glGetUniformLocation( shaderPgm, "vColor" );
            unif_model  = glGetUniformLocation( shaderPgm, "model" );
        }
        System.out.println( "vColor, model " + unif_vColor + " " + unif_model );
    }
    
    //------------------------------ makeBuffers -----------------------
    /**
    *  Create VertexArrayObject and VertexBufferObject.
    */
    void makeBuffers()
    {
        // ---- set up to transfer points to gpu
        // 1. Create a vertex array object
        this.vaoId = glGenVertexArrays();
        glBindVertexArray( vaoId );  // binding => this VAO is "current" one
        
        // 2. Create a vertex buffer
        this.bufferId = glGenBuffers();
        // make it the current buffer
        glBindBuffer( GL_ARRAY_BUFFER, this.bufferId ); 
    
        FloatBuffer fbuf = BufferUtils.createFloatBuffer( coords.length );
        fbuf.put( coords ).flip();
    
        glBufferData( GL_ARRAY_BUFFER, fbuf, GL_STATIC_DRAW );
    
        // define a variable, "vPosition"
        // Note: could use predefined locations glBindAttrLocation
        attrLoc_vpos = glGetAttribLocation( shaderPgm, "vPosition" );
        glEnableVertexAttribArray( attrLoc_vpos );
    
        glVertexAttribPointer( attrLoc_vpos, 2, GL_FLOAT, false, 0, 0L );
        
        // debug: test if correct data is in the buffer
        //   Could add code here to define a float array and read
        //   the VertexAttribArray just uploaded and compare it to the
        //   coords array -- or just print it.
    
        glBindVertexArray( 0 );               // unbind the VAO
        glBindBuffer( GL_ARRAY_BUFFER, 0 );   // unbind the VBO
    }
    //------------------------- redraw ----------------------------
    /**
     * Update the specifications for the shape.
     */
    void redraw()
    {
        // Simple modeling: only size and location. 
        //   we can write down the desired matrix.
        // This specification and the FloatBuffer we create does NOT have
        //   to be done on every re-draw; it only needs to be done when
        //   the location or size changes: setLocation or setSize.
        //   These methods are defined in Shape, but could be overridden
        //   here and modelBuf could be an instance variable.
        //
        float[] model = { xSize,   0,   0, 0,  
                           0,    ySize, 0, 0,  
                           0,      0,   1, 0,  
                          xLoc,  yLoc,  0, 1 };

        // BufferUtils is an lwjgl utility class
        FloatBuffer modelBuf = BufferUtils.createFloatBuffer( model.length );
        modelBuf.put( model ).flip();
 
        glUniformMatrix4fv( unif_model, false, modelBuf );
        glUniform4fv( unif_vColor, colorBuf );
 
        glBindVertexArray( vaoId );
        glDrawArrays( GL_TRIANGLES, 0, 3 );
        glBindVertexArray( 0 );
        
        
    }
}

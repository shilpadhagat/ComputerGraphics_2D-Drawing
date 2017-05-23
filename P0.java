/**
 * BasicLWJGL.java - demonstrates some of the most fundamental concepts
 *                   of programming in LWJGL3 within the context of an
 *                   object oriented data management methodology.
 *       This simple demo is not considered with efficient use of GPU 
 *       computational power, nor in minimizing data transfer between the
 *       CPU and GPU. That will come later.
 *
 * @author rdb
 * 09/06/15 version 1.0
 *          This version is not a great example of good style or organization 
 *          or cleanliness! But it works. 
 * 09/08/15 version 1.2
 *          Improved testing for OS to determine what OpenGL version can be
 *             used and to edit GLSL version specification when running on
 *             Linux machines
 *          version 1.2b 
 *             added warning message if input shaders are not version 330
 * 09/09/15 version 1.2c
 *             moved keyboard handler setup to its own method
 *
 * This program makes use code from demos found at lwjgl.org accessed as
 * lwjgl3-demo-master and downloaded in late August 2015. It also uses a
 * slightly modified complete class from that package, DemoUtils.
 */
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;


import java.nio.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class P0
{
    //---------------------- instance variables ----------------------
    // window size parameters
    int windowW = 600;
    int windowH = 640;
    
    // shader program id.
    int shaderPgm; 

    private Scene scene;
    
    /*
    // storage for shapes created
    ArrayList<Shape> shapes = new ArrayList<Shape>();
    */
    
    private SceneMaker sceneMaker = new SceneMaker();
    
    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    
    // The window handle
    private long window;
    
    //--------------- init ------------------------------------------
    public void init()
    {
        openWindow();
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        
        //rdb GL.createCapabilities();
        GL.createCapabilities( false ); // rdb: true => forward compatible

        try 
        {
            makeShaders();
        } 
        catch ( IOException iox )  
        {
            System.err.println( "Shader construction failed." );
            System.exit( -1 );
        }
        setupView();
        scene = sceneMaker.makeScene(shaderPgm);
        
        setupKeyHandler();

        loop();
            
        // Release window and window callbacks
        glfwDestroyWindow(window);
        keyCallback.release();
        errorCallback.release();
    }
    
    //------------------------ openWindow ----------------------------
    /**
     * Do whatever is necessary to open a GLFW window. This code is heavily
     * based on code from lwjgl3-demo-master.
     */
    private void openWindow()
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback( errorCallback = errorCallbackPrint(System.err) );
        
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException( "Unable to initialize GLFW" );
        
        // Configure our window
        glfwDefaultWindowHints(); // superfluous here; already default
        glfwWindowHint( GLFW_VISIBLE, GL_FALSE ); // window hidden after create
        glfwWindowHint( GLFW_RESIZABLE, GL_TRUE ); // window is resizable

        //-------
        // Linux (at least ours) currently only supports OpenGL 3.0 and
        //    GLSL 3.0 which identifies its shaders as 1.3 (130).
        //    Shader version 130 is identical to version 330 for features
        //    shared by both versions which is all of 130 and the vast
        //    majority of 330. Since it's not possible to ask what versions
        //    are supported before opening a window, we'll just hard code
        //    the desired versions by os name.
        //
        String osname = System.getProperty( "os.name" );
        if ( !osname.toLowerCase().startsWith( "linux" ) )
        {
            // Linux (at least ours) can only handle 3.0, but doesn't support 
            //    the specifications that allow us to test that.
            // Mac OS/X (at least with Mavericks and Yosemite) can handle 3.2
            // Windows also should be able to do 3.2.
		    glfwWindowHint( GLFW_CONTEXT_VERSION_MAJOR, 3 );
		    glfwWindowHint( GLFW_CONTEXT_VERSION_MINOR, 2 );
            glfwWindowHint( GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE ); 
            glfwWindowHint( GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE ); 
        }
 
        // Create the window
        window = glfwCreateWindow( windowW, windowH, "BasicLWJGL", NULL, NULL );
        if ( window == NULL )
            throw new RuntimeException( "Failed to create the GLFW window" );
       
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode( glfwGetPrimaryMonitor() );
        int centerX = ( GLFWvidmode.width( vidmode ) - windowW ) / 2;
        int centerY = ( GLFWvidmode.height( vidmode ) - windowH ) / 2;

        // Center our window
        glfwSetWindowPos( window, centerX, centerY );
        
        // Make the OpenGL context current
        glfwMakeContextCurrent( window );
        GLContext.createFromCurrent();  // not in lwjgl demos
        // Enable v-sync
        glfwSwapInterval( 1 );
        glfwShowWindow( window );
    }

    //--------------------- setupKeyHandler ----------------------
    /**
     * void setupKeyHandler
     */
    private void setupKeyHandler()
    {
        // Setup a key callback. It is called every time a key is pressed, 
        //      repeated or released.
        glfwSetKeyCallback( window, 
            keyCallback = new GLFWKeyCallback()
            {
                @Override
                public void invoke( long keyWindow, int key, 
                                    int scancode, int action, int mods )
                {
                    // escape key means terminate; set flag; test in render loop
                    if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                        glfwSetWindowShouldClose( window, GL_TRUE ); 
                }
            });
    }
    
    //-------------------------- loop ----------------------------
    /**
     * Loop until user closes the window or kills the program.
     */
    private void loop() 
    {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( glfwWindowShouldClose( window ) == GL_FALSE )
        {
            // clear the framebuffer
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); 

            // redraw the frame
            redraw();

            glfwSwapBuffers( window ); // swap the color buffers

            // Wait for window events. The key callback above will only be
            // invoked during this call.
            // lwjgl demos use glfwPollEvents(), which uses nearly 2X
            //    the cpu time for simple demos as glfwWaitEvents.
            glfwWaitEvents();
        }
        System.err.println( "Exited loop" );
    }
    
    //------------------ makeScene --------------------------
    /**
     * Create the objects that make up the scene.
     */
   
    
    //--------------------- createShader --------------------------
	/**
	 * Create a shader object from the given classpath resource.
	 *
	 * @param resource
	 *            the class path 
     *        rdb no, it's the file itself 
     *            but perhaps searched in a resource accessible from
     *            class path; I made accessible from cwd.
	 * @param type
	 *            the shader type
	 *
	 * @return the shader object id
	 *
	 * @throws IOException
     * @author lwjgl.org from lwgjl tutorials
     *
	 */
	private static int createShader( String resource, int type )
			throws IOException 
    {
		ByteBuffer source = DemoUtils.ioResourceToByteBuffer( resource, 8192 );

        modifyVersion( source );  // convert version 330 to 130 if needed

		PointerBuffer strings = BufferUtils.createPointerBuffer( 1 );
		IntBuffer lengths = BufferUtils.createIntBuffer( 1 );

		strings.put( 0, source );
		lengths.put( 0, source.remaining());

		int shader = glCreateShader( type );

		glShaderSource( shader, strings, lengths );
		glCompileShader( shader );
		int compiled = glGetShaderi( shader, GL_COMPILE_STATUS );
		String shaderLog = glGetShaderInfoLog( shader );
		if ( shaderLog.trim().length() > 0 ) 
			System.err.println( shaderLog );
		if ( compiled == 0 ) 
			throw new AssertionError( "Could not compile shader: " + resource );
		return shader;
	}
    //------------------- modifyVersion ----------------------
    /**
     * Since GLSL versions 130 and 330 are equivalent (for our purposes)
     *    and our Linux wants 130 and Mac and Windows want 330, we'll 
     *    always use 330, but this method will intercept the ByteBuffer
     *    and edit the 3 at byte position 9 into a 1.
     * Must use exactly:
     * #version 330
     * with no extra spaces between n and 3!
     *
     * Someday we might make this smarter, which is why it is a method.
     */
    private static void modifyVersion( ByteBuffer source )
    {
        //--------- Linux test ------------
        // linux only supports version 130, which is nearly identical
        //    to version 330; we'll change shader version 330 to 
        //    shader version 130 if this is a linux machine.
        String osname = System.getProperty( "os.name" );
        if ( osname.toLowerCase().startsWith( "linux" ) )
        {
            System.err.println( "------ Linux: converting shader version ---" ); 
            byte[] versionBytes = new byte[ 12 ];
            source.get( versionBytes );
            source.rewind();
            String version = new String( versionBytes );

            if ( version.substring( 9, 12 ).equals( "330" ))
            {
                source.put( "#version 130".getBytes() );
                source.rewind();
                System.err.println( "Converting shader to version 130" );
            }
            else
            {
                String inVersion = version.substring( 0, 12 );
                System.err.println( "\n************ WARNING ******************\n"
                                  + "No change: input shader is " + inVersion
                                  + "\nexpecting 330 for portability.\n" 
                                  + "***************************************" );
            }
        }
    }

    //------------------ makeShaders --------------------------
    /**
     * Create the shader programs for this application.
	 *
	 * @throws IOException
     */
    private void makeShaders() throws IOException
    {
		int vshader = createShader( "transform.vsh", GL_VERTEX_SHADER );
		int fshader = createShader( "flat.fsh", GL_FRAGMENT_SHADER );

		int program = glCreateProgram();
		glAttachShader( program, vshader );
		glAttachShader( program, fshader );
		glBindAttribLocation( program, 0, "vPosition" );

		glLinkProgram( program );
		int linked = glGetProgrami( program, GL_LINK_STATUS );
		String programLog = glGetProgramInfoLog( program );
		if ( programLog.trim().length() > 0 ) 
        {
			System.err.println( programLog );
		}
		if ( linked == 0 ) 
        {
			throw new AssertionError( "Could not link program" );
		}
		this.shaderPgm = program;
        initProgram();  
	}

	/**
	 * Initialize shader program variables.
 	 */
	private void initProgram() 
    {
		glUseProgram( this.shaderPgm );
        // Should move uniform variable settings here?

		//glUseProgram( 0 );
	}


    //------------------ setupView --------------------------
    /**
     * We have a constant viewing and projection specification.
     *   Can define it once and send the spec to the shader.
     */
    void setupView()
    {
        // equivalent to glOrtho2D( -2, 2, -2, 2 )
        float[] pXv={ 0.5f, 0,   0, 0,  
                        0, 0.5f, 0, 0,  
                        0,  0,   1, 0,  
                        0,  0,   0, 1 };
        //FloatBuffer pXvBuf = FloatBuffer.allocate( pXv.length );
        FloatBuffer pXvBuf = BufferUtils.createFloatBuffer( pXv.length);
        pXvBuf.put( pXv ).flip();

        /**********
        // default: equivalent to glOrtho2D( -1, 1, -1, 1 )
        float pXv[]={ 1,  0, 0, 0,  
                           0,  1, 0, 0,  
                           0,  0, 1, 0,  
                           0,  0, 0, 1 };
        /**************/
        
        //--- now push the composite into a uniform var in vertex shader
        //  this id does not need to be global since we never change 
        //  projection or viewing specs in this program.
        int unif_pXv = glGetUniformLocation( shaderPgm, "projXview" );

        glUniformMatrix4fv( unif_pXv, false, pXvBuf );
    }
    //------------------------ redraw() ----------------------------
    void redraw()
    {
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
        for (Shape s: scene.getShapes())
            s.redraw();

        GL11.glFlush();
    }
    
    //------------------------- main ----------------------------------
    /**
     * main constructions the object, invokes init and terminates.
     */
    public static void main( String args[] )
    {
        P0 demo = new P0();
        try
        {
            demo.init();
        }
        finally 
        {
            System.err.println( "Finally" );
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
        }
    }
}

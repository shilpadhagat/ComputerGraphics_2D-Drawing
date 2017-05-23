
import static org.lwjgl.opengl.GL20.*;

class MyTriangle extends Triangle
{    
    //-------------------- my constructor -----------------------------


    
    public MyTriangle(float dx[], float dy[], int shaderProgram) {
 		super(shaderProgram);
 		System.out.println( "my Class");

        /** setLocation( 0, 1 );
         setColor( 1, 2, 3); **/   

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
         System.out.println( "my Class");
 	}


}

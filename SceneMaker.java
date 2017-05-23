import org.lwjgl.opengl.GL11;

public class SceneMaker {
    
    Scene scene = new Scene();
	
	public Scene makeScene(int shaderPgm)
    {
        GL11.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
        
        Triangle tri = new Triangle( shaderPgm );
        tri.setLocation( 1.25f, 0.0f );
        tri.setColor( 0, 1, 0 );  
        scene.getShapes().add( tri );
        
        tri = new Triangle( shaderPgm );
        tri.setLocation( -0.5f, -0.25f );
        tri.setColor( 0, 1, 0 );  
        tri.setSize( 0.7f, 2.0f );
        scene.getShapes().add( tri );
        
        tri = new Triangle( shaderPgm );  
        tri.setLocation( 0.3f, -0.2f );
        tri.setColor( 1, 0, 0 );
        tri.setSize( 2, 0.7f );
        scene.getShapes().add( tri );
        
        
        //This new triangle is created using the extended version of the triangle class 
        
        float dx[] = { -0.25f, 0.25f, 0.0f };
        float dy[] = { 0.0f, 0.0f, 0.5f };
        
        MyTriangle tri1 = new MyTriangle( dx, dy, shaderPgm );
        tri1.setLocation( -1.25f, 1.25f );
        tri1.setColor( 0, 0, 1 );
        tri1.setSize( 2f, 1.0f ); 
        scene.getShapes().add( tri1 );
        
        float height = 0.5f;
        float width = 0.1f;
        float xLoc = -0.5f;
        float yLoc = -0.75f;
        Rectangle rec = new Rectangle( scene.getShapes(), xLoc, yLoc, width, height, shaderPgm );
        
        float poly_x[] = { -0.25f, -0.25f, 0.20f, 0.25f, 0.5f, 0.6f };
        float poly_y[] = { 0.0f, -0.5f, 0.20f, -0.75f, -0.1f, -0.6f };
        
        Polygon poly = new Polygon(poly_x.length, poly_x, poly_y, shaderPgm);
        poly.setLocation( 1.0f, 1.75f );
        poly.setColor( 1, 1, 1 );
        scene.getShapes().add( poly );
        poly.setSize(0.25f, 0.25f);
        
        float poly_x1[] = { 0.0f, 0.0f, 0.25f, 0.25f};
        float poly_y1[] = { 0.0f, -0.25f, 0.0f,-0.25f };
        
        Polygon poly1 = new Polygon(poly_x1.length, poly_x1, poly_y1, shaderPgm);
        poly1.setLocation( 0.05f, -0.2f );
        scene.getShapes().add( poly1 ); 
        poly1.setSize( 2.f, 2.0f ); 
        poly1.setColor( 1, 0, 0 );
        
        float poly_x2[] = { 0.0f, 0.0f, 0.25f, 0.25f};
        float poly_y2[] = { 0.0f, -0.25f, 0.0f,-0.25f };
        
        Polygon poly2 = new Polygon(poly_x1.length, poly_x2, poly_y2, shaderPgm);
        poly2.setLocation( 1.2f, 0.0f );
        scene.getShapes().add( poly2 ); 
        poly2.setSize( 0.5f, 2.9f ); 
        poly2.setColor( 1, 0, 0 );
        
        float poly_x3[] = { 0.0f, 0.5f, 0.25f, 0.75f };
        float poly_y3[] = { 0.0f, 1.0f, 0.0f , 1.0f};
        
        Polygon poly3 = new Polygon(poly_x1.length, poly_x3, poly_y3, shaderPgm);
        poly3.setLocation( -0.05f, -1.7f );
        scene.getShapes().add( poly3 ); 
        poly3.setSize( 0.5f, 1.0f ); 
        poly3.setColor( 1, 1, 1 );
        
        float poly_x4[] = { -0.25f, -0.25f, 0.20f, 0.25f, 0.5f, 0.7f };
        float poly_y4[] = { 0.0f, -0.5f, 0.20f, -0.75f, -0.1f, -0.7f };
        
        
        Vertices vertices = new Vertices(poly_x4, poly_y4);
        Polygon poly4 = new Polygon(vertices, shaderPgm);
        poly4.setLocation( 0f, 1.75f );
        poly4.setColor( 1, 0, 1 );
        scene.getShapes().add( poly4 );
        poly4.setSize(0.5f, 0.55f);
        
        return scene;
    }

}

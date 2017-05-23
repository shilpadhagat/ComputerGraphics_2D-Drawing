import java.util.ArrayList;

public class Scene {

	// storage for shapes created
    ArrayList<Shape> shapes;
    
    public Scene() {
    	shapes = new ArrayList<Shape>();
    }

	public ArrayList<Shape> getShapes() {
		return shapes;
	}

	public void setShapes(ArrayList<Shape> shapes) {
		this.shapes = shapes;
	}

	
}


public class Vertices {

	float dx[];
	float dy[];
	
	public Vertices(float dx[], float dy[]){
		setDx(dx);
		setDy(dy);
	}
	
	public float[] getDx() {
		return dx;
	}
	public void setDx(float[] dx) {
		this.dx = dx;
	}
	public float[] getDy() {
		return dy;
	}
	public void setDy(float[] dy) {
		this.dy = dy;
	}
}

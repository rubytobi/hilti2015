package UI;
/**
 * 
 * Temporaeres Speichern von GPS-Daten um diese in die Javascript Dateien zu schreiben
 *
 */
public class Marker {
	private double x,y;

	
	
	public Marker(double d, double e) {
		this.x = d;
		this.y = e;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public String toString(){
		return "UNKNOWN @ Lat: "+this.x+" Long: "+this.y;
	}

}

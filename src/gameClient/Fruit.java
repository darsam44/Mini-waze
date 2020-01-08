package gameClient;

import java.util.Collection;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import MYdataStructure.node_data;
import utils.Point3D;

public class Fruit {
	Hashtable<Integer, Fruit> Fruits = new Hashtable<Integer, Fruit>();
	int TYPE;
	int VALUE;
	Point3D POINT;
	
	public Fruit () {
		this.TYPE=TYPE;
		this.POINT=POINT;
		this.VALUE= VALUE;
	}
	
	public Collection<Fruit> getF() {
		return Fruits.values();
	}
	
	public Fruit (int type , int value , Point3D p) {
		this.VALUE=value;
		this.TYPE=type;
		this.POINT=p;
	}
	
	public Point3D getPoint3D () {
		return this.POINT;
	}
	
	public int getValue () {
		return this.VALUE;
	}
	
	public int getType() {
		return this.TYPE;
	}
	

	public void init(String s) {
		try {
			double x=0,y=0,z=0,counter=0;
			String k = "";
			JSONObject obj = new JSONObject(s);
			JSONObject Fruits2 =obj.getJSONObject("Fruit");
				String point=(String) Fruits2.get("pos");
				for (int j = 0; j < point.length(); j++) {
					if (point.charAt(j) != ',') {
						k+=point.charAt(j); 
						 if (counter==2 && j == point.length()-1) {
								z= Double.parseDouble(k);
								counter=0;
								k="";
							}
					}
					else {
						if (counter==0) {
							x= Double.parseDouble(k);
							counter++;
							k="";
						}
						else if (counter==1) {
							y= Double.parseDouble(k);
							counter++;
							k="";
						}
					}
				}
				Point3D p = new Point3D(x,y,z);
				int value = Fruits2.getInt("value");
				int type =  Fruits2.getInt("type");
				Fruit f = new Fruit(type, value, p);
				Fruits.put(value, f);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}





package Datos;

import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "vinos")
public class Vinos {
	private ArrayList<Link> vinos;

	public Vinos() {
		this.vinos = new ArrayList<Link>();
	}

	@XmlElement(name="vino")
	public ArrayList<Link> getVinos() {
		return vinos;
	}

	public void setVinos(ArrayList<Link> vinos) {
		this.vinos = vinos;
	}
}

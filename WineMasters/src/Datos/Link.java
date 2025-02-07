package Datos;

import java.net.MalformedURLException;
//import java.net.MalformedURLException;
import java.net.URL;
import jakarta.xml.bind.annotation.XmlAttribute;

public class Link {
	private URL url;
	private String rel;

	public Link(String url, String rel) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.rel = rel;
	}

	public Link() {

	}

	@XmlAttribute(name="href")
	public URL getUrl() {
		return url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}
	
	@XmlAttribute
	public String getRel() {
		return rel;
	}
	
	public void setRel(String rel) {
		this.rel = rel;
	}
		
}

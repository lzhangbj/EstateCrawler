package ZillowCrawler;

import java.util.Map;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Home {
	private Double m_latitude;
	private Double m_longitude;
	private String m_url;
	private String m_streetAddr;
	private Integer m_price;
	private Float m_beds;
	private Float m_baths;
	private Integer m_area;
	private Integer m_type;
	
	public final String[] HOMETYPE = {"House", "Apartment", "Townhouse"};
	public Map<String, Integer> HOMETYPE_MAP;
	
	public JSONObject toJsonObject() {
		JSONObject myJSON = new JSONObject();
		myJSON.put("latitude", m_latitude);
		myJSON.put("longitude", m_longitude);
		myJSON.put("url", m_url);
		myJSON.put("street_address", m_streetAddr);
		myJSON.put("price", m_price);
		myJSON.put("beds", m_beds);
		myJSON.put("baths", m_baths);
		myJSON.put("area", m_area);
		myJSON.put("type", m_type);
		return myJSON;
	}
	
	public Home() {
		HOMETYPE_MAP = new HashMap<String, Integer>();
		for (int i=0;i<HOMETYPE.length; i++) {
			HOMETYPE_MAP.put(HOMETYPE[i], i);
		}
	}
	
	public boolean extractJsonInfo(String json_val) {	
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject json = (JSONObject) jsonParser.parse(json_val);
			
			JSONObject geo = (JSONObject) json.get("geo");
			m_latitude = (double) geo.get("latitude");
			m_longitude = (double) geo.get("longitude");
			m_url = (String) json.get("url");
			
			JSONObject addr = (JSONObject) json.get("address");
			m_streetAddr = (String) addr.get("streetAddress");
			 
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Double getLatitude() {
		return m_latitude;
	}
	public Double getLongitude() {
		return m_longitude;
	}
	public String getUrl() {
		return m_url;
	}
	public String getStreetAddr() {
		return m_streetAddr;
	}
	
	public void setBeds(Float num_beds) {
		m_beds = num_beds;
	}
	public Float getBeds() {
		return m_beds;
	}
	
	public void setBaths(Float num_baths) {
		m_baths = num_baths;
	}
	public Float getBaths() {
		return m_baths;
	}
	
	public void setArea(Integer area) {
		m_area = area;
	}
	public Integer getArea() {
		return m_area;
	}
	
	public void setPrice(Integer price) {
		m_price = price;
	}
	public Integer getPrice() {
		return m_price;
	}
	
	public void setType(String type) {
		type = type.split(" ")[0];
		m_type= HOMETYPE_MAP.get(type);
	}
	public String getType() {
		return HOMETYPE[m_type];
	}
}

package ZillowCrawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.DomText;

import java.util.List;
import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import me.tongfei.progressbar.*;

public class Crawler {
	private WebClient client;
	private ArrayList<Home> m_homes;;
	
	public Crawler() {
		client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		m_homes = new ArrayList<Home>();
	}
	
	private HtmlElement getOneByXPath(HtmlPage page, String xpath) {
		List<?> group = (List<?>) page.getByXPath(xpath);
		assert group.size()==1:"Multiple group list";
		HtmlElement onlyOne = (HtmlElement) group.get(0);
		return onlyOne;
	}
	
	/*
	 * criterion of a valid home
	 * defined by users
	 */
	private boolean isValidHome() {
		return true;
	}
	
	private String regexMatch(String text, Pattern p) {
		Matcher m = p.matcher(text);
		if (m.find()) {
			return text.substring(m.start(), m.end());
		}else {
			return null;
		}
	}
	
	private void searchHomes(HtmlElement e) {
		Pattern jsonPattern = Pattern.compile("(?:application\\/ld\\+json\">)(.*)(?:])");
		Pattern bigNumberPattern = Pattern.compile("\\d+[,]*\\d+");
		Pattern smallNumberPattern = Pattern.compile("\\d+[.]*\\d*");
		
		int initHomeSize = m_homes.size();
		
		List<?> children =(List<?>) e.getChildNodes();
		int childSize = children.size();
//		try(ProgressBar pb = new ProgressBar("test", childSize)){
			for (int i=0;i<children.size();i++) {
				Home home = new Home();
				
				HtmlElement homeE = (HtmlElement) children.get(i);
//				pb.step();
//				pb.setExtraMessage("get html");
//				System.out.println(homeE.asText());
				
				// get json	
				String jsonStr;
				try {
					HtmlElement scriptE = (HtmlElement) homeE.getFirstByXPath(".//script");
	//				System.out.println(scriptE.toString());
					Matcher matcher = jsonPattern.matcher(scriptE.toString());
					boolean jsonFound = matcher.find();
					jsonStr = matcher.group(1);
//					pb.step();
//					pb.setExtraMessage("get json ");
				}catch(Exception exc) {
					continue;
				}
				home.extractJsonInfo(jsonStr);
//				System.out.println("latitude " + home.getLatitude());
//				System.out.println("longitude " + home.getLongitude());
//				System.out.println("Url " + home.getUrl());
//				System.out.println("StreetAddr " + home.getStreetAddr());
				
				
				
				// get home type
				HtmlElement hometypeE = (HtmlElement) homeE.getFirstByXPath(".//div[@class='list-card-type']");
				String homeType = hometypeE.asText();
				home.setType(homeType);
//				System.out.println(homeType);
//				System.out.println("type " + home.getType());
//				pb.step();
//				pb.setExtraMessage("get type ");
				
				// get price
				HtmlElement priceE = (HtmlElement) homeE.getFirstByXPath(".//div[@class='list-card-price']");
				String homePriceText = priceE.asText();
				String homePriceFound = this.regexMatch(homePriceText, bigNumberPattern);
				Integer homePrice = homePriceFound!=null? Integer.parseInt(homePriceFound.replace(",", "")): null;
				home.setPrice(homePrice);
//				System.out.println("price " + home.getPrice());
//				pb.step();
//				pb.setExtraMessage("get price");
				
				// get beds, baths and area
				List<?> listCard = (List<?>) homeE.getByXPath(".//ul[@class='list-card-details']//li");
				if (listCard.size()!=3) {
					System.out.println("card size should be 3, but is "+listCard.size());
					break;
				}
				// get beds
				HtmlElement bedE = (HtmlElement) listCard.get(0);
				String bedText = bedE.asText();
				String bedFound = this.regexMatch(bedText, smallNumberPattern);
				Float numBeds = bedFound!=null? Float.parseFloat(bedFound) : null;
				home.setBeds(numBeds);
//				System.out.println("beds " + home.getBeds());
//				pb.step();
//				pb.setExtraMessage("get beds");
				
				// get baths
				HtmlElement bathE = (HtmlElement) listCard.get(1);
				String bathText = bathE.asText();
				String bathFound = this.regexMatch(bathText, smallNumberPattern);
				Float numBaths = bathFound!=null? Float.parseFloat(bathFound) : null;
				home.setBaths(numBaths);
//				System.out.println("baths " + home.getBaths());
//				pb.step();
//				pb.setExtraMessage("get bath");
				
				// get area
				HtmlElement areaE = (HtmlElement) listCard.get(2);
				String areaText = areaE.asText();
				String areaFound = this.regexMatch(areaText, bigNumberPattern);
				Integer area = areaFound!=null? Integer.parseInt(areaFound.replace(",", "")): null;
				home.setArea(area);
//				System.out.println("area " + home.getArea());
//				pb.step();
//				pb.setExtraMessage("get area");
				
				m_homes.add(home);
//				pb.step();
//				pb.setExtraMessage("retriving home ");
//				break;
			}
//		}
		System.out.println(m_homes.size()-initHomeSize + " homes retrieved");
	}
	
	private void print(String s) {
		System.out.println(s);
	} 
	
	public void init() {
		m_homes.clear();
	}
	
	public int getSize() {
		return m_homes.size();
	}
	
	public void saveJson(String save_file) throws IOException {
		FileWriter file = new FileWriter(save_file);
		JSONObject fileObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (int i=0;i<m_homes.size();i++) {
			Home home = m_homes.get(i);
			jsonArray.add(home.toJsonObject());
		}
		fileObj.put("data", jsonArray);
		file.write(fileObj.toJSONString());
		file.close();
		System.out.println("saved " + m_homes.size() + " homes to " + save_file);
	}
	
	public void crawl(String baseurl) {
		try {
			HtmlPage page = client.getPage(baseurl);
			HtmlElement homeListE = this.getOneByXPath(page, "//ul[contains(@class, 'photo-cards') and "
																+ "contains(@class, 'photo-cards_wow') and "
																+ "contains(@class, 'photo-cards_short')]");
			searchHomes(homeListE);
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
}

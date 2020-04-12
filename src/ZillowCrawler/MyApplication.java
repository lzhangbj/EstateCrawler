package ZillowCrawler;

import java.io.File;
import java.io.IOException;
import me.tongfei.progressbar.*;

public class MyApplication {
	private static Crawler crawler = new Crawler();
	private static String zillow_url = "https://www.zillow.com/pittsburgh-pa/rentals/3-_beds/1.0-_baths/";
	private static String searchQuery = "?searchQueryState=%7B%22pagination%22%3A%7B%7D%2C%22mapBounds%22%3A%7B%22west%22%3A-80.23239418457034%2C%22east%22%3A-79.69269081542971%2C%22south%22%3A40.29292193923852%2C%22north%22%3A40.599864771238394%7D%2C%22regionSelection%22%3A%5B%7B%22regionId%22%3A26529%2C%22regionType%22%3A6%7D%5D%2C%22isMapVisible%22%3Atrue%2C%22mapZoom%22%3A11%2C%22filterState%22%3A%7B%22beds%22%3A%7B%22min%22%3A3%7D%2C%22baths%22%3A%7B%22min%22%3A1%7D%2C%22con%22%3A%7B%22value%22%3Afalse%7D%2C%22pmf%22%3A%7B%22value%22%3Afalse%7D%2C%22fore%22%3A%7B%22value%22%3Afalse%7D%2C%22apa%22%3A%7B%22value%22%3Afalse%7D%2C%22auc%22%3A%7B%22value%22%3Afalse%7D%2C%22nc%22%3A%7B%22value%22%3Afalse%7D%2C%22fr%22%3A%7B%22value%22%3Atrue%7D%2C%22fsbo%22%3A%7B%22value%22%3Afalse%7D%2C%22cmsn%22%3A%7B%22value%22%3Afalse%7D%2C%22pf%22%3A%7B%22value%22%3Afalse%7D%2C%22fsba%22%3A%7B%22value%22%3Afalse%7D%2C%22lau%22%3A%7B%22value%22%3Atrue%7D%7D%2C%22isListVisible%22%3Atrue%7D";
	private static String saveFile = "results/json/3b1b.json";
	static public void main(String argv[]) throws IOException {
		int numPage = 10;
		File file = new File(saveFile);
		if (!file.isDirectory())
			file.getParentFile().mkdirs();
		try(ProgressBar pb = new ProgressBar("zillow crawler", numPage)){
			for (int page=1;page<=numPage;page++) {
				String baseurl;
				if (page==1)
					baseurl = zillow_url + searchQuery;
				else
					baseurl = zillow_url + page + "_p/";
				try {
					crawler.crawl(baseurl);
				}catch (Exception e) {
					break;
				}
				pb.step();
				pb.setExtraMessage("crawled page "+ page);
			}
		}
		System.out.println("finished crawling " + crawler.getSize() + " houses");
		crawler.saveJson(saveFile);
	}
}

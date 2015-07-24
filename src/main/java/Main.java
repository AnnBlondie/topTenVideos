
import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {
	public static void main(String[] args) throws IOException {
		
		String video = "http://www.youtube.com/watch?v=LmM1_BiuqXs";
		int numbers =5;
		int deep = 3;
		TreeMap<String, Integer> top = getTopVideos(video, numbers, deep);
		System.out.println(top);
}
	
	public static TreeMap<String, Integer> getTopVideos(String video, int numbers, int deep) throws IOException {
		TreeMap<String, Integer> top = new TreeMap<String, Integer>();
		Document doc = Jsoup.connect(video).get();
		Elements link = doc.getElementsByAttributeValue("class",
				"watch-view-count");
		int rating = Integer.parseInt(link.text().replaceAll("[^0-9]", ""));
		
		Map<String, Integer> temporaryVideos = new HashMap<String, Integer>();
		temporaryVideos.put(video, rating);
		
		top.putAll(temporaryVideos);

		int currentLevel = 0;
		
		while(currentLevel!=deep){

			Map<String, Integer> nextLevelVideos = new HashMap<String, Integer>();

			for(String name: temporaryVideos.keySet()){
				nextLevelVideos.putAll(getRelatedVideos( name, numbers));
			}
			
			top.putAll(nextLevelVideos);
			
			temporaryVideos.clear();
			temporaryVideos.putAll(nextLevelVideos);
			
			currentLevel++;
		}
		
        return top;
	}

	public static Map<String, Integer> getRelatedVideos(String video, int numbers) throws IOException {
		Map<String, Integer> videos = new HashMap<String, Integer>();

		Document doc = Jsoup.connect(video).get();
	
		Elements divElements = doc.getElementsByAttributeValue("class",
				"thumb-wrapper");
		
		int counter = 0;
		while (!divElements.isEmpty() && counter<numbers) {
			Element el = divElements.first();
			
			String newVideo = "http://www.youtube.com"+el.select("a").attr("href");
			
			Document newDoc = Jsoup.connect(newVideo).get();
			Elements link = newDoc.getElementsByAttributeValue("class",
					"watch-view-count");
			int rating = Integer.parseInt(link.text().replaceAll("[^0-9]", ""));
			
			videos.put(newVideo, rating);
			
			counter++;
			divElements.remove(el);
		}
		return videos;
	}
}
import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {
	public static <V> void main(String[] args) throws IOException {

		String video = "http://www.youtube.com/watch?v=LmM1_BiuqXs";
		int numbers = 3;
		int deep = 3;
		TreeMap<String, Integer> top = SortByValue(getTopVideos(video, numbers,
				deep));

		for (Map.Entry<String, Integer> pair : top.entrySet()) {
			System.out.println(pair.getKey() + ":" + pair.getValue());
		}

	}

	public static HashMap<String, Integer> getTopVideos(String video,
			int numbers, int deep) throws IOException {
		HashMap<String, Integer> top = new HashMap<String, Integer>();
		Document doc = Jsoup.connect(video).get();
		Elements link = doc.getElementsByAttributeValue("class",
				"watch-view-count");
		int rating = Integer.parseInt(link.text().replaceAll("[^0-9]", ""));

		Map<String, Integer> temporaryVideos = new HashMap<String, Integer>();
		temporaryVideos.put(video, rating);

		top.putAll(temporaryVideos);

		int currentLevel = 0;

		while (currentLevel != deep) {

			Map<String, Integer> nextLevelVideos = new HashMap<String, Integer>();

			for (String name : temporaryVideos.keySet()) {
				nextLevelVideos.putAll(getRelatedVideos(name, numbers));
			}

			top.putAll(nextLevelVideos);

			temporaryVideos.clear();
			temporaryVideos.putAll(nextLevelVideos);

			currentLevel++;
		}

		return top;
	}

	public static Map<String, Integer> getRelatedVideos(String video,
			int numbers) throws IOException {
		Map<String, Integer> videos = new HashMap<String, Integer>();

		Document doc = Jsoup.connect(video).get();

		Elements divElements = doc.getElementsByAttributeValue("class",
				"thumb-wrapper");

		int counter = 0;
		while (!divElements.isEmpty() && counter < numbers) {
			Element el = divElements.first();

			String newVideo = "http://www.youtube.com"
					+ el.select("a").attr("href");

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

	public static TreeMap<String, Integer> SortByValue(
			HashMap<String, Integer> map) {
		ValueComparator vc = new ValueComparator(map);
		TreeMap<String, Integer> tempMap = new TreeMap<String, Integer>(vc);
		tempMap.putAll(map);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(vc);
		
		for(int i=0; i<10 || i<tempMap.size(); i++){
			sortedMap.put(tempMap.firstKey(), tempMap.get(tempMap.firstKey()));
			tempMap.remove(tempMap.firstEntry());
		}

		return sortedMap;
	}
}

class ValueComparator implements Comparator<String> {

	Map<String, Integer> map;

	public ValueComparator(Map<String, Integer> base) {
		this.map = base;
	}

	public int compare(String a, String b) {
		if (map.get(a) >= map.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}
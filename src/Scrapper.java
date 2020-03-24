import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.opencsv.CSVWriter;

public class Scrapper {
	
	public static ArrayList<String> getItems() {
			
		ArrayList<String> items = new ArrayList<String>();
		final String url = "https://townshiptale.gamepedia.com/Category:Items";
		try {
			final Document document = Jsoup.connect(url).get();
			for (Element row : document.select("div.mw-category div.mw-category-group ul li")) {
				
				items.add(row.text());
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return items;
	}
	
	public static String saveImage(String image_url) throws IOException {
		
		URL url = new URL(image_url);
		String dest_name = url.getFile().substring(url.getFile().lastIndexOf("/"));
		String result = dest_name.substring(dest_name.indexOf("/") + 1, dest_name.indexOf("?"));
		dest_name = result.substring(result.lastIndexOf("-") +1);
		InputStream in = new BufferedInputStream(url.openStream());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while (-1!=(n=in.read(buf)))
		{
		   out.write(buf, 0, n);
		}
		out.close();
		in.close();
		byte[] response = out.toByteArray();
		
		FileOutputStream fos = new FileOutputStream("./images/" + dest_name);
		fos.write(response);
		fos.close();
		
		return dest_name;
		
	}

	
	public static void main(String[] args) throws IOException {
		ArrayList<String> category_items = getItems();
		ArrayList<String[]> csv_output = new ArrayList<String[]> ();
		for (String item : category_items) {
			final String url = "https://townshiptale.gamepedia.com/" + item;
			try {
				final Document document = Jsoup.connect(url).get();
				String item_name = document.select("h1.firstHeading").text();
				String item_description = document.select("div.mw-parser-output p").first().text();
				String f_name;
				String image_path = "C:\\Users\\Bibhas\\Desktop\\example\\Web_scrapper\\images\\";
				String image_url = document.select("table.infobox").select("table.infobox:nth-of-type(1)").select("tbody").select("tr:nth-of-type(2)").select("th").select("a").select("img").attr("src");
				try {
					if (!image_url.equals("")) {
						f_name = saveImage(image_url);
						image_path += f_name;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				String[] result = {item_name, item_description, image_path};
				csv_output.add(result);
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Write to CSV file
				
		FileWriter file = new FileWriter("data.csv");
		CSVWriter csv_writer = new CSVWriter(file); 
		
		String[] header = {"Name", "Description", "Image Path"};
		csv_writer.writeNext(header);
		
		for (String[] data : csv_output) {
			csv_writer.writeNext(data);
		}
		
		csv_writer.close();
		
		System.out.println("Scrapping Successful");
	}
}

package car;

import java.net.URL;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class query {
	private static String baseUrl = "http://www.autohome.com.cn/";
	private static JSONObject jObject = new JSONObject();

	public static void start() {
		String url;
		queryABydUrl(baseUrl+"car/");
		//from A to Z
		for (int i = 66; i <= 90; i++) {
			url=baseUrl+"grade/carhtml/"+(char)i+".html";
			queryBToZBydUrl((char)i,url);
		}
		System.out.println(jObject.toString());
	}

	/**
	 * Query page A
	 * 
	 * @param url
	 * @return false if didn't get the page
	 */
	public static boolean queryABydUrl(String url) {
		Connection con = Jsoup.connect(url);// 获取连接
		con.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");// 配置模拟浏览器
		Response rs;
		try {
			rs = con.execute();
			Document domTree = Jsoup.parse(rs.body());// 转换为Dom树
			Element nodeA = domTree.getElementById("tab-content-item1")
					.getElementsByClass("uibox").get(0);
			// System.out.println(nodeA.child(1).toString());
			jObject.append("A", getJsonObjectFromContent(nodeA.child(1).toString()));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			// TODO: handle exception
		}
		return true;
	}
	
	
	
	/**
	 * Query page B-Z
	 * 
	 * @param url
	 * @return false if didn't get the page
	 */
	public static boolean queryBToZBydUrl(char firstLetter,String url) {
		try {
			//rs = con.execute();
			Document doc = Jsoup.parse(new URL(url).openStream(), "gb2312", baseUrl);
			jObject.append(String.valueOf(firstLetter), getJsonObjectFromContent(doc.toString()));
		} catch (Exception e) {
			System.out.println(url);
			e.printStackTrace();
			return false;
			// TODO: handle exception
		}
		return true;
	}

	/**
	 * Get jsonObject from ontent
	 * 
	 * @param content
	 * @return jsonObject
	 */

	public static JSONObject getJsonObjectFromContent(String content) {
		JSONObject brandsObject = new JSONObject();
		Document domTree = Jsoup.parse(content);
		Elements brandsTrees = domTree.getElementsByTag("dl");
		JSONObject brandObject, seriObject, modelObject;
		for (Element brandNode : brandsTrees) {
			brandObject = new JSONObject();
			String brandHref = brandNode.child(0).getElementsByTag("a").get(0)
					.attr("href");
			String brandImg = brandNode.child(0).getElementsByTag("img").get(0)
					.attr("src");
			String brandName = brandNode.child(0).getElementsByTag("a").get(1)
					.text();
			brandObject.append("brand", brandName);
			brandObject.append("img", brandImg);
			brandObject.append("href", brandHref);
			//System.out.println(brandName);
			Elements serisNamesNodes = brandNode.child(1).getElementsByClass(
					"h3-tit");
			for (Element serisNamenode : serisNamesNodes) {
				seriObject = new JSONObject();
				String seriName = serisNamenode.text();
				//System.out.println(seriName);
				Elements modelsNodes = serisNamenode.nextElementSibling()
						.getElementsByTag("li");
				for (Element modelNode : modelsNodes) {
					if (!modelNode.hasClass("dashline")) {
						modelObject = new JSONObject();
						String modelName = modelNode.child(0).text();
						String modelHref = modelNode.child(0).child(0)
								.attr("href");
						
						
						Elements priceNode=modelNode.getElementsByClass("red");
						String modelPrice="";
						if (priceNode.size()!=0) {
							modelPrice = priceNode.text();
						}
						else {
							modelPrice ="暂无";
						}
					//	System.out.println(modelName+modelPrice);
						modelObject.append("href", modelHref);
						modelObject.append("price", modelPrice);
						modelObject.append("name", modelName);
						seriObject.append(modelName, modelObject);
					}
				}
			//	System.out.println(seriObject);
				brandObject.append(seriName, seriObject);
			}
			//System.out.println(brandObject);
			brandsObject.append(brandName, brandObject);
		}
		return brandsObject;
	}
}

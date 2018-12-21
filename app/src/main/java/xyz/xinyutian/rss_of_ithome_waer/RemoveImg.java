package xyz.xinyutian.rss_of_ithome_waer;

public class RemoveImg {
	
	public static String removeit(String content) {
		content=content.replace("\\", "");
		if (content.indexOf("@")!=-1) {
			int index=content.indexOf("@");
			content=content.substring(0, index)+"\"/>";
			System.err.println(index);
		}
		return content;
	}

}

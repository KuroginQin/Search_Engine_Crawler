package com.Crawler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.Entity.ResultPage;
import com.Store.StoreResultPage;

//爬取结果页面的类

public class ResultPageCrawler
{
	private ResultPage resultPage; //当前结果页面实体类
	private String MD5Value = ""; //网页内容对应的MD5值
	
	private double ratioThreshold = 0.3; //链接-文字比阈值 (0.25)
	
	//构造方法
	public ResultPageCrawler(ResultPage resultPage)
	{
		System.out.println("-Crawl Result Page-");
		this.resultPage = resultPage;
		String URL = resultPage.getURL();
		String HTML = "";
		try
		{
			//获取结果页面的HTML文本
			Document doc = Jsoup.connect(URL).timeout(50000).get();
			//计算网页内容对应的MD5值
			MD5Value = calMD5Str(doc);
			HTML = doc.html();
			
			//System.out.println(HTML);
		}
		catch(Exception e)
		{
			System.out.println("-Exception URL: " + URL);
			e.printStackTrace();
		}
		//获取结果页面正文
		String content = getPageContent(HTML);
		//添加正文内容
		this.resultPage.addContent(content);
		System.out.println("-Title: " + resultPage.getTitle());
		System.out.println("-URL: " + resultPage.getURL());
		System.out.println("-Content: " + resultPage.getContent());
		System.out.println();
	}
	
	//获取当前页面实体类的方法
	public ResultPage getResultPage()
	{
		return resultPage;
	}
	
	//获取网页内容MD5值的方法
	public String getMD5Value()
	{
		return MD5Value;
	}
	
	//计算网页MD5值的方法
	private String calMD5Str(Document pageDoc)
	{
		String content = pageDoc.text(); //获取网页内容
		content = content.replaceAll("\\s*|\t|\r|\n", ""); //去除内容中的空格、制表符等
		//计算内容文本对应的MD5值
		MessageDigest md = null;
		byte[] MD5Message = null;
		try
		{
			md = MessageDigest.getInstance("MD5");
			MD5Message = md.digest(content.getBytes()); 
		}
		catch(NoSuchAlgorithmException e)
		{
			System.out.println("-No Such Algorithm-");
		}
		//组合生成MD5字符串
		String MD5Str = "";
		for(int i=0;i<MD5Message.length;i++)
			MD5Str += MD5Message[i];
		return MD5Str;
	}
	
	//提取网页正文节点的方法
	//广度优先遍历DOM树不断删除"冗余"节点,提取内容节点
	private Element getContentNode(Element node)
	{
		//不考虑超链接标签
	    if (node.tagName() == "a") 
	           return node;
	     //遍历当前节点的子节点
	     Elements childNodes = node.children();
	     for(Element curNode:childNodes)
	     {
	    	 //如果当前节点没有内容,则删除当前节点
	    	 if(!curNode.hasText())
	    		 curNode.remove();
	    	 else //否则计算当前节点的连接-文字比
	    	 {
	    		 int linkCount = 0; //节点内的连接数
	    		 double wordCount = 1 + ratioThreshold; //节点内的文字长度(避免计算链接-文字比时出现除零异常,将默认值设置为大于0)
	    		 int puncCount = 0; //节点内标点符号数
	    		 double linkWordRatio = 0; //当前节点的为连接-文字比
	    		 
	    		 //计算当前节点的连接数
	    		 Elements linkNodes = curNode.select("a[href]");
	    		 linkCount = linkNodes.size();
	    		 //计算当前节点的文字长度
	    		 String curContent = curNode.text();
	    		 if(curContent.length()>0) 
	    			 wordCount = curContent.length();
	    		 //计算当前节点的链接-文字比
	    		 linkWordRatio = linkCount/wordCount;
	    		 //计算当前节点的标点符号数
	    		 String[] puncSet = { ",", ";", ".", "\"", "'", "\\?", "。", ":", "，" };
	    		 for(int i=0;i<puncSet.length;i++)
	    			 puncCount += curNode.text().split(puncSet[i]).length-1;
	    		 
	    		 //如果当前节点没有标签符号,则删除当前节点
	    		 if(puncCount<1)
	    			 curNode.remove();
	    		 else if(linkWordRatio>ratioThreshold) //如果当前节点的文字-链接比超过阈值,则删除当前节点
	    			 curNode.remove();
	    		 else //否则递归调用自身,考察当前节点的孩子节点
	    			 getContentNode(curNode);
	    	 }
	    }
	     return node;
	}
		
	//提取正文内容的方法
	private String getPageContent(String HTML)
	{	
		//删除冗余的HTML标签
		HTML = HTML.replaceAll("(?is)<!DOCTYPE.*?>", "");
		HTML = HTML.replaceAll("(?is)<!--.*?-->", "");
		HTML = HTML.replaceAll("(?is)<script.*?>.*?</script>", "");
		HTML = HTML.replaceAll("(?is)<style.*?>.*?</style>", "");
		Document contentDoc = Jsoup.parse(HTML);
		//考虑<body>节点
		Element bodyNode = contentDoc.select("body").first();
		//获取内容节点
		Element contentNode = getContentNode(bodyNode); 
		String content = contentNode.text();
		content = content.replaceAll("<.*?>|[{].*?[}]|\t", ""); //去除冗余的HTML, JS代码
		content = content.replaceAll("\\s{2,}", " "); //将2个以上空格替换为1个
		return content;
	}
	
	//测试
	/*public static void main(String args[])
	{
		//ResultPage page1 = new ResultPage(3, 7, "网络蜘蛛(网络爬虫)webspider完整版-CSDN下载", "http://download.csdn.net/detail/tjx2006/2621909");
		//ResultPage page2 = new ResultPage(3, 8, "AppleBot:苹果有自己的网络爬虫是真的_36氪", "http://36kr.com/p/532608.html");
		//ResultPage page3 = new ResultPage(3, 9, "多可网络爬虫|多可网络爬虫 v1.0绿色版_绿色下载站", "http://www.greenxiazai.com/soft/11911.html");
		//page1 = new ResultPageCrawler(page1).getResultPage();
		//page2 = new ResultPageCrawler(page2).getResultPage();
		//page3 = new ResultPageCrawler(page3).getResultPage();
		//new StoreResultPage(page1, 0);
		//new StoreResultPage(page2, 1);
		//new StoreResultPage(page3, 2);
		
		ResultPage page = new ResultPage(0, 0, "北京大学", "https://baike.so.com/doc/845213-893738.html");
		page = new ResultPageCrawler(page).getResultPage();
	}*/
}
package com.Crawler;

import java.util.List;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.Entity.ResultPage;

import com.Entity.SearchPage;

//爬取搜索页面的类

public class SearchPageCrawler
{	
	private boolean validType = true; //判断搜索引擎类型是否合法的标志变量
	private List<ResultPage> resultPageList = new ArrayList<ResultPage>();
	
	//构造方法
	//keyword 搜索关键词; searchType 搜索引擎类型; pageIndex 搜索页面索引
	//规定searchType的值只能为{baidu, sogou, 360, bing}中的1个,否则为不合法类型
	public SearchPageCrawler(String keyword, String searchType, int pageIndex)
	{
		System.out.println("Crawl Result Page-");
		String pageURL = ""; //搜索页面URL
		//根据搜索引擎类型调用不同的爬取方法
		if(searchType.equals("baidu")) //百度搜索引擎
		{
			//生成搜索页面URL
			pageURL = "http://www.baidu.com/s?wd=" + keyword + "&pn=" + (pageIndex*10);
			//爬取搜索页面
			crawlBaiduSearchPage(pageURL, pageIndex);
		}
		else if(searchType.equals("sogou")) //搜狗搜索引擎
		{
			//生成搜索页面URL
			pageURL =  "http://www.sogou.com/web?query=" + keyword + "&page=" + (pageIndex+1);
			//爬取搜索页面
			crawlSogouSearchPage(pageURL, pageIndex);
		}
		else if(searchType.equals("360")) //360搜索引擎
		{
			//生成搜索页面URL
			//将关键词转换为UTF-8编码
			String keywordUTF8 = "";
			try
			{
				keywordUTF8 = URLEncoder.encode(keyword, "UTF-8");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			//构造搜索页面URL
			pageURL = "http://www.so.com/s?q=" + keywordUTF8 + "&pn=" + (pageIndex+1);
			//爬取搜索页面
			crawl360SearchPage(pageURL, pageIndex);
		}
		else if(searchType.equals("bing")) //bing搜索引擎
		{
			//生成搜索页面URL
			//将关键词转换为UTF-8编码
			String keywordUTF8 = "";
			try
			{
				keywordUTF8 = URLEncoder.encode(keyword, "UTF-8");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			//构造搜索页面URL
			pageURL = "http://cn.bing.com/search?q=" + keywordUTF8 + "&first=" + (pageIndex*10+1);
			//爬取搜索页面
			crawlBingSearchPage(pageURL, pageIndex);
		}
		else 
			validType = false;
	}
	
	//爬取百度搜索引擎搜索页面的方法
	private void crawlBaiduSearchPage(String pageURL, int searchPageIndex)
	{
		try
		{
			//获取网页的HTML文本
			Document doc = Jsoup.connect(pageURL).timeout(10000).get();
			//获取结果页面URL
			Elements nodes = doc.select("div[class=result c-container],div[class=result-op c-container xpath-log]"); 
			System.out.println("-Total Result Page: " + nodes.size());
			System.out.println();
			int resultPageIndex = 0;
			for(Element cnode:nodes) 
			{
				//提取标题
				String title = cnode.select("h3").first().text();
				//提取外部链接URL
				String infoBlock = cnode.select("h3[class=t],h3[class=t c-gap-bottom-small]").first().toString(); 
				//使用正则表达式提取外部连接地址
				String regex = "href=\".*?\"";
				Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
				Matcher match = pattern.matcher(infoBlock);
				String outerURL = "";
				if(match.find())
					outerURL = match.group();
				outerURL = outerURL.replaceAll("href=|\"", "");
				//获取重定向后地址
				HttpURLConnection conn = (HttpURLConnection) new URL(outerURL).openConnection();  
				conn.setInstanceFollowRedirects(false);  
				conn.setConnectTimeout(5000);  
				String redirURL = conn.getHeaderField("Location");  
				System.out.println("-Search Page Index: " + searchPageIndex);
				System.out.println("-Result Page Index: " + resultPageIndex);
				System.out.println("-Title: " + title);
				System.out.println("-URL: " + redirURL);
				System.out.println();
				//声明结果页面实体类
				ResultPage curResultPage = new ResultPage(searchPageIndex, resultPageIndex, title, redirURL);
				resultPageList.add(curResultPage); //添加结果页面列表
				++resultPageIndex;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//爬取搜狗搜索引擎搜索页面的方法
	private void crawlSogouSearchPage(String pageURL, int searchPageIndex)
	{
		try
		{
			//获取网页的HTML文本
			Document doc = Jsoup.connect(pageURL).timeout(10000).get();
			//获取结果页面URL
			Elements nodes = doc.select("h3[class=vrTitle],h3[class=pt]"); 
			System.out.println("-Total Result Page: " + nodes.size());
			System.out.println();
			int resultPageIndex = 0;
			for(Element cnode:nodes) 
			{
				//提取标题
				String title = cnode.text();
				String infoBlock = cnode.toString(); 
				//使用正则表达式提取结果页面URL
				String regex = "href=\".*?\"";
				Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
				Matcher match = pattern.matcher(infoBlock);
				String resultURL = "";
				if(match.find())
					resultURL = match.group();
				resultURL = resultURL.replaceAll("href=|\"", "");
				System.out.println("-Search Page Index: " + searchPageIndex);
				System.out.println("-Result Page Index: " + resultPageIndex);
				System.out.println("-Title: " + title);
				System.out.println("-URL: " + resultURL);
				System.out.println();
				//声明结果页面实体类
				ResultPage curResultPage = new ResultPage(searchPageIndex, resultPageIndex, title, resultURL);
				resultPageList.add(curResultPage); //添加结果页面列表
				++resultPageIndex;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//爬取360搜索引擎搜索页面的方法
	private void crawl360SearchPage(String pageURL, int searchPageIndex)
	{
		try
		{
			//获取网页的HTML文本
			Document doc = Jsoup.connect(pageURL).timeout(10000).get();
			//获取结果页面URL
			Elements nodes = doc.select("h3[class=res-title]"); 
			System.out.println("-Total Result Page: " + nodes.size());
			System.out.println();
			int resultPageIndex = 0;
			for(Element cnode:nodes) 
			{
				//提取标题
				String title = cnode.text();
				String infoBlock = cnode.toString(); 
				//使用正则表达式提取结果页面URL
				String regex = "data-url=\".*?\"";
				Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
				Matcher match = pattern.matcher(infoBlock);
				String resultURL = "";
				if(match.find())
					resultURL = match.group();
				resultURL = resultURL.replaceAll("data-url=|\"", "");
				if(resultURL.equals(""))
				{
					regex = "href=\".*?\"";
					pattern = Pattern.compile(regex, Pattern.DOTALL);
					match = pattern.matcher(infoBlock);
					if(match.find())
						resultURL = match.group();
					resultURL = resultURL.replaceAll("href=|\"", "");	
				}
				System.out.println("-Search Page Index: " + searchPageIndex);
				System.out.println("-Result Page Index: " + resultPageIndex);
				System.out.println("-Title: " + title);
				System.out.println("-URL: " + resultURL);
				System.out.println();
				//声明结果页面实体类
				ResultPage curResultPage = new ResultPage(searchPageIndex, resultPageIndex, title, resultURL);
				resultPageList.add(curResultPage); //添加结果页面列表
				++resultPageIndex;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//爬取bing搜索引擎搜索页面的方法
	private void crawlBingSearchPage(String pageURL, int searchPageIndex)
	{
		try
		{
			//获取网页的HTML文本
			Document doc = Jsoup.connect(pageURL).timeout(10000).get();
			//获取结果页面URL
			Elements nodes = doc.select("li[class=b_algo]>h2,div[class=bm_ctn]>h2"); 
			System.out.println("-Total Result Page: " + nodes.size());
			System.out.println();
			int resultPageIndex = 0;
			for(Element cnode:nodes) 
			{
				//提取标题
				String title = cnode.text();
				String infoBlock = cnode.toString(); 
				//使用正则表达式提取结果页面URL
				String regex = "href=\".*?\"";
				Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
				Matcher match = pattern.matcher(infoBlock);
				String resultURL = "";
				if(match.find())
					resultURL = match.group();
				resultURL = resultURL.replaceAll("href=|\"", "");
				System.out.println("-Search Page Index: " + searchPageIndex);
				System.out.println("-Result Page Index: " + resultPageIndex);
				System.out.println("-Title: " + title);
				System.out.println("-URL: " + resultURL);
				System.out.println();
				//声明结果页面实体类
				ResultPage curResultPage = new ResultPage(searchPageIndex, resultPageIndex, title, resultURL);
				resultPageList.add(curResultPage); //添加结果页面列表
				++resultPageIndex;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//获取结果页面列表的方法
	public List<ResultPage> getResultPageList()
	{
		//判断搜索引擎类型是否合法
		if(validType)
			return resultPageList;
		else
		{
			System.out.println("-Invalid Search Engine Type-");
			return null;
		}
	}
	
	//测试
	/*public static void main(String args[])
	{
		//new SearchPageCrawler("网络爬虫", "baidu", 0);
		//new SearchPageCrawler("网络爬虫", "sogou", 1);
		//new SearchPageCrawler("网络爬虫", "360", 0);
		//new SearchPageCrawler("网络爬虫", "bing", 0);
		SearchPage searchPage = new SearchPage("baidu");
		
		SearchPageCrawler searchCralwer2 = new SearchPageCrawler("网络爬虫", "baidu", 1);
		List<ResultPage> resultPageList2 = searchCralwer2.getResultPageList();
		searchPage.addResultPageList(resultPageList2);
		
		SearchPageCrawler searchCralwer3 = new SearchPageCrawler("网络爬虫", "baidu", 2);
		List<ResultPage> resultPageList3 = searchCralwer3.getResultPageList();
		searchPage.addResultPageList(resultPageList3);
		
		SearchPageCrawler searchCralwer1 = new SearchPageCrawler("网络爬虫", "baidu", 0);
		List<ResultPage> resultPageList1 = searchCralwer1.getResultPageList();
		searchPage.addResultPageList(resultPageList1);
		
		searchPage.sortResultPageList();
		
		List<ResultPage> resultPageList0 = searchPage.getResultPageList();
		System.out.println("-Result Page List: ");
		System.out.println();
		System.out.println(resultPageList0.size());
		for(int i=0;i<resultPageList0.size();i++)
		{
			ResultPage curResultPage = resultPageList0.get(i);
			int searchPageIndex = curResultPage.getSearchPageIndex();
			int resultIndex = curResultPage.getResultIndex();
			String title = curResultPage.getTitle();
			String URL = curResultPage.getURL();
			System.out.println("-Search Page Index: " + searchPageIndex);
			System.out.println("-Result Page Index: " + resultIndex);
			System.out.println("-Title: " + title);
			System.out.println("-URL: " + URL);
			System.out.println();
		}
	}*/
}
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

//��ȡ����ҳ�����

public class SearchPageCrawler
{	
	private boolean validType = true; //�ж��������������Ƿ�Ϸ��ı�־����
	private List<ResultPage> resultPageList = new ArrayList<ResultPage>();
	
	//���췽��
	//keyword �����ؼ���; searchType ������������; pageIndex ����ҳ������
	//�涨searchType��ֵֻ��Ϊ{baidu, sogou, 360, bing}�е�1��,����Ϊ���Ϸ�����
	public SearchPageCrawler(String keyword, String searchType, int pageIndex)
	{
		System.out.println("Crawl Result Page-");
		String pageURL = ""; //����ҳ��URL
		//���������������͵��ò�ͬ����ȡ����
		if(searchType.equals("baidu")) //�ٶ���������
		{
			//��������ҳ��URL
			pageURL = "http://www.baidu.com/s?wd=" + keyword + "&pn=" + (pageIndex*10);
			//��ȡ����ҳ��
			crawlBaiduSearchPage(pageURL, pageIndex);
		}
		else if(searchType.equals("sogou")) //�ѹ���������
		{
			//��������ҳ��URL
			pageURL =  "http://www.sogou.com/web?query=" + keyword + "&page=" + (pageIndex+1);
			//��ȡ����ҳ��
			crawlSogouSearchPage(pageURL, pageIndex);
		}
		else if(searchType.equals("360")) //360��������
		{
			//��������ҳ��URL
			//���ؼ���ת��ΪUTF-8����
			String keywordUTF8 = "";
			try
			{
				keywordUTF8 = URLEncoder.encode(keyword, "UTF-8");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			//��������ҳ��URL
			pageURL = "http://www.so.com/s?q=" + keywordUTF8 + "&pn=" + (pageIndex+1);
			//��ȡ����ҳ��
			crawl360SearchPage(pageURL, pageIndex);
		}
		else if(searchType.equals("bing")) //bing��������
		{
			//��������ҳ��URL
			//���ؼ���ת��ΪUTF-8����
			String keywordUTF8 = "";
			try
			{
				keywordUTF8 = URLEncoder.encode(keyword, "UTF-8");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			//��������ҳ��URL
			pageURL = "http://cn.bing.com/search?q=" + keywordUTF8 + "&first=" + (pageIndex*10+1);
			//��ȡ����ҳ��
			crawlBingSearchPage(pageURL, pageIndex);
		}
		else 
			validType = false;
	}
	
	//��ȡ�ٶ�������������ҳ��ķ���
	private void crawlBaiduSearchPage(String pageURL, int searchPageIndex)
	{
		try
		{
			//��ȡ��ҳ��HTML�ı�
			Document doc = Jsoup.connect(pageURL).timeout(10000).get();
			//��ȡ���ҳ��URL
			Elements nodes = doc.select("div[class=result c-container],div[class=result-op c-container xpath-log]"); 
			System.out.println("-Total Result Page: " + nodes.size());
			System.out.println();
			int resultPageIndex = 0;
			for(Element cnode:nodes) 
			{
				//��ȡ����
				String title = cnode.select("h3").first().text();
				//��ȡ�ⲿ����URL
				String infoBlock = cnode.select("h3[class=t],h3[class=t c-gap-bottom-small]").first().toString(); 
				//ʹ��������ʽ��ȡ�ⲿ���ӵ�ַ
				String regex = "href=\".*?\"";
				Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
				Matcher match = pattern.matcher(infoBlock);
				String outerURL = "";
				if(match.find())
					outerURL = match.group();
				outerURL = outerURL.replaceAll("href=|\"", "");
				//��ȡ�ض�����ַ
				HttpURLConnection conn = (HttpURLConnection) new URL(outerURL).openConnection();  
				conn.setInstanceFollowRedirects(false);  
				conn.setConnectTimeout(5000);  
				String redirURL = conn.getHeaderField("Location");  
				System.out.println("-Search Page Index: " + searchPageIndex);
				System.out.println("-Result Page Index: " + resultPageIndex);
				System.out.println("-Title: " + title);
				System.out.println("-URL: " + redirURL);
				System.out.println();
				//�������ҳ��ʵ����
				ResultPage curResultPage = new ResultPage(searchPageIndex, resultPageIndex, title, redirURL);
				resultPageList.add(curResultPage); //��ӽ��ҳ���б�
				++resultPageIndex;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//��ȡ�ѹ�������������ҳ��ķ���
	private void crawlSogouSearchPage(String pageURL, int searchPageIndex)
	{
		try
		{
			//��ȡ��ҳ��HTML�ı�
			Document doc = Jsoup.connect(pageURL).timeout(10000).get();
			//��ȡ���ҳ��URL
			Elements nodes = doc.select("h3[class=vrTitle],h3[class=pt]"); 
			System.out.println("-Total Result Page: " + nodes.size());
			System.out.println();
			int resultPageIndex = 0;
			for(Element cnode:nodes) 
			{
				//��ȡ����
				String title = cnode.text();
				String infoBlock = cnode.toString(); 
				//ʹ��������ʽ��ȡ���ҳ��URL
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
				//�������ҳ��ʵ����
				ResultPage curResultPage = new ResultPage(searchPageIndex, resultPageIndex, title, resultURL);
				resultPageList.add(curResultPage); //��ӽ��ҳ���б�
				++resultPageIndex;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//��ȡ360������������ҳ��ķ���
	private void crawl360SearchPage(String pageURL, int searchPageIndex)
	{
		try
		{
			//��ȡ��ҳ��HTML�ı�
			Document doc = Jsoup.connect(pageURL).timeout(10000).get();
			//��ȡ���ҳ��URL
			Elements nodes = doc.select("h3[class=res-title]"); 
			System.out.println("-Total Result Page: " + nodes.size());
			System.out.println();
			int resultPageIndex = 0;
			for(Element cnode:nodes) 
			{
				//��ȡ����
				String title = cnode.text();
				String infoBlock = cnode.toString(); 
				//ʹ��������ʽ��ȡ���ҳ��URL
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
				//�������ҳ��ʵ����
				ResultPage curResultPage = new ResultPage(searchPageIndex, resultPageIndex, title, resultURL);
				resultPageList.add(curResultPage); //��ӽ��ҳ���б�
				++resultPageIndex;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//��ȡbing������������ҳ��ķ���
	private void crawlBingSearchPage(String pageURL, int searchPageIndex)
	{
		try
		{
			//��ȡ��ҳ��HTML�ı�
			Document doc = Jsoup.connect(pageURL).timeout(10000).get();
			//��ȡ���ҳ��URL
			Elements nodes = doc.select("li[class=b_algo]>h2,div[class=bm_ctn]>h2"); 
			System.out.println("-Total Result Page: " + nodes.size());
			System.out.println();
			int resultPageIndex = 0;
			for(Element cnode:nodes) 
			{
				//��ȡ����
				String title = cnode.text();
				String infoBlock = cnode.toString(); 
				//ʹ��������ʽ��ȡ���ҳ��URL
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
				//�������ҳ��ʵ����
				ResultPage curResultPage = new ResultPage(searchPageIndex, resultPageIndex, title, resultURL);
				resultPageList.add(curResultPage); //��ӽ��ҳ���б�
				++resultPageIndex;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//��ȡ���ҳ���б�ķ���
	public List<ResultPage> getResultPageList()
	{
		//�ж��������������Ƿ�Ϸ�
		if(validType)
			return resultPageList;
		else
		{
			System.out.println("-Invalid Search Engine Type-");
			return null;
		}
	}
	
	//����
	/*public static void main(String args[])
	{
		//new SearchPageCrawler("��������", "baidu", 0);
		//new SearchPageCrawler("��������", "sogou", 1);
		//new SearchPageCrawler("��������", "360", 0);
		//new SearchPageCrawler("��������", "bing", 0);
		SearchPage searchPage = new SearchPage("baidu");
		
		SearchPageCrawler searchCralwer2 = new SearchPageCrawler("��������", "baidu", 1);
		List<ResultPage> resultPageList2 = searchCralwer2.getResultPageList();
		searchPage.addResultPageList(resultPageList2);
		
		SearchPageCrawler searchCralwer3 = new SearchPageCrawler("��������", "baidu", 2);
		List<ResultPage> resultPageList3 = searchCralwer3.getResultPageList();
		searchPage.addResultPageList(resultPageList3);
		
		SearchPageCrawler searchCralwer1 = new SearchPageCrawler("��������", "baidu", 0);
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
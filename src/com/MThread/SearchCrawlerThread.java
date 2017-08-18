package com.MThread;

import java.util.List;

import com.Entity.ResultPage;
import com.Entity.SearchPage;
import com.Crawler.SearchPageCrawler;

//实现多线程爬取搜索页面的类

public class SearchCrawlerThread implements Runnable
{
	private String keyword = ""; //搜索关键词
	private String searchType = ""; //搜索引擎类型
	private int pageIndex = 0; //搜索页面索引
	
	private boolean finishFlag = false; //线程是否完成的标志变量
	
	private final int maxSleepTime = 2000;
	
	private List<ResultPage> resultPageList = null; //爬取的结果页面列表
	
	//构造方法
	public SearchCrawlerThread(String keyword, String searchType, int pageIndex)
	{
		this.keyword = keyword;
		this.searchType = searchType;
		this.pageIndex = pageIndex;
	}
	
	//获取标志变量的方法
	public boolean getFinishFlag()
	{
		return finishFlag;
	}
	
	//获取结果页面列表的方法
	public List<ResultPage> getResultPageList()
	{
		return resultPageList;
	}
	
	//实现Run方法
	public void run()
	{
		System.out.println("-Search Crawler Thread Start-");
		System.out.println(keyword + ", " + searchType + ", " + pageIndex);
		System.out.println();
		SearchPageCrawler searchPageCrawler = new SearchPageCrawler(keyword, searchType, pageIndex); //声明搜索页面爬取类
		resultPageList = searchPageCrawler.getResultPageList(); //获取爬取的结果页面列表
		finishFlag = true; //修改完成标志变量
		//线程随机休眠
		int sleepTime = (int)(Math.random()*maxSleepTime);
		try
		{
			System.out.println("-Sleep Time: " + sleepTime + "msec-");
			Thread.sleep(sleepTime);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//测试
	public static void main(String args[])
	{
		String keyword = "网络爬虫";
		String searchType = "baidu";
		
		int threadNum = 2;
		int pageNum = 4;
		
		SearchPage searchPage = new SearchPage("baidu");
		
		SearchCrawlerThread[] searchPageSet = new SearchCrawlerThread[pageNum]; //带爬取搜索页面集合
		//初始化搜索页面集合
		for(int i=0;i<pageNum;i++)
		{
			searchPageSet[i] = new SearchCrawlerThread(keyword, searchType, i);
		}
		int searchPageCount = 0;
		while(searchPageCount<pageNum)
		{
			int low = searchPageCount; //搜索页面集合下界索引
			int high = searchPageCount+threadNum; //搜索页面结合上界索引
			//按照设定的线程数启动线程
			for(int i=0;i<threadNum;i++)
			{
				new Thread(searchPageSet[searchPageCount]).start();
				++searchPageCount;
			}
			boolean finishFlag = false; //判断所有线程是否运行完的标志变量
			while(!finishFlag)
			{
				int finishCount = 0; //完成线程计数器
				for(int i=low;i<high;i++)
					if(searchPageSet[i].getFinishFlag())
						++finishCount;
				if(finishCount == threadNum)
					finishFlag = true;
			}
			//添加爬取的搜索页面列表
			for(int i=low;i<high;i++)
				searchPage.addResultPageList(searchPageSet[i].getResultPageList());
		}
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
	}
}
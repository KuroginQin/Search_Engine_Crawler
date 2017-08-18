package com.Main;

import com.Entity.ResultPage;
import com.Entity.SearchPage;
import com.MThread.ResultCrawlerThread;
import com.MThread.SearchCrawlerThread;
import com.Store.StoreResultPage;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

//搜索引擎爬虫总控程序

public class SearchEngineCrawler
{
	//构造方法
	//keyword 搜索关键词; searchPageNum 带爬取的搜索页面数; 
	//searchThreadNum 爬取搜索页面的线程数; resultThreadNum 爬取结果页面的线程数
	public SearchEngineCrawler(String keyword, int searchPageNum, int searchThreadNum, int resultThreadNum)
	{
		List<ResultPage> resultPageList = new ArrayList<ResultPage>(); //所有类型搜索引擎结果页面列表
		String searchType = ""; //当前搜索引擎类型
		//爬取百度搜素页面
		searchType = "baidu";
		List<ResultPage> resultPageListBaidu = crawlResultPageList(keyword, searchType, searchPageNum, searchThreadNum); //获取百度结果页面列表
		//爬取搜狗搜索页面
		searchType = "sogou";
		List<ResultPage> resultPageListSogou = crawlResultPageList(keyword, searchType, searchPageNum, searchThreadNum); //获取搜搜狗结果页面列表
		//爬取360搜索页面
		searchType = "360";
		List<ResultPage> resultPageList360 = crawlResultPageList(keyword, searchType, searchPageNum, searchThreadNum); //获取360结果页面列表
		//爬取bing搜索页面
		searchType = "bing";
		List<ResultPage> resultPageListBing = crawlResultPageList(keyword, searchType, searchPageNum, searchThreadNum); //获取bing结果页面列表
		//整合不同搜索引擎的结果列表
		//获取不同类搜索引擎结果页面列表长度
		int listLenBaidu = resultPageListBaidu.size();
		int listLenSogou = resultPageListSogou.size();
		int listLen360 = resultPageList360.size();
		int listLenBing = resultPageListBing.size();
		int maxLen = getMax(listLenBaidu, listLenSogou, listLen360, listLenBing);
		for(int i=0;i<maxLen;i++)
		{
			if(i<listLenBaidu)
				resultPageList.add(resultPageListBaidu.get(i));
			if(i<listLenSogou)
				resultPageList.add(resultPageListSogou.get(i));
			if(i<listLen360)
				resultPageList.add(resultPageList360.get(i));
			if(i<listLenBing)
				resultPageList.add(resultPageListBing.get(i));
		}
		
		//多线程爬取结果页面列表中的结果页面
		System.out.println("-Crawl Result Page-");
		System.out.println("-Thread Num: " + searchThreadNum);
		ExecutorService exec = Executors.newFixedThreadPool(resultThreadNum);  //爬取结果页面的线程池
		int totalLen = resultPageList.size(); //结果
		for(int i=0;i<totalLen;i++)
			exec.execute(new ResultCrawlerThread(resultPageList.get(i), i));
		exec.shutdown();
		//等待所有线程结束
		while(true)
			if(Thread.activeCount()==1) break;
		
		//获取去重后的结果页面列表
		List<ResultPage> finalResultPageList = ResultCrawlerThread.getFinalResultPageList();
		//将结果页面保存至本地
		int len = finalResultPageList.size();
		for(int i=0;i<len;i++)
		{
			ResultPage curResultPage = finalResultPageList.get(i);
			new StoreResultPage(curResultPage, i);
		}
		System.out.println("-Total Result Page: " + len);
	}
	
	//获取当前搜索引擎所有结果页面列表的方法
	//keyword 搜索关键词; searchType 搜索引擎累心
	//searchPageNum 待爬取的搜索页面总数; searchThreadNum 爬取搜索页面的线程数
	private List<ResultPage> crawlResultPageList(String keyword, String searchType, int searchPageNum, int searchThreadNum)
	{
		System.out.println("-Crawl Result Page List of " + searchType + "-");
		System.out.println("Thead Num: " +  searchPageNum);
		SearchPage searchPage = new SearchPage(searchType); //当前搜索引擎搜索页面实体
		SearchCrawlerThread[] searchPageSet = new SearchCrawlerThread[searchPageNum]; //带爬取搜索页面集合
		//初始化搜索页面集合
		for(int i=0;i<searchPageNum;i++)
		{
			searchPageSet[i] = new SearchCrawlerThread(keyword, searchType, i);
		}
		int searchPageCount = 0; //已爬取的搜索页面计数器
		while(searchPageCount<searchPageNum)
		{
			int low = searchPageCount; //搜索页面集合下界索引
			int high = searchPageCount+searchThreadNum; //搜索页面结合上界索引
			if(high>searchPageNum)
				high = searchPageNum;
			//按照设定的线程数启动线程
			for(int i=0;i<searchThreadNum;i++)
			{
				new Thread(searchPageSet[searchPageCount]).start();
				++searchPageCount;
				if(searchPageCount>=searchPageNum)
					break;
			}
			boolean finishFlag = false; //判断所有线程是否运行完的标志变量
			while(!finishFlag)
			{
				finishFlag = true;
				for(int i=low;i<high;i++)
					if(!searchPageSet[i].getFinishFlag())
						finishFlag = false;
			}
			//添加爬取的搜索页面列表
			for(int i=low;i<high;i++)
				searchPage.addResultPageList(searchPageSet[i].getResultPageList());
		}
		searchPage.sortResultPageList(); //将搜索页面列表按照索引号排序
		List<ResultPage> resultPageList = searchPage.getResultPageList(); //获取排序后的搜索页面列表
		return resultPageList;
	}
	
	//获取4个整数中最大值的方法
	private int getMax(int num1, int num2, int num3, int num4)
	{
		int curMaxA = num1;
		if(curMaxA<num2)
			curMaxA = num2;
		int curMaxB = num3;
		if(curMaxB>num4)
			curMaxB = num4;
		
		if(curMaxA>curMaxB)
			return curMaxA;
		else
			return curMaxB;
	}
	
	public static void main(String args[])
	{
		String keyword = "传销组织"; //搜索关键词
		int searchPageNum = 20; //爬取每种搜索引擎的搜索页面数
		int searchThreadNum = 10; //爬取搜索页面线程数
		int resultThreadNum = 50; //爬取结果页面线程数
		
		SearchEngineCrawler searchEngineCrawler = new SearchEngineCrawler(keyword, searchPageNum, searchThreadNum, resultThreadNum);
	}
}
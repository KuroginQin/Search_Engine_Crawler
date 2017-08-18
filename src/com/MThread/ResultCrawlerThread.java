package com.MThread;

import com.Entity.ResultPage;
import com.Crawler.ResultPageCrawler;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

//实现多线程爬取结果页面的类

public class ResultCrawlerThread implements Runnable
{
	private static Set<String> MD5ValueSet = new HashSet<String>(); //结果页面内容MD5值集合
	private static List<ResultPage> finalResultPageList = new ArrayList<ResultPage>(); //去重处理后的结果页面列表
	private static List<Integer> finalResultIndexList = new ArrayList<Integer>(); //去重处理后的结果页面索引列表
	
	private ResultPage resultPage; //当前爬取的结果页面
	private int pageIndex = 0; //结果页面列表中的索引
	
	//构造方法
	public ResultCrawlerThread(ResultPage resultPage, int pageIndex)
	{
		this.resultPage = resultPage;
		this.pageIndex = pageIndex;
	}
	
	//实现Run方法
	public void run()
	{
		ResultPageCrawler resultPageCrawler = new ResultPageCrawler(resultPage); //爬取当前结果页面
		resultPage = resultPageCrawler.getResultPage(); //获取包含正文内容的结果页面实体
		String content = resultPage.getContent().trim();
		String MD5Value = resultPageCrawler.getMD5Value(); //当前结果页面内容MD5值
		
		//根据结果页面内容MD5值去除重复网页
		synchronized (this) 
		{
            if(content.length()==0)
            	System.out.println("-Skip Result Page with No Content-");
            else if(!MD5ValueSet.contains(MD5Value))
            {
            	System.out.println("New MD5 Value: " + MD5Value);
            	MD5ValueSet.add(MD5Value); 
            	finalResultPageList.add(resultPage);
            	finalResultIndexList.add(pageIndex);
            }
            else
            	System.out.println("-Skip Duplicate Result Page-");
        }
	}
	
	//获取去重处理后的结果页面列表
	public static List<ResultPage> getFinalResultPageList()
	{
		//将结果页面列表按照索引排序
		int len = finalResultIndexList.size();
		for(int i=1;i<len;i++)
		{
			for(int j=0;j<i;j++)
			{
				int iIndex = finalResultIndexList.get(i);
				int jIndex = finalResultIndexList.get(j);
				if(j>i)
				{
					ResultPage iResultPage = finalResultPageList.get(i);
					ResultPage jResultPage = finalResultPageList.get(j);
					finalResultIndexList.set(i, jIndex);
					finalResultIndexList.set(j, iIndex);
					finalResultPageList.set(i, jResultPage);
					finalResultPageList.set(j, iResultPage);
				}
			}
		}
		return finalResultPageList;
	}
}
package com.MThread;

import java.util.List;

import com.Entity.ResultPage;
import com.Entity.SearchPage;
import com.Crawler.SearchPageCrawler;

//ʵ�ֶ��߳���ȡ����ҳ�����

public class SearchCrawlerThread implements Runnable
{
	private String keyword = ""; //�����ؼ���
	private String searchType = ""; //������������
	private int pageIndex = 0; //����ҳ������
	
	private boolean finishFlag = false; //�߳��Ƿ���ɵı�־����
	
	private final int maxSleepTime = 2000;
	
	private List<ResultPage> resultPageList = null; //��ȡ�Ľ��ҳ���б�
	
	//���췽��
	public SearchCrawlerThread(String keyword, String searchType, int pageIndex)
	{
		this.keyword = keyword;
		this.searchType = searchType;
		this.pageIndex = pageIndex;
	}
	
	//��ȡ��־�����ķ���
	public boolean getFinishFlag()
	{
		return finishFlag;
	}
	
	//��ȡ���ҳ���б�ķ���
	public List<ResultPage> getResultPageList()
	{
		return resultPageList;
	}
	
	//ʵ��Run����
	public void run()
	{
		System.out.println("-Search Crawler Thread Start-");
		System.out.println(keyword + ", " + searchType + ", " + pageIndex);
		System.out.println();
		SearchPageCrawler searchPageCrawler = new SearchPageCrawler(keyword, searchType, pageIndex); //��������ҳ����ȡ��
		resultPageList = searchPageCrawler.getResultPageList(); //��ȡ��ȡ�Ľ��ҳ���б�
		finishFlag = true; //�޸���ɱ�־����
		//�߳��������
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
	
	//����
	public static void main(String args[])
	{
		String keyword = "��������";
		String searchType = "baidu";
		
		int threadNum = 2;
		int pageNum = 4;
		
		SearchPage searchPage = new SearchPage("baidu");
		
		SearchCrawlerThread[] searchPageSet = new SearchCrawlerThread[pageNum]; //����ȡ����ҳ�漯��
		//��ʼ������ҳ�漯��
		for(int i=0;i<pageNum;i++)
		{
			searchPageSet[i] = new SearchCrawlerThread(keyword, searchType, i);
		}
		int searchPageCount = 0;
		while(searchPageCount<pageNum)
		{
			int low = searchPageCount; //����ҳ�漯���½�����
			int high = searchPageCount+threadNum; //����ҳ�����Ͻ�����
			//�����趨���߳��������߳�
			for(int i=0;i<threadNum;i++)
			{
				new Thread(searchPageSet[searchPageCount]).start();
				++searchPageCount;
			}
			boolean finishFlag = false; //�ж������߳��Ƿ�������ı�־����
			while(!finishFlag)
			{
				int finishCount = 0; //����̼߳�����
				for(int i=low;i<high;i++)
					if(searchPageSet[i].getFinishFlag())
						++finishCount;
				if(finishCount == threadNum)
					finishFlag = true;
			}
			//�����ȡ������ҳ���б�
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
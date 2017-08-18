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

//�������������ܿس���

public class SearchEngineCrawler
{
	//���췽��
	//keyword �����ؼ���; searchPageNum ����ȡ������ҳ����; 
	//searchThreadNum ��ȡ����ҳ����߳���; resultThreadNum ��ȡ���ҳ����߳���
	public SearchEngineCrawler(String keyword, int searchPageNum, int searchThreadNum, int resultThreadNum)
	{
		List<ResultPage> resultPageList = new ArrayList<ResultPage>(); //������������������ҳ���б�
		String searchType = ""; //��ǰ������������
		//��ȡ�ٶ�����ҳ��
		searchType = "baidu";
		List<ResultPage> resultPageListBaidu = crawlResultPageList(keyword, searchType, searchPageNum, searchThreadNum); //��ȡ�ٶȽ��ҳ���б�
		//��ȡ�ѹ�����ҳ��
		searchType = "sogou";
		List<ResultPage> resultPageListSogou = crawlResultPageList(keyword, searchType, searchPageNum, searchThreadNum); //��ȡ���ѹ����ҳ���б�
		//��ȡ360����ҳ��
		searchType = "360";
		List<ResultPage> resultPageList360 = crawlResultPageList(keyword, searchType, searchPageNum, searchThreadNum); //��ȡ360���ҳ���б�
		//��ȡbing����ҳ��
		searchType = "bing";
		List<ResultPage> resultPageListBing = crawlResultPageList(keyword, searchType, searchPageNum, searchThreadNum); //��ȡbing���ҳ���б�
		//���ϲ�ͬ��������Ľ���б�
		//��ȡ��ͬ������������ҳ���б���
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
		
		//���߳���ȡ���ҳ���б��еĽ��ҳ��
		System.out.println("-Crawl Result Page-");
		System.out.println("-Thread Num: " + searchThreadNum);
		ExecutorService exec = Executors.newFixedThreadPool(resultThreadNum);  //��ȡ���ҳ����̳߳�
		int totalLen = resultPageList.size(); //���
		for(int i=0;i<totalLen;i++)
			exec.execute(new ResultCrawlerThread(resultPageList.get(i), i));
		exec.shutdown();
		//�ȴ������߳̽���
		while(true)
			if(Thread.activeCount()==1) break;
		
		//��ȡȥ�غ�Ľ��ҳ���б�
		List<ResultPage> finalResultPageList = ResultCrawlerThread.getFinalResultPageList();
		//�����ҳ�汣��������
		int len = finalResultPageList.size();
		for(int i=0;i<len;i++)
		{
			ResultPage curResultPage = finalResultPageList.get(i);
			new StoreResultPage(curResultPage, i);
		}
		System.out.println("-Total Result Page: " + len);
	}
	
	//��ȡ��ǰ�����������н��ҳ���б�ķ���
	//keyword �����ؼ���; searchType ������������
	//searchPageNum ����ȡ������ҳ������; searchThreadNum ��ȡ����ҳ����߳���
	private List<ResultPage> crawlResultPageList(String keyword, String searchType, int searchPageNum, int searchThreadNum)
	{
		System.out.println("-Crawl Result Page List of " + searchType + "-");
		System.out.println("Thead Num: " +  searchPageNum);
		SearchPage searchPage = new SearchPage(searchType); //��ǰ������������ҳ��ʵ��
		SearchCrawlerThread[] searchPageSet = new SearchCrawlerThread[searchPageNum]; //����ȡ����ҳ�漯��
		//��ʼ������ҳ�漯��
		for(int i=0;i<searchPageNum;i++)
		{
			searchPageSet[i] = new SearchCrawlerThread(keyword, searchType, i);
		}
		int searchPageCount = 0; //����ȡ������ҳ�������
		while(searchPageCount<searchPageNum)
		{
			int low = searchPageCount; //����ҳ�漯���½�����
			int high = searchPageCount+searchThreadNum; //����ҳ�����Ͻ�����
			if(high>searchPageNum)
				high = searchPageNum;
			//�����趨���߳��������߳�
			for(int i=0;i<searchThreadNum;i++)
			{
				new Thread(searchPageSet[searchPageCount]).start();
				++searchPageCount;
				if(searchPageCount>=searchPageNum)
					break;
			}
			boolean finishFlag = false; //�ж������߳��Ƿ�������ı�־����
			while(!finishFlag)
			{
				finishFlag = true;
				for(int i=low;i<high;i++)
					if(!searchPageSet[i].getFinishFlag())
						finishFlag = false;
			}
			//�����ȡ������ҳ���б�
			for(int i=low;i<high;i++)
				searchPage.addResultPageList(searchPageSet[i].getResultPageList());
		}
		searchPage.sortResultPageList(); //������ҳ���б�������������
		List<ResultPage> resultPageList = searchPage.getResultPageList(); //��ȡ����������ҳ���б�
		return resultPageList;
	}
	
	//��ȡ4�����������ֵ�ķ���
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
		String keyword = "������֯"; //�����ؼ���
		int searchPageNum = 20; //��ȡÿ���������������ҳ����
		int searchThreadNum = 10; //��ȡ����ҳ���߳���
		int resultThreadNum = 50; //��ȡ���ҳ���߳���
		
		SearchEngineCrawler searchEngineCrawler = new SearchEngineCrawler(keyword, searchPageNum, searchThreadNum, resultThreadNum);
	}
}
package com.MThread;

import com.Entity.ResultPage;
import com.Crawler.ResultPageCrawler;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

//ʵ�ֶ��߳���ȡ���ҳ�����

public class ResultCrawlerThread implements Runnable
{
	private static Set<String> MD5ValueSet = new HashSet<String>(); //���ҳ������MD5ֵ����
	private static List<ResultPage> finalResultPageList = new ArrayList<ResultPage>(); //ȥ�ش����Ľ��ҳ���б�
	private static List<Integer> finalResultIndexList = new ArrayList<Integer>(); //ȥ�ش����Ľ��ҳ�������б�
	
	private ResultPage resultPage; //��ǰ��ȡ�Ľ��ҳ��
	private int pageIndex = 0; //���ҳ���б��е�����
	
	//���췽��
	public ResultCrawlerThread(ResultPage resultPage, int pageIndex)
	{
		this.resultPage = resultPage;
		this.pageIndex = pageIndex;
	}
	
	//ʵ��Run����
	public void run()
	{
		ResultPageCrawler resultPageCrawler = new ResultPageCrawler(resultPage); //��ȡ��ǰ���ҳ��
		resultPage = resultPageCrawler.getResultPage(); //��ȡ�����������ݵĽ��ҳ��ʵ��
		String content = resultPage.getContent().trim();
		String MD5Value = resultPageCrawler.getMD5Value(); //��ǰ���ҳ������MD5ֵ
		
		//���ݽ��ҳ������MD5ֵȥ���ظ���ҳ
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
	
	//��ȡȥ�ش����Ľ��ҳ���б�
	public static List<ResultPage> getFinalResultPageList()
	{
		//�����ҳ���б�����������
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
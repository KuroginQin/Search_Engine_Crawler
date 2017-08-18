package com.Entity;

import java.util.List;
import java.util.ArrayList;

//����ҳ��ʵ����
public class SearchPage
{
	private String searchType = ""; //������������,�涨searchType��ֵֻ��Ϊ{baidu, sogou, 360, bing}�е�1��,����Ϊ���Ϸ�����
	private boolean validType = false; //�ж��������������Ƿ�Ϸ��ı�־����
	private List<ResultPage> resultPageList = new ArrayList<ResultPage>(); //���ҳ���б�
	
	//���췽��
	public SearchPage(String searchType)
	{
		//�ж��������������Ƿ�Ϸ�
		if(searchType.equals("baidu") || searchType.equals("sogou") || searchType.equals("360") || searchType.equals("bing"))
		{
			this.searchType = searchType;
			validType = true;
		}
		else
			System.out.println("-Invalid Search Engine Type-");
	}
	
	//��ӽ��ҳ��ķ���
	public void addResultPage(ResultPage resultPage)
	{
		//�ж��������������Ƿ�Ϸ�
		if(validType)
			resultPageList.add(resultPage);
		else
			System.out.println("-Invalid Search Engine Type-");
	}
	
	//��ӽ��ҳ���б�ķ���
	public void addResultPageList(List<ResultPage> curResutlPageList)
	{
		//�ж��������������Ƿ�Ϸ�
		if(validType)
		{
			int len = curResutlPageList.size();
			for(int i=0;i<len;i++)
				resultPageList.add(curResutlPageList.get(i));
		}
		else
			System.out.println("-Invalid Search Engine Type-");
	}
	
	//��ȡ�����������͵ķ���
	public String getSearchType()
	{
		if(validType)
			return searchType;
		else
		{
			System.out.println("-Invalid Search Engine Type-");
			return null;
		}
	}
	
	//������ҳ���б�ķ���
	public void sortResultPageList()
	{
			System.out.println("-Sort Result Page List-");
		int len = resultPageList.size();
		for(int i=1;i<len;i++)
		{
			for(int j=0;j<i;j++)
			{
				ResultPage iPage = resultPageList.get(i);
				ResultPage jPage = resultPageList.get(j);
				int iSearchIndex = iPage.getSearchPageIndex();
				int jSearchIndex = jPage.getSearchPageIndex();
				if(iSearchIndex<jSearchIndex)
				{
					resultPageList.set(i, jPage);
					resultPageList.set(j, iPage);
				}
				else if(iSearchIndex==jSearchIndex)
				{
					int iResultIndex = iPage.getResultIndex();
					int jResultIndex = jPage.getResultIndex();
					if(iResultIndex<jResultIndex)
					{
						resultPageList.set(i, jPage);
						resultPageList.set(j, iPage);
					}
				}
			}
		}
	}
	
	//��ȡ���ҳ���б�ķ���
	public List<ResultPage> getResultPageList()
	{
		if(validType)
			return resultPageList;
		else
		{
			System.out.println("-Invalid Search Engine Type-");
			return null;
		}
	}
}
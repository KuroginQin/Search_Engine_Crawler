package com.Entity;

import java.util.List;
import java.util.ArrayList;

//搜索页面实体类
public class SearchPage
{
	private String searchType = ""; //搜索引擎类型,规定searchType的值只能为{baidu, sogou, 360, bing}中的1个,否则为不合法类型
	private boolean validType = false; //判断搜索引擎类型是否合法的标志变量
	private List<ResultPage> resultPageList = new ArrayList<ResultPage>(); //结果页面列表
	
	//构造方法
	public SearchPage(String searchType)
	{
		//判断搜索引擎类型是否合法
		if(searchType.equals("baidu") || searchType.equals("sogou") || searchType.equals("360") || searchType.equals("bing"))
		{
			this.searchType = searchType;
			validType = true;
		}
		else
			System.out.println("-Invalid Search Engine Type-");
	}
	
	//添加结果页面的方法
	public void addResultPage(ResultPage resultPage)
	{
		//判断搜索引擎类型是否合法
		if(validType)
			resultPageList.add(resultPage);
		else
			System.out.println("-Invalid Search Engine Type-");
	}
	
	//添加结果页面列表的方法
	public void addResultPageList(List<ResultPage> curResutlPageList)
	{
		//判断搜索引擎类型是否合法
		if(validType)
		{
			int len = curResutlPageList.size();
			for(int i=0;i<len;i++)
				resultPageList.add(curResutlPageList.get(i));
		}
		else
			System.out.println("-Invalid Search Engine Type-");
	}
	
	//获取搜索引擎类型的方法
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
	
	//排序结果页面列表的方法
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
	
	//获取结果页面列表的方法
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
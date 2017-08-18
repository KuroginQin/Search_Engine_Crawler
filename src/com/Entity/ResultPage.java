package com.Entity;

//结果页面实体类
public class ResultPage
{
	private int searchPageIndex = -1; //搜索页面索引
	private int resultIndex = -1; //在当前搜索页面的查询结果索引
	private String title= ""; //搜索页面显示的标题
	private String URL = ""; //结果页面URL
	private String content = ""; //结果页面内容
	
	//构造方法
	public ResultPage(int searchPageIndex, int resultIndex, String title, String URL)
	{
		this.searchPageIndex = searchPageIndex;
		this.resultIndex = resultIndex;
		this.title = title;
		this.URL = URL;
	}
	
	//添加内容的方法
	public void addContent(String content)
	{
		this.content += content;
	}
	
	//获取搜索页面索引的方法
	public int getSearchPageIndex()
	{
		return searchPageIndex;
	}
	
	//获取查询结果索引的方法
	public int getResultIndex()
	{
		return resultIndex;
	}
	
	//获取标题的方法
	public String getTitle()
	{
		return title;
	}
	
	//获取URL的方法
	public String getURL()
	{
		return URL;
	}
	
	//获取内容的方法
	public String getContent()
	{
		return content;
	}
}
package com.Entity;

//���ҳ��ʵ����
public class ResultPage
{
	private int searchPageIndex = -1; //����ҳ������
	private int resultIndex = -1; //�ڵ�ǰ����ҳ��Ĳ�ѯ�������
	private String title= ""; //����ҳ����ʾ�ı���
	private String URL = ""; //���ҳ��URL
	private String content = ""; //���ҳ������
	
	//���췽��
	public ResultPage(int searchPageIndex, int resultIndex, String title, String URL)
	{
		this.searchPageIndex = searchPageIndex;
		this.resultIndex = resultIndex;
		this.title = title;
		this.URL = URL;
	}
	
	//������ݵķ���
	public void addContent(String content)
	{
		this.content += content;
	}
	
	//��ȡ����ҳ�������ķ���
	public int getSearchPageIndex()
	{
		return searchPageIndex;
	}
	
	//��ȡ��ѯ��������ķ���
	public int getResultIndex()
	{
		return resultIndex;
	}
	
	//��ȡ����ķ���
	public String getTitle()
	{
		return title;
	}
	
	//��ȡURL�ķ���
	public String getURL()
	{
		return URL;
	}
	
	//��ȡ���ݵķ���
	public String getContent()
	{
		return content;
	}
}
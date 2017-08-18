package com.Crawler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.Entity.ResultPage;
import com.Store.StoreResultPage;

//��ȡ���ҳ�����

public class ResultPageCrawler
{
	private ResultPage resultPage; //��ǰ���ҳ��ʵ����
	private String MD5Value = ""; //��ҳ���ݶ�Ӧ��MD5ֵ
	
	private double ratioThreshold = 0.3; //����-���ֱ���ֵ (0.25)
	
	//���췽��
	public ResultPageCrawler(ResultPage resultPage)
	{
		System.out.println("-Crawl Result Page-");
		this.resultPage = resultPage;
		String URL = resultPage.getURL();
		String HTML = "";
		try
		{
			//��ȡ���ҳ���HTML�ı�
			Document doc = Jsoup.connect(URL).timeout(50000).get();
			//������ҳ���ݶ�Ӧ��MD5ֵ
			MD5Value = calMD5Str(doc);
			HTML = doc.html();
			
			//System.out.println(HTML);
		}
		catch(Exception e)
		{
			System.out.println("-Exception URL: " + URL);
			e.printStackTrace();
		}
		//��ȡ���ҳ������
		String content = getPageContent(HTML);
		//�����������
		this.resultPage.addContent(content);
		System.out.println("-Title: " + resultPage.getTitle());
		System.out.println("-URL: " + resultPage.getURL());
		System.out.println("-Content: " + resultPage.getContent());
		System.out.println();
	}
	
	//��ȡ��ǰҳ��ʵ����ķ���
	public ResultPage getResultPage()
	{
		return resultPage;
	}
	
	//��ȡ��ҳ����MD5ֵ�ķ���
	public String getMD5Value()
	{
		return MD5Value;
	}
	
	//������ҳMD5ֵ�ķ���
	private String calMD5Str(Document pageDoc)
	{
		String content = pageDoc.text(); //��ȡ��ҳ����
		content = content.replaceAll("\\s*|\t|\r|\n", ""); //ȥ�������еĿո��Ʊ����
		//���������ı���Ӧ��MD5ֵ
		MessageDigest md = null;
		byte[] MD5Message = null;
		try
		{
			md = MessageDigest.getInstance("MD5");
			MD5Message = md.digest(content.getBytes()); 
		}
		catch(NoSuchAlgorithmException e)
		{
			System.out.println("-No Such Algorithm-");
		}
		//�������MD5�ַ���
		String MD5Str = "";
		for(int i=0;i<MD5Message.length;i++)
			MD5Str += MD5Message[i];
		return MD5Str;
	}
	
	//��ȡ��ҳ���Ľڵ�ķ���
	//������ȱ���DOM������ɾ��"����"�ڵ�,��ȡ���ݽڵ�
	private Element getContentNode(Element node)
	{
		//�����ǳ����ӱ�ǩ
	    if (node.tagName() == "a") 
	           return node;
	     //������ǰ�ڵ���ӽڵ�
	     Elements childNodes = node.children();
	     for(Element curNode:childNodes)
	     {
	    	 //�����ǰ�ڵ�û������,��ɾ����ǰ�ڵ�
	    	 if(!curNode.hasText())
	    		 curNode.remove();
	    	 else //������㵱ǰ�ڵ������-���ֱ�
	    	 {
	    		 int linkCount = 0; //�ڵ��ڵ�������
	    		 double wordCount = 1 + ratioThreshold; //�ڵ��ڵ����ֳ���(�����������-���ֱ�ʱ���ֳ����쳣,��Ĭ��ֵ����Ϊ����0)
	    		 int puncCount = 0; //�ڵ��ڱ�������
	    		 double linkWordRatio = 0; //��ǰ�ڵ��Ϊ����-���ֱ�
	    		 
	    		 //���㵱ǰ�ڵ��������
	    		 Elements linkNodes = curNode.select("a[href]");
	    		 linkCount = linkNodes.size();
	    		 //���㵱ǰ�ڵ�����ֳ���
	    		 String curContent = curNode.text();
	    		 if(curContent.length()>0) 
	    			 wordCount = curContent.length();
	    		 //���㵱ǰ�ڵ������-���ֱ�
	    		 linkWordRatio = linkCount/wordCount;
	    		 //���㵱ǰ�ڵ�ı�������
	    		 String[] puncSet = { ",", ";", ".", "\"", "'", "\\?", "��", ":", "��" };
	    		 for(int i=0;i<puncSet.length;i++)
	    			 puncCount += curNode.text().split(puncSet[i]).length-1;
	    		 
	    		 //�����ǰ�ڵ�û�б�ǩ����,��ɾ����ǰ�ڵ�
	    		 if(puncCount<1)
	    			 curNode.remove();
	    		 else if(linkWordRatio>ratioThreshold) //�����ǰ�ڵ������-���ӱȳ�����ֵ,��ɾ����ǰ�ڵ�
	    			 curNode.remove();
	    		 else //����ݹ��������,���쵱ǰ�ڵ�ĺ��ӽڵ�
	    			 getContentNode(curNode);
	    	 }
	    }
	     return node;
	}
		
	//��ȡ�������ݵķ���
	private String getPageContent(String HTML)
	{	
		//ɾ�������HTML��ǩ
		HTML = HTML.replaceAll("(?is)<!DOCTYPE.*?>", "");
		HTML = HTML.replaceAll("(?is)<!--.*?-->", "");
		HTML = HTML.replaceAll("(?is)<script.*?>.*?</script>", "");
		HTML = HTML.replaceAll("(?is)<style.*?>.*?</style>", "");
		Document contentDoc = Jsoup.parse(HTML);
		//����<body>�ڵ�
		Element bodyNode = contentDoc.select("body").first();
		//��ȡ���ݽڵ�
		Element contentNode = getContentNode(bodyNode); 
		String content = contentNode.text();
		content = content.replaceAll("<.*?>|[{].*?[}]|\t", ""); //ȥ�������HTML, JS����
		content = content.replaceAll("\\s{2,}", " "); //��2�����Ͽո��滻Ϊ1��
		return content;
	}
	
	//����
	/*public static void main(String args[])
	{
		//ResultPage page1 = new ResultPage(3, 7, "����֩��(��������)webspider������-CSDN����", "http://download.csdn.net/detail/tjx2006/2621909");
		//ResultPage page2 = new ResultPage(3, 8, "AppleBot:ƻ�����Լ����������������_36�", "http://36kr.com/p/532608.html");
		//ResultPage page3 = new ResultPage(3, 9, "�����������|����������� v1.0��ɫ��_��ɫ����վ", "http://www.greenxiazai.com/soft/11911.html");
		//page1 = new ResultPageCrawler(page1).getResultPage();
		//page2 = new ResultPageCrawler(page2).getResultPage();
		//page3 = new ResultPageCrawler(page3).getResultPage();
		//new StoreResultPage(page1, 0);
		//new StoreResultPage(page2, 1);
		//new StoreResultPage(page3, 2);
		
		ResultPage page = new ResultPage(0, 0, "������ѧ", "https://baike.so.com/doc/845213-893738.html");
		page = new ResultPageCrawler(page).getResultPage();
	}*/
}
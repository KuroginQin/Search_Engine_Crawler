package com.Store;

import java.io.File;
import java.io.FileWriter;

import com.Entity.ResultPage;

//�����ҳ�汣�������ص���

public class StoreResultPage
{
	//���췽��
	public StoreResultPage(ResultPage resultPage, int pageIndex)
	{
		FileWriter fileWriter=null;
		try
		{
			//ȷ�����������ļ���·������
			String dirPath = ".//data//"; //�������������ļ��е����·��
			File file =new File(dirPath); 
			if (!file.exists()&&!file.isDirectory()) //����ļ��в������򴴽�
				file.mkdir(); 
			fileWriter= new FileWriter(".//data//" + pageIndex + ".txt", true);
			String title = resultPage.getTitle();
			String URL = resultPage.getURL();
			String content = resultPage.getContent();
			System.out.println("-Store Result Page-");
			System.out.println(title + ", " + URL + "," + content);
			String storeInfo = "@Title: " + title + "\r\n" 
					+ "@URL: " + URL + "\r\n"
					+ "@Content: " + content + "\r\n";
			fileWriter.write(storeInfo);
			fileWriter.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				fileWriter.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
}
package com.Store;

import java.io.File;
import java.io.FileWriter;

import com.Entity.ResultPage;

//将结果页面保存至本地的类

public class StoreResultPage
{
	//构造方法
	public StoreResultPage(ResultPage resultPage, int pageIndex)
	{
		FileWriter fileWriter=null;
		try
		{
			//确保保存数据文件的路径可用
			String dirPath = ".//data//"; //保存贴吧数据文件夹的相对路径
			File file =new File(dirPath); 
			if (!file.exists()&&!file.isDirectory()) //如果文件夹不存在则创建
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
0. DESCRIPTION 
This is the project of a simple search engine crawler, which can crawl and save the search results from different search engines corresponding to a key word. The result of the crawler includes the titles, the URLs and the main content of the results.

Features of the crawler can be described as follows: 
1) Integrating the search results from 4 different search engines including Baidu, Sogou, 360 and Bing;
2) Extracting the main content of result pages with different styles and structures by using a strategy based on text density (the link-word ratio);
3) Eliminating the duplicate pages from different search engines by calculating the corresponding MD5 value of the pages’ content;
4) Solving the problem of URL redirection occurred in some search engines;
5) Using multi-threading technique, and keeping the relative positions of result pages, namely the search result with higher rank in a search engine would be assigned smaller index (in general, the result with higher rank may have more relevant content).

Because of the author's limited capacity of programming, it's hard to avoid some errors and deficiencies in the project. If you find some errors or anything that can be improved, you can contact me via [mengqin_az@foxmail.com]. Thank You Very Much!

0. 简述
简单的搜索引擎爬虫,能够爬取并保存不同搜索引擎对于某关键词的搜索结果,包括搜索结果的标题、URL和页面主要内容。

该爬虫的特性概括如下：
1) 整合4种(百度、搜狗、360、必应)不同搜索引擎的返回结果;
2) 使用基于文本密度(连接-文字比)的异构网页正文提取策略;
3) 通过计算页面内容对应MD5值去除不同搜索引擎返回的相同页面;
4) 解决搜索结果URL重定向问题;
5) 多线程实现,并保留搜索结果的相对位置,即越靠前的搜索结果能获得相对较小的索引号(一般情况下,搜索结果越靠前,对应的内容越相关)。

由于作者水平有限,难免有疏漏之处,还望大家批评指正!如有关于源代码、数据集的任何问题,可通过 [mengqin_az@foxmail.com] 邮件联系。谢谢！


1. HOW TO USE
1) Import the project file “SearchEngineCrawler”;
2) Reload the jar in “lib”;
3) Set the parameter (keyword, searchPageNum, searchThreadNum, resultThreadNum) in “src\com\Main\SearchEngineCrawler.java”;
4) Run the program of “src\com\SearchEngineCrawle.java”, and the result will be saved in “data”.

1. 使用方法
1) 导入项目文件;
2) 重新加载lib文件夹下的jar包;
3) 设定src\com\Main\SearchEngineCrawler.java 中的参数(keyword, searchPageNum, searchThreadNum, resultThreadNum);
4) 运行src\com\Main\SearchEngineCrawler.java,结果保存在data文件夹中.


2. 定义
搜索引擎的一般过程可描述为：用户输入待查询的关键词，搜索引擎返回一个显示查询候选结果列表的页面，用户可进一步选择该列表中的某一个页面查看具体的查询结果。
	1)根据上述过程，可将搜索引擎返回的显示候选结果列表的页面定义为“搜索页面”(Search Page)；
	2)将“搜索页面”中每一个候选结果指向的具体页面定义为“结果页面”(Result Page)，即“搜索页面”实际上是“结果结果”页面的列表。


3. 搜索页面URL格式

1) 百度搜索引擎URL格式 
百度搜索引擎搜索页面URL格式为 http://www.baidu.com/s?wd=[关键词]&pn=[当前页面之前的结果页面总数,与搜索页面的页数有关]；
其中，每个搜索页面能够显示10个查询结果，分别对应10个结果页面，而搜索页面使用参数pn表示显示的查询结果数，以此确定当前搜索页面的页数，即pn=(i-1)*10表示搜索页面第i页(当pn=0时可省略该参数)，如pn=0表示搜索页面第1页，而pn=10表示搜索页面第2页，以此类推。
对于关键词“网络爬虫”，搜索页面的URL示例为：
	http://www.baidu.com/s?wd=网络爬虫 表示关键词“网络爬虫”搜索页面第1页；
	http://www.baidu.com/s?wd=网络爬虫&pn=10 表示关键词“网络爬虫”搜索页面第2页。
注意: 百度搜索页面显示的结果页面URL为“外部链接”,需要进一步重定向才能获得结果页面对应的真实URL。

2) 搜狗搜索引擎URL格式 (结果页面URL重定向) 
搜狗搜索引擎的URL格式为 http://www.sogou.com/web?query=[关键词]&page=[搜索页面页数]；
其中，每个搜索页面能够显示10个查询结果(除查询结果外搜索页面可能参杂广告，爬取时不考虑广告)，表示搜页面页数的参数page从1开始，即page=1表示搜索页面第1页(当page=1时参数可省略)，而page=2表示搜索页面第2页，以此类推。
对于关键词“网络爬虫”，搜索页面的URL示例为： 
	http://www.sogou.com/web?query=网络爬虫 表示关键词“网络爬虫”搜索页面第1页；
	http://www.sogou.com/web?query=网络爬虫&page=2 表示关键词“网络爬虫”搜索页面第2页。

3) 360搜索引擎URL格式 
360搜索引擎搜索页面URL的格式为 http://www.so.com/s?q=[关键词]&pn=[搜索页面页数]，其中，每个搜索页面能够显示10个查询结果(除查询结果外搜索页面可能参杂广告，爬取时不考虑广告)，表示搜索页面数的参数pn从1开始，即pn=1表示搜索页面第1页(pn=1时参数可省略)，而pn=2表示搜索页面第2页，以此类推。
对于关键词“网络爬虫”，搜索页面的URL示例为： 
	http://www.so.com/s?q=网络爬虫 表示关键词“网络爬虫”搜索页面第1页； 
	http://www.so.com/s?p=网络爬虫&pn=2 表示关键词“网络爬虫”搜索页面第2页。
注意: 生成搜索页面URL时，需要将关键词参数q的值转化为UTF-8编码，如将字符串“网络爬虫”转换为字符串“%E7%BD%91%E7%BB%9C%E7%88%AC%E8%99%AB”。
注意: 360搜索页面显示的结果页面URL为“外部链接”,实际上可直接提取<h3 class="res-title" … >…</h3> 中的"data-url"参数,接口获得重定向后的URL。

4) Bing搜索引擎URL格式 
bing搜索引擎搜索页面URL格式为 http://cn.bing.com/search?q=[关键词]&first=[当前搜索页面第一个查询结果的序列号，与搜索页面页数有关]；
其中，每个搜索页面能够显示10个查询结果(除查询结果外搜索页面可能参杂广告，爬取时不考虑广告)，搜索页面页数与参数first有关，first=(i-1)*10+1表示搜索页面第i页(当first=1时参数可省略)，如first=1表示搜索页面第1页，而first=11表示搜索页面第2页，以此类推。
对于关键词“网络爬虫”，搜索页面的URL示例为： 
	http://cn.bing.com/search?q=网络爬虫 表示关键词“网络爬虫”搜索页面第1页； 
	http://cn.bing.com/search?q=网络爬虫&first=11 表示关键词“网络爬虫”搜索页面第2页。
注意: 在生成搜索页面URL时，需要将关键词参数q的值转化为UTF-8编码，如将字符串“网络爬虫”转换为字符串“%E7%BD%91%E7%BB%9C%E7%88%AC%E8%99%AB”。


4. 异构网页正文提取
搜索引擎返回的搜索结果为不同样式和结构的网页,从不同的结果页面中提取主要内容属于典型的异构网页正文提取问题,而不能使用某种特定的CSS选择规则提取所有结果的正文部分。
本爬虫使用了一种基于连接-文字比的正文提取策略,该策略的基本流程如下所示:
	S1: 获取页面对应的DOM树
	S2: 去除JS, CSS 和HTML注释等冗余标签
	S3: 提取<body>…</body>部分的DOM子树
	S4: 广度优先遍历预处理后的DOM树
		4.1: 计算当前节点的文字-链接比(linkWordRatio)
		4.2: 如果当前节点的文字-链接比大于阈值(ratioThreshold)
			4.2.1: 删除当前节点
		4.3: 否则
			4.3.1: 递归调用当前节点的孩子节点


5. 网页去重
不同搜索引擎可能返回相同的结果页面,因此在结果中需要去除这些重复的页面。
本爬虫采取的网页去重策略为计算页面内容对应的MD5值,再与以爬取页面的MD5值对比,检查当前页面是否重复。
由于MD5值对于不定长的文本内容均适用,且只有完全相同的文本MD5值才完全相同,因此可以通过额外维护一个MD5值集合,达到检查判断重复网页的目的。


6. 项目文件说明
Search_Engine_Crawler\lib 项目所需的jar包
Search_Engine_Crawler\data 保存爬取结果的数据文件
Search_Engine_Crawer\src 项目源代码


7. 程序结构说明
com\Entity 页面实体相关程序 
	SearchPage.java 搜索页面实体类 
	ResultPage.java 结果页面实体类 
com\Crawler 页面爬虫相关程序 
	SearchPageCrawler.java 爬取搜索页面，获取结果页面列表的程序 
	ResultPageCrawler.java 爬取结果页面，提取页面正文内容的程序 
com\MThread 多线程实现相关程序 
	SearchCrawlerThread.java 搜索页面爬取多线程实现程序 
	ResultCrawlerThread.java 结果页面爬取多线程实现程序
com\Store 保存爬取结果相关程序 
	StoreResultPage.java 将爬取结果页面正文内容保存至本地的程序
com\Main 爬虫总控相关程序 
	SearchEngineCrawler.java 搜索引擎爬虫总控程序

	
8. 结果数据文件说明
爬取结果的数据文件保存在data文件夹中,每个结果页面保存在一个txt文本文件中,文件名格式为[finalPageIndex].txt,其中finalPageIndex 为结果页面在去重后的列表中的索引号,该索引号越小说明结果页面的内容越相关。
每个数据文件格式如下所示:
@Title: [搜索引擎搜索页面显示的标题]
@URL: [重定向后的真实URL]
@Content: [提取的正文内容]

##对于关键词“传销组织”的爬取结果示例在Result_Example.rar中

##注意: 对于本爬虫,存在一些不能提取正文或提取后正文为空的结果页面,而爬虫在将结果保存至本地时会自动忽略这些结果页面。
本爬虫程序不能爬取的结果页面,主要可分为以下几类:
1)内容涉及动态加载的页面
如 https://baike.so.com/doc/845213-893738.html 

2)网页整体以图片和导航文字为主,内容较分散(如一些组织机构官网)
如	http://www.gsm.pku.edu.cn/ 
	http://eecs.pku.edu.cn/

3)网页服务器异常,连接超时或出现错误提示
如	http://gkcx.eol.cn/schoolhtm/schoolTemple/school31.htm 
	http://www.pkuschool.edu.cn/

4)需要事先登录才能浏览内容的网页(如微博)
如	http://weibo.com/n/%E5%8C%97%E4%BA%AC%E5%A4%A7%E5%AD%A6?c=spr_qdhz_bd_baidusmt_weibo_s  

因此,本爬程序比较善于爬取文本内容相对集中的新闻网站,而不适合爬取以图片、视频、短文本为主的结果页面;并且,本爬虫只能爬取并保存结构页面的正文文本。
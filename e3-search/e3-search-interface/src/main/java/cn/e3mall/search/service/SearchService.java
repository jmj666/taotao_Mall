package cn.e3mall.search.service;

import cn.e3mall.common.pojo.SearchResult;

public interface SearchService {
	//当前页的记录数
	SearchResult search(String keyword,int page,int rows)throws Exception;
}

package cn.e3mall.search.service;

import cn.e3mall.common.pojo.E3Result;

public interface SearchItemService {
	E3Result importItems();//查询所有商品数据，导入solr索引库
}

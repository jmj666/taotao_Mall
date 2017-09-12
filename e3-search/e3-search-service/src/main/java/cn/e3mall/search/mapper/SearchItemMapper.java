package cn.e3mall.search.mapper;

import java.util.List;

import cn.e3mall.common.pojo.SearchItem;

public interface SearchItemMapper {
	List<SearchItem> getItemList();//查询商品列表
	SearchItem getItemById(long itemId);//根据商品ID查询数据库
}

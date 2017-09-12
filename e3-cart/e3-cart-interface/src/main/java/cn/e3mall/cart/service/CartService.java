package cn.e3mall.cart.service;

import java.util.List;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.pojo.TbItem;

public interface CartService {
	E3Result addCart(long userId,long itemId,int num);
	E3Result mergeCart(long userId, List<TbItem> itemList);
	List<TbItem> getList(long userId);
	E3Result updateCartItemNum(long userId, long itemId, int num);
	E3Result deleteCartItem(long userId, long itemId);
	E3Result clearCartItem(long userId);
}

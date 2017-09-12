package cn.e3mall.sso.service;

//校验用户注册信息是否已经在数据库中存在
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.pojo.TbUser;

public interface UserService {
	E3Result checkData(String param,int type);
	E3Result createUser(TbUser user);
	E3Result userLogin(String username,String password);
	E3Result getUserByToken(String token);
}

package com.api.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import Yplat.annotation.CacheData;
import Yplat.cache.mysql.BaseJdbcDao;
import Yplat.cache.mysql.bean.Page;

@Component
public class GameDao extends BaseJdbcDao<GameInfo>{

	@CacheData(name="gameInfo")
	public List<JSONObject> queryGameList(){
		List<Object> para = new ArrayList<>();
		para.add("%s%");
		String sql = "select * from game where gameName like ?";
		JSONObject paraObj = new JSONObject();
		//paraObj.put("gameID", "4");
		//paraObj.put("gameName", "好的1");
		paraObj.put("gameMan", "我自己");
		/*this.insert("game", paraObj);*/
		/*List<GameInfo> list = this.queryTList(sql, para);
		System.out.println(list);*/
		
		List<GameInfo> list = this.queryTByPara("select * from game", paraObj, "gameId desc");
		System.out.println(list);
		
		Page<GameInfo> page = new Page<>();
		page.setPageSize(1);
		page = this.queryJsonObjectByPage("select * from game", null, page, "gameId desc", "game");
		System.out.println(page);
		
		JSONObject condtObj = new JSONObject();
		condtObj.put("gameID", "3");
		this.update("game", paraObj, condtObj);
		return this.queryForJsonList(sql, para);
	}
	
}

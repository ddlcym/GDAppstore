package com.changhong.gdappstore.model;

import java.util.ArrayList;

public class RankingData {
	private static RankingData rankingData;
	
	private String strHost;
	private ArrayList<Ranking_Item> popularArrayList = null;
//	private ArrayList<Ranking_Item> newArrayList = null;
//	private ArrayList<Ranking_Item> hotArrayList = null;
//	private ArrayList<Ranking_Item> surgeArrayList = null;
	
	protected RankingData() {
		// TODO Auto-generated constructor stub
	}

	public static RankingData getInstance() {
		if(rankingData == null) {
			rankingData = new RankingData();
		}
		return rankingData;
	}

	public ArrayList<Ranking_Item> getPopularArrayList() {
		return popularArrayList;
	}

	public void setPopularArrayList(ArrayList<Ranking_Item> popularArrayList) {
		this.popularArrayList = popularArrayList;
	}

	public void setHost(String host) {
		strHost = host;
	}

	public String getHost() {
		return strHost;
	}
}

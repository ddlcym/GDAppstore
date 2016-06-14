package com.changhong.gdappstore.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 栏目数据类型
 * 
 * @author wangxiufeng
 * 
 */
public class Category{
	// 栏目id
	protected int id;
	// 栏目父栏目id
	protected int parentId = -1;
	// 栏目名字
	protected String name;
	// 栏目图片地址
	protected String iconFilePath;
	//true:专题栏目，false：普通栏目
	protected boolean istopic=false;
	// 栏目子栏目
	protected List<Category> categoyChildren = new ArrayList<Category>();
	// 栏目应用
	protected List<PageApp> categoryPageApps = new ArrayList<PageApp>();

	/**
	 * New ADD:2016-03-22 Add by Yves Yang
	 **/

	protected int categoryNumber;

	public Category() {
		super();
	}

	public Category(int id, int parentId, String name) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.name = name;
	}

	public Category(int id, int parentId, String name, String iconFilePath) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.iconFilePath = iconFilePath;
	}

	public Category(int id, int parentId, String name, String iconFilePath, boolean istopic,
			List<Category> categoyChildren, List<PageApp> categoryPageApps) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.iconFilePath = iconFilePath;
		this.istopic = istopic;
		this.categoyChildren = categoyChildren;
		this.categoryPageApps = categoryPageApps;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCategoryNumber() {
		return categoryNumber;
	}

	public void setCategoryNumber(int categoryNumber) {
		this.categoryNumber = categoryNumber;
	}

	public String getIconFilePath() {
		return iconFilePath;
	}

	public void setIconFilePath(String iconFilePath) {
		this.iconFilePath = iconFilePath;
	}

	public List<Category> getCategoyChildren() {
		return categoyChildren;
	}

	public void setCategoyChildren(List<Category> categoyChildren) {
		this.categoyChildren = categoyChildren;
	}

	public List<PageApp> getCategoryPageApps() {
		return categoryPageApps;
	}

	public void setCategoryPageApps(List<PageApp> categoryPageApps) {
		this.categoryPageApps = categoryPageApps;
	}
	
	public boolean isIstopic() {
		return istopic;
	}

	public void setIstopic(boolean istopic) {
		this.istopic = istopic;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", parentId=" + parentId + ", name=" + name + ", iconFilePath=" + iconFilePath
				+ ", istopic=" + istopic + ", categoyChildren=" + categoyChildren + ", categoryPageApps="
				+ categoryPageApps + "]";
	}

}

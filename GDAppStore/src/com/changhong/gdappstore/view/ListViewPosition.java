package com.changhong.gdappstore.view;

import com.changhong.gdappstore.util.L;

public class ListViewPosition {
	int countNum;
	int fullShowNum;
	int startNumPosition;
	int endNumPosition;
	int layoutHeight;
	int itemHeight;
	int currentPosition;
	int remainderHeight;	//高度的余数
	int lastOffset;
	public ListViewPosition(int countNum,int layoutHeight,int itemHeight) {
		// TODO Auto-generated constructor stub
		this.countNum = countNum;
		this.layoutHeight = layoutHeight;
		this.itemHeight = itemHeight;
		
		fullShowNum = layoutHeight / itemHeight;
		
		if(fullShowNum >= countNum && countNum > 0) {
			fullShowNum = countNum;
			remainderHeight = 0;
		}
		else {
			remainderHeight = layoutHeight % itemHeight;
		}
		startNumPosition = 0;
		endNumPosition = fullShowNum - 1;
		currentPosition = 0;
		lastOffset = 0;
		
		L.i("countNum :" + this.countNum);
		L.i("layoutHeight :" + this.layoutHeight);
		L.i("itemHeight :" + this.itemHeight);
		L.i("fullShowNum :" + this.fullShowNum);
		L.i("startNumPosition :" + this.startNumPosition);
		L.i("endNumPosition :" + this.endNumPosition);
		L.i("remainderHeight :" + this.remainderHeight);
	}
	
	public int caculateOffset(int toPosition,boolean keyUpDown) {
		int offset = 0;
		L.i("startNumPosition : " + startNumPosition);
		L.i("endNumPosition : " + endNumPosition);
		L.i("currentPosition : " + currentPosition);
		L.i("keyUpDown : " + keyUpDown);
		if(toPosition < 0) {
			startNumPosition = 0;
			endNumPosition = fullShowNum - 1;
			currentPosition = 0;
			lastOffset = 0;
			return 0;
		}
		else if (toPosition > countNum - 1) {
			endNumPosition = countNum - 1;
			startNumPosition = endNumPosition - fullShowNum + 1;
			currentPosition = countNum - 1;
			lastOffset = offset = (fullShowNum - 1) * itemHeight + remainderHeight;
			return offset;
		}
		
		if(toPosition > currentPosition) {
			// 向下移动
			if(toPosition == countNum - 1) {
				//移动到最后一格
				offset = (fullShowNum - 1) * itemHeight + remainderHeight;
				endNumPosition = countNum - 1;
				startNumPosition = endNumPosition - fullShowNum + 1;
			}
			else if(toPosition > endNumPosition) {
				//需要向下切屏
				offset = (fullShowNum - 1) * itemHeight + remainderHeight / 2;
				startNumPosition++ ;
				endNumPosition++;
			}
			else {
				//不需要切屏
				if(0 == startNumPosition) {
					//上面位置不存在半屏
					offset = (toPosition - startNumPosition) * itemHeight;
				}
				else if(endNumPosition == countNum - 1) {
					//上面位置存在两个半屏
					offset = (toPosition - startNumPosition) * itemHeight + remainderHeight;
				}
				else {
					//上面位置存在一个半屏
					offset = (toPosition - startNumPosition) * itemHeight + remainderHeight / 2;
				}
			}
		}
		else if(toPosition < currentPosition) {
			//向上移动
			if (0 == toPosition) {
				//移动到第一格
				offset = 0;
				startNumPosition = 0;
				endNumPosition = fullShowNum - 1;
			}
			else if(toPosition < startNumPosition) {
				//需要向上切屏
				offset = remainderHeight / 2;
				startNumPosition-- ;
				endNumPosition--;
			}
			else {
				//不需要切屏
				if(0 == startNumPosition) {
					//上面位置不存在半屏
					offset = (toPosition - startNumPosition) * itemHeight;
				}
				else if(endNumPosition == countNum - 1) {
					//上面位置存在两个半屏
					offset = (toPosition - startNumPosition) * itemHeight + remainderHeight;
				}
				else {
					//上面位置存在一个半屏
					offset = (toPosition - startNumPosition) * itemHeight + remainderHeight / 2;;
				}
			}
		}
		else {
			//位置不变
			if(keyUpDown) {
				//表明是上下键触发的，此触发无效
				offset = -1;
			}
			else {
				if(startNumPosition == toPosition) {
					//移动到当前页的第一个位置
					if(startNumPosition == 0) {
						//当前页为第一页，offset为0
						offset = 0;
					}
					else if(endNumPosition == countNum - 1) {
						//当前页为最后一页，offset为两个半屏
						offset = remainderHeight;
					}
					else {
						//一个半屏
						offset = remainderHeight / 2;
					}
				}
				else {
					//不会改变offset
					offset = lastOffset;
				}
			}
		}
		
		if(-1 != offset) {
			lastOffset = offset;
			L.i("lastOffset = " + lastOffset);
			offset += (offset % itemHeight) == 0 ? (offset / itemHeight) * 2 : ((offset / itemHeight) + 1) * 2;
		}
		
		currentPosition = toPosition;
		L.i("Offset = " + offset + "toPosition = " + toPosition);
		return offset;
	}
	
	public int getRelativePosition() {
		return currentPosition - startNumPosition;
	}
	
	public int caculateAbsolutePosition(int relativePosition) {
		if(startNumPosition + relativePosition >= countNum) {
			return countNum - 1;
		}
		return startNumPosition + relativePosition;
	}
	
	public int getEndNumPosition() {
		return endNumPosition;
	}
	
	public int getStartNumPosition() {
		return startNumPosition;
	}
	
	public void resetParameter(int countNum) {
		this.countNum = countNum;
		
		fullShowNum = layoutHeight / itemHeight;
		if(fullShowNum >= countNum && countNum > 0) {
			fullShowNum = countNum;
			remainderHeight = 0;
		}
		else {
			remainderHeight = layoutHeight % itemHeight;
		}
		startNumPosition = 0;
		endNumPosition = fullShowNum - 1;
		currentPosition = 0;
		lastOffset = 0;
		
		L.i("resetParameter :");
		L.i("countNum :" + this.countNum);
		L.i("layoutHeight :" + this.layoutHeight);
		L.i("itemHeight :" + this.itemHeight);
		L.i("fullShowNum :" + this.fullShowNum);
		L.i("startNumPosition :" + this.startNumPosition);
		L.i("endNumPosition :" + this.endNumPosition);
		L.i("remainderHeight :" + this.remainderHeight);
	}
}

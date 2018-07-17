package com.interaction.data;

import android.util.Log;

public class DataCalaulate 
{
//	private static int ss_x;
//	private static int ss_y;

	//动态加载时，地图碎片的拼接需要的数据----------------------------------------------------------------------------------------
	//第一列的碎片内部切割x
	public static int getInsideX() 
	{	
		return DataStore.getX1() % DataStore.getsMap_width();
	}
	
	//第一行的碎片的内部切割y
	public static int getInsideY() 
	{
		return DataStore.getY1() % DataStore.getsMap_height();
	}
	
	//第一列的地图的切割宽度
	public static int getHead_width()
	{
		return DataStore.getsMap_width()-DataCalaulate.getInsideX();
	}
	//最后一列的地图切割宽度
	public static int getEnd_width()
	{
		return DataStore.getX2()%DataStore.getsMap_width();
	}
	//第一行的切割高度
	public static int getHead_height()
	{
		return DataStore.getsMap_height()-DataCalaulate.getInsideY();
	}
	//最后一行的切割高度
	public static int getEnd_height()
	{
		if((DataStore.getY1()+DataStore.getHeight())%DataStore.getsMap_height()!=0)
		{
			return (DataStore.getY1()+DataStore.getHeight())%DataStore.getsMap_height();//DataCalaulate.getEMap_y();			
		}
		else {
			return DataStore.getsMap_height();
		}
	}
	//中间的x方向位置
	public static int getOutsideX(int i)
	{
//		if(i>49)
//			i=i-50;
		return DataCalaulate.getHead_width()+(i-1)*DataStore.getsMap_width();			
	}
	//中间的y方向
	public static int getOutsideY(int j)
	{
		return DataCalaulate.getHead_height()+(j-1)*DataStore.getsMap_height();
	}
//	//最后一列的x方向位置
//	public static int getEMap_X()
//	{
//		return DataStore.getWidth()-DataCalaulate.getEnd_width();
//	}
//	//最后一行的y方向位置
//	public static int getEMap_Y()
//	{
//		return DataStore.getHeight()-DataCalaulate.getEnd_height();
//	}
	
	
	// 地图碎片数据end----------------------------------------------------------------------------------------------------
}

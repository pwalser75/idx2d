package idx2d;

import java.awt.image.DirectColorModel;
import java.awt.Color;

public final class Color24
{
	private final static int ALPHA=0xFF000000;
	private final static int RED=0xFF0000;
	private final static int GREEN=0xFF00;
	private final static int BLUE=0xFF;
	private final static int MASK=0xFEFEFEFE;
	
	private static int pixel,overflow,r,g,b;
	private static float[] hsb=new float[3];
	
	private static DirectColorModel colorModel=new DirectColorModel(24,RED,GREEN,BLUE);
	
	private Color24()
	{
	}

	public static DirectColorModel getColorModel()
	{
		return colorModel;
	}
	
	public static int getColor(int r, int g, int b)
	{
		return ALPHA | (r<<16) | (g<<8) | b;
	}
	
	public static int getRed(int color)
	{
		return (color & RED) >>16;
	}
	
	public static int getGreen(int color)
	{
		return (color & GREEN) >>8;
	}
	
	public static int getBlue(int color)
	{
		return color & BLUE;
	}
	
	public static final int add(int color1, int color2)
	{
		pixel=(color1&0xFEFEFE)+(color2&0xFEFEFE);
		overflow=pixel&0x1010100;
		overflow=overflow-(overflow>>8);
		return ALPHA|overflow|pixel;
	}
	
	public static final int sub(int color1, int color2)
	{
		pixel=(color1&0xFEFEFE)+(~color2&0xFEFEFE);
		overflow=~pixel&0x1010100;
		overflow=overflow-(overflow>>8);
		return ALPHA|(~overflow&pixel);
	}
	
	public static final int scale(int color, int factor)
	{
		factor=Math.min(factor,255);
		r=(((color>>16)&255)*factor)>>8;
		g=(((color>>8)&255)*factor)>>8;
		b=((color&255)*factor)>>8;
		return ALPHA|(r<<16)|(g<<8)|b;
	}
	
	public static final int mix(int color1, int color2, int factor)
	{
		return add(scale(color1,factor),scale(color2,255-factor));
	}
	
	public static int mix(int oldpixel, int newpixel)
	{
		return ALPHA|(((oldpixel&MASK)>>1)+((newpixel&MASK)>>1));
	}
	
	public static final int multiply(int color1, int color2)
	{
		r=(((color1>>16)&255)*((color2>>16)&255))>>8;
		g=(((color1>>8)&255)*((color2>>8)&255))>>8;
		b=((color1&255)*(color2&255))>>8;
		return ALPHA|(r<<16)|(g<<8)|b;
	}
	
	public static int getGray(int c)
	{
		int brightness=(getRed(c)*3+getGreen(c)*6+getBlue(c))/10;
		return getColor(brightness,brightness,brightness);
	}
	
	public static int createGray(int c)
	{
		return getColor(c,c,c);
	}
	
	public static float getHue(int c)
	{
		Color.RGBtoHSB(getRed(c),getGreen(c),getBlue(c),hsb);
		return hsb[0];
	}
	
	public static float getSaturation(int c)
	{
		Color.RGBtoHSB(getRed(c),getGreen(c),getBlue(c),hsb);
		return hsb[1];
	}
	
	public static float getBrightness(int c)
	{
		Color.RGBtoHSB(getRed(c),getGreen(c),getBlue(c),hsb);
		return hsb[2];
	}	
}
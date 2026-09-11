package idx2d;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.*;
//import com.sun.image.codec.jpeg.*;
import java.io.*;

public class Texture
{
	public int width, height;
	public int[] pixel;
	private FastImageProducer producer=null;
	private Image renderedImage=null;
	
	
	public Texture(int width, int height)
	{
		this.width=width;
		this.height=height;
		pixel=new int[width*height];
	}
	
	public Texture(int width, int height, int[] pixel)
	{
		this.width=width;
		this.height=height;
		this.pixel=pixel;
	}
	
	public Texture(Image img)
	{
		try
		{
			while (((width=img.getWidth(null))<0)||((height=img.getHeight(null))<0));

			pixel=new int[width*height];
			PixelGrabber pg=new PixelGrabber(img,0,0,width,height,pixel,0,width);
			pg.grabPixels();
		}
		catch (InterruptedException e) {}
	}
	
	public Texture(String imageName)
	{
		this(Toolkit.getDefaultToolkit().getImage(imageName));
	}
	
	public Texture(byte[] rawImageData)
	{
		this(Toolkit.getDefaultToolkit().createImage(rawImageData));
	}
	
	public Image getImage()
	{
		if (producer==null)
		{
			producer=new FastImageProducer(width,height,Color24.getColorModel(),pixel);
			renderedImage=Toolkit.getDefaultToolkit().createImage(producer);
		}
	
		producer.update();
		return renderedImage;		
	}

	public void cls()
	{
		int[] buffer=pixel;
		int size=buffer.length-1;
		int cleared=1;
		int index=1;
		buffer[0]=0xFF000000;

		while (cleared<size)
		{
			System.arraycopy(buffer,0,buffer,index,cleared);
			size-=cleared;
			index+=cleared;
			cleared<<=1;
		}
		System.arraycopy(buffer,0,buffer,index,size);
	}	
	
	public void flipVertical()
	{
		int[] temp=new int[width*height];
		for(int y=0;y<height;y++)
			for(int x=0;x<width;x++)
				temp[x+(height-1-y)*width]=pixel[x+y*width];
		pixel=temp;
	}
	
	public void flipHorizontal()
	{
		int[] temp=new int[width*height];
		for(int y=0;y<height;y++)
			for(int x=0;x<width;x++)
				temp[(width-1-x)+y*width]=pixel[x+y*width];
		pixel=temp;
	}
	
	public int getBilinearPixel(float x, float y)
	{
		float x2=(float)Math.min(x,width-2);
		float y2=(float)Math.min(y,height-2);
		
		int xpos=(int)x2;
		int ypos=(int)y2;
		float xrel=x2-xpos;
		float yrel=y2-ypos;
		int colorA=pixel[xpos+ypos*width];
		int colorB=pixel[xpos+1+ypos*width];
		int colorC=pixel[xpos+(ypos+1)*width];
		int colorD=pixel[xpos+1+(ypos+1)*width];
		int weightA=(int)(255*(1-xrel)*(1-yrel));
		int weightB=(int)(255*xrel*(1-yrel));
		int weightC=(int)(255*(1-xrel)*yrel);
		int weightD=(int)(255*xrel*yrel);
		int r=((((colorA>>16)&255)*weightA)>>8)
			+((((colorB>>16)&255)*weightB)>>8)
			+((((colorC>>16)&255)*weightC)>>8)
			+((((colorD>>16)&255)*weightD)>>8);
		int g=((((colorA>>8)&255)*weightA)>>8)
			+((((colorB>>8)&255)*weightB)>>8)
			+((((colorC>>8)&255)*weightC)>>8)
			+((((colorD>>8)&255)*weightD)>>8);
		int b=(((colorA&255)*weightA)>>8)
			+(((colorB&255)*weightB)>>8)
			+(((colorC&255)*weightC)>>8)
			+(((colorD&255)*weightD)>>8);
		return 0xFF000000|(r<<16)|(g<<8)|b;
	}
	
	public Texture getClone()
	{
		int[] clone=new int[width*height];
		System.arraycopy(pixel,0,clone,0,width*height);
		return new Texture(width,height,clone);
	}
	
	public void cloneInto(Texture other)
	{
		if (!compatibleWith(other)) return;
		System.arraycopy(pixel,0,other.pixel,0,width*height);
	}
	
	public boolean compatibleWith(Texture other)
	{
		return (width==other.width && height==other.height);
	}
	
	public void resize(int w, int h)
	{
		Texture t=bilinearResample(this,w,h);
		pixel=t.pixel;
		width=t.width;
		height=t.height;
	}
	
	public static Texture bilinearResample(Texture oldT, int width, int height)
	{
		if (oldT.width==width && oldT.height==height) return oldT.getClone();
		Texture newT=new Texture(width,height);
		
		int w=oldT.width;
		int h=oldT.height;
		int pos=0;
		float x,y,xrel,yrel;
		int xpos,ypos,colorA,colorB,colorC,colorD,weightA,weightB,weightC,weightD;
		int r,g,b;
		
		for (int ydest=0; ydest<height; ydest++)
			for (int xdest=0; xdest<width; xdest++)
			{
				x=(float)xdest*(w-1)/width;
				y=(float)ydest*(h-1)/height;
				
				xpos=(int)x;
				ypos=(int)y;
				xrel=x-xpos;
				yrel=y-ypos;
				colorA=oldT.pixel[xpos+ypos*w];
				colorB=oldT.pixel[xpos+1+ypos*w];
				colorC=oldT.pixel[xpos+(ypos+1)*w];
				colorD=oldT.pixel[xpos+1+(ypos+1)*w];
				weightA=(int)(255*(1-xrel)*(1-yrel));
				weightB=(int)(255*xrel*(1-yrel));
				weightC=(int)(255*(1-xrel)*yrel);
				weightD=(int)(255*xrel*yrel);
				
				r=((((colorA>>16)&255)*weightA)>>8)
					+((((colorB>>16)&255)*weightB)>>8)
					+((((colorC>>16)&255)*weightC)>>8)
					+((((colorD>>16)&255)*weightD)>>8);
				g=((((colorA>>8)&255)*weightA)>>8)
					+((((colorB>>8)&255)*weightB)>>8)
					+((((colorC>>8)&255)*weightC)>>8)
					+((((colorD>>8)&255)*weightD)>>8);
				b=(((colorA&255)*weightA)>>8)
					+(((colorB&255)*weightB)>>8)
					+(((colorC&255)*weightC)>>8)
					+(((colorD&255)*weightD)>>8);
				
				newT.pixel[pos++]=0xFF000000|(r<<16)|(g<<8)|b;
			}
		return newT;
	}
	
	public Texture resizeHighQuality(int width, int height)
	{
		Texture newTexture=new Texture(width,height);
		int w=this.width;
		int h=this.height;
		float xscale=(float)w/width;
		float yscale=(float)h/height;
		
		for (int y=0; y<height; y++)
		{
			for (int x=0; x<width; x++)
			{
				int r=0;
				int g=0;
				int b=0;
				
				int grid=4;
				
				for (int i=0; i<grid; i++)
				{
					for (int j=0; j<grid; j++)
					{
						int color=getBilinearPixel(xscale*((float)x+(float)i/grid),yscale*((float)y+(float)j/grid));	
						r+=(color>>16)&0xFF;
						g+=(color>>8)&0xFF;
						b+=color&0xFF;
					}	
				}
				
				r>>=4;
				g>>=4;
				b>>=4;
				newTexture.pixel[x+y*width]=(r<<16)|(g<<8)|b;
			}
		}
		return newTexture;
	}
	
	public Texture rotate(int mx, int my, double angle)
	{
		int dx,dy; // Distance to rotation center
		int tx,ty;
		double sin=Math.sin(angle);
		double cos=Math.cos(angle);
		
		int[] newpixel=new int[width*height];
		
		for (int y=0; y<height; y++)
		{
			for (int x=0; x<width; x++)
			{
				dx=x-mx;
				dy=y-my;
				tx= (int)(dx*cos - dy*sin);
				ty= (int)(dy*cos + dx*sin);
				
				tx+=mx;
				ty+=my;
				
				if (tx<0 || tx>=width || ty<0 || ty>=height)
					newpixel[x+y*width]=0xFF000000;				else
				{
					tx=(tx+2*width)%width;
					ty=(ty+2*height)%height;
				
					newpixel[x+y*width]=pixel[tx+ty*width];
				}
			}
		}
		
		return new Texture(width,height,newpixel);
	}
	
	public void add(Texture texture, int posx, int posy, int xsize, int ysize)
	{
		if (xsize==0) return;
		if (ysize==0) return;
		
		int w=xsize;
		int h=ysize;
		int xBase=posx;
		int yBase=posy;
		int tx=texture.width*255;
		int ty=texture.height*255;
		int tw=texture.width;
		int dtx=tx/w;
		int dty=ty/h;
		int txBase=crop(-xBase*dtx,0,255*tx);
		int tyBase=crop(-yBase*dty,0,255*ty);
		int xend=crop(xBase+w,0,width);
		int yend=crop(yBase+h,0,height);
		int pos,offset1,offset2;
		xBase=crop(xBase,0,width);
		yBase=crop(yBase,0,height);

		ty=tyBase;
		for(int j=yBase;j<yend;j++)
		{
			tx=txBase;
			offset1=j*width;
			offset2=(ty>>8)*tw;
			for(int i=xBase;i<xend;i++)
			{
				pixel[i+offset1]=Color24.add(texture.pixel[(tx>>8)+offset2], pixel[i+offset1]);
				tx+=dtx;
			}
			ty+=dty;
		}
	}
	
	public void paint(Texture texture, int posx, int posy, int xsize, int ysize)
	{
		if (xsize==0) return;
		if (ysize==0) return;
		
		int w=xsize;
		int h=ysize;
		int xBase=posx;
		int yBase=posy;
		int tx=texture.width*255;
		int ty=texture.height*255;
		int tw=texture.width;
		int dtx=tx/w;
		int dty=ty/h;
		int txBase=crop(-xBase*dtx,0,255*tx);
		int tyBase=crop(-yBase*dty,0,255*ty);
		int xend=crop(xBase+w,0,width);
		int yend=crop(yBase+h,0,height);
		int pos,offset1,offset2;
		xBase=crop(xBase,0,width);
		yBase=crop(yBase,0,height);

		ty=tyBase;
		for(int j=yBase;j<yend;j++)
		{
			tx=txBase;
			offset1=j*width;
			offset2=(ty>>8)*tw;
			for(int i=xBase;i<xend;i++)
			{
				pixel[i+offset1]=texture.pixel[(tx>>8)+offset2];
				tx+=dtx;
			}
			ty+=dty;
		}
	}	
	
	protected int crop(int val, int min, int max)
	{
		return Math.max(Math.min(val,max),min);
	}
	/*
	public void writeToStream(OutputStream stream, float quality) throws IOException
	{
		BufferedImage buf = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		java.awt.Graphics g = buf.createGraphics();
		g.drawImage(getImage(),0,0,null);

		JPEGEncodeParam jepar = JPEGCodec.getDefaultJPEGEncodeParam(buf);
		jepar.setQuality(quality, true);
    
		JPEGImageEncoder jpege = JPEGCodec.createJPEGEncoder(stream) ;
		jpege.encode(buf, jepar);
		stream.flush();
	}*/
	/*
	public byte[] getRawImageData()
	{
		try
		{
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			writeToStream(out,1f);
			return out.toByteArray();                   
           
		} 
		catch (java.io.IOException ioe) 
		{ 
			return null;
            	}
	}*/
}
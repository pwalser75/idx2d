package idx2d;

import java.awt.*;
import java.io.*;
import java.awt.image.*;

public class BitmapCodec extends Component 
{
	private final static int BITMAPFILEHEADER_SIZE = 14;
	private final static int BITMAPINFOHEADER_SIZE = 40;

	// Bitmap file header
	private byte bitmapFileHeader [] = new byte [14];
	private byte bfType [] = {'B', 'M'};
	private int bfSize = 0;
	private int bfReserved1 = 0;
	private int bfReserved2 = 0;
	private int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;

	// Bitmap info header
	private byte bitmapInfoHeader [] = new byte [40];
	private int biSize = BITMAPINFOHEADER_SIZE;
	private int biWidth = 0;
	private int biHeight = 0;
	private int biPlanes = 1;
	private int biBitCount = 24;
	private int biCompression = 0;
	private int biSizeImage = 0x030000;
	private int biXPelsPerMeter = 0x0;
	private int biYPelsPerMeter = 0x0;
	private int biClrUsed = 0;
	private int biClrImportant = 0;

	// Bitmap raw data
	private int bitmap [];

	// File section
	private FileOutputStream fo;
	
	public static void main(String[] args)
	{
		Texture t=new Texture(new BitmapCodec().loadBitmap(new File("test.bmp")));	
		new BitmapCodec().saveBitmap(new File("foo.bmp") ,t.getImage());
		System.exit(0);
	}

	// Default constructor
	public BitmapCodec() 
	{

	}

	public void saveBitmap (File f, Image img)
	{
		saveBitmap(f,img,img.getWidth(null),img.getHeight(null));
	}
	
	public void saveBitmap (File f, Image parImage, int parWidth, int parHeight)
	{
		try 
		{
			fo = new FileOutputStream (f);
			save (parImage, parWidth, parHeight);
			fo.close ();	
		}
		catch (Exception saveEx) { saveEx.printStackTrace (); }
	}

	private void save (Image parImage, int parWidth, int parHeight)  throws Exception
	{
		convertImage (parImage, parWidth, parHeight);
		writeBitmapFileHeader ();
		writeBitmapInfoHeader ();
		writeBitmap ();
	}

	private void convertImage (Image parImage, int parWidth, int parHeight)  throws Exception
	{
		int pad;
		bitmap = new int [parWidth * parHeight];

		PixelGrabber pg = new PixelGrabber (parImage, 0, 0, parWidth, parHeight,bitmap, 0, parWidth);
		pg.grabPixels ();
		
		pad = (4 - ((parWidth * 3) % 4)) * parHeight;
		biSizeImage = ((parWidth * parHeight) * 3) + pad;
		bfSize = biSizeImage + BITMAPFILEHEADER_SIZE +BITMAPINFOHEADER_SIZE;
		biWidth = parWidth;
		biHeight = parHeight;
	}

	private void writeBitmap ()  throws Exception
	{
		int size;
		int value;
		int j;
		int i;
		int rowCount;
		int rowIndex;
		int lastRowIndex;
		int pad;
		int padCount;
		byte rgb [] = new byte [3];
	
	
		size = (biWidth * biHeight) - 1;
		pad = 4 - ((biWidth * 3) % 4);
		if (pad == 4)
		pad = 0;
		rowCount = 1;
		padCount = 0;
		rowIndex = size - biWidth;
		lastRowIndex = rowIndex;

		for (j = 0; j < size; j++) 
	 	{
			value = bitmap [rowIndex];
			rgb [0] = (byte) (value & 0xFF);
			rgb [1] = (byte) ((value >> 8) & 0xFF);
			rgb [2] = (byte) ((value >> 16) & 0xFF);
			fo.write (rgb);
			if (rowCount == biWidth) 
			{
				padCount += pad;
				for (i = 1; i <= pad; i++) fo.write (0x00);
				rowCount = 1;
				rowIndex = lastRowIndex - biWidth;
				lastRowIndex = rowIndex;
			}
			else rowCount++;
			rowIndex++;
		 }

		 bfSize += padCount - pad;
		 biSizeImage += padCount - pad;
	 }	

	private void writeBitmapFileHeader () throws Exception
	{
		fo.write (bfType);
		fo.write (intToDWord (bfSize));
		fo.write (intToWord (bfReserved1));
		fo.write (intToWord (bfReserved2));
		fo.write (intToDWord (bfOffBits));
	}

	private void writeBitmapInfoHeader ()  throws Exception
	{
		fo.write (intToDWord (biSize));
		fo.write (intToDWord (biWidth));
		fo.write (intToDWord (biHeight));
		fo.write (intToWord (biPlanes));
		fo.write (intToWord (biBitCount));
		fo.write (intToDWord (biCompression));
		fo.write (intToDWord (biSizeImage));
		fo.write (intToDWord (biXPelsPerMeter));
		fo.write (intToDWord (biYPelsPerMeter));
		fo.write (intToDWord (biClrUsed));
		fo.write (intToDWord (biClrImportant));
	}

	private byte [] intToWord (int parValue) 
	{
		byte retValue [] = new byte [2];
	
		retValue [0] = (byte) (parValue & 0x00FF);
		retValue [1] = (byte) ((parValue >> 8) & 0x00FF);
	
		return (retValue);
	}

	private byte [] intToDWord (int parValue) 
	{
		byte retValue [] = new byte [4];
	
		retValue [0] = (byte) (parValue & 0x00FF);
		retValue [1] = (byte) ((parValue >> 8) & 0x000000FF);
		retValue [2] = (byte) ((parValue >> 16) & 0x000000FF);
		retValue [3] = (byte) ((parValue >> 24) & 0x000000FF);
	
		return (retValue);
	}

	public Image loadBitmap (File f)
	{
		Image image;
		System.out.println("loading:"+f.getPath());
		try
		{
			FileInputStream fs=new FileInputStream(f);
			int bflen=14;	// 14 byte BITMAPFILEHEADER
			byte bf[]=new byte[bflen];
			fs.read(bf,0,bflen);
			int bilen=40; // 40-byte BITMAPINFOHEADER
			byte bi[]=new byte[bilen];
			fs.read(bi,0,bilen);
	
			// Interperet data.
			int nsize = (((int)bf[5]&0xff)<<24) 
				| (((int)bf[4]&0xff)<<16)
				| (((int)bf[3]&0xff)<<8)
				| (int)bf[2]&0xff;
			System.out.println("File type is :"+(char)bf[0]+(char)bf[1]);
			System.out.println("Size of file is :"+nsize);
	
			int nbisize = (((int)bi[3]&0xff)<<24)
				| (((int)bi[2]&0xff)<<16)
				| (((int)bi[1]&0xff)<<8)
				| (int)bi[0]&0xff;
			System.out.println("Size of bitmapinfoheader is :"+nbisize);
	
			int nwidth = (((int)bi[7]&0xff)<<24)
				| (((int)bi[6]&0xff)<<16)
				| (((int)bi[5]&0xff)<<8)
				| (int)bi[4]&0xff;
			System.out.println("Width is :"+nwidth);
	
			int nheight = (((int)bi[11]&0xff)<<24)
				| (((int)bi[10]&0xff)<<16)
				| (((int)bi[9]&0xff)<<8)
				| (int)bi[8]&0xff;
			System.out.println("Height is :"+nheight);
	
			int nplanes = (((int)bi[13]&0xff)<<8) | (int)bi[12]&0xff;
			System.out.println("Planes is :"+nplanes);
	
			int nbitcount = (((int)bi[15]&0xff)<<8) | (int)bi[14]&0xff;
			System.out.println("BitCount is :"+nbitcount);
	
			// Look for non-zero values to indicate compression
			int ncompression = (((int)bi[19])<<24)
				| (((int)bi[18])<<16)
				| (((int)bi[17])<<8)
				| (int)bi[16];
			System.out.println("Compression is :"+ncompression);
	
			int nsizeimage = (((int)bi[23]&0xff)<<24)
				| (((int)bi[22]&0xff)<<16)
				| (((int)bi[21]&0xff)<<8)
				| (int)bi[20]&0xff;
			System.out.println("SizeImage is :"+nsizeimage);
	
			int nxpm = (((int)bi[27]&0xff)<<24)
				| (((int)bi[26]&0xff)<<16)
				| (((int)bi[25]&0xff)<<8)
				| (int)bi[24]&0xff;
			System.out.println("X-Pixels per meter is :"+nxpm);
	
			int nypm = (((int)bi[31]&0xff)<<24)
				| (((int)bi[30]&0xff)<<16)
				| (((int)bi[29]&0xff)<<8)
				| (int)bi[28]&0xff;
			System.out.println("Y-Pixels per meter is :"+nypm);
	
			int nclrused = (((int)bi[35]&0xff)<<24)
				| (((int)bi[34]&0xff)<<16)
				| (((int)bi[33]&0xff)<<8)
				| (int)bi[32]&0xff;
			System.out.println("Colors used are :"+nclrused);
	
			int nclrimp = (((int)bi[39]&0xff)<<24)
				| (((int)bi[38]&0xff)<<16)
				| (((int)bi[37]&0xff)<<8)
				| (int)bi[36]&0xff;
			System.out.println("Colors important are :"+nclrimp);

			if (nbitcount==24)
			{
				// No Palatte data for 24-bit format but scan lines are
				// padded out to even 4-byte boundaries.
				int npad = (nsizeimage / nheight) - nwidth * 3;
				int ndata[] = new int [nheight * nwidth];
				byte brgb[] = new byte [( nwidth + npad) * 3 * nheight];
				fs.read (brgb, 0, (nwidth + npad) * 3 * nheight);
				int nindex = 0;
				for (int j = 0; j < nheight; j++)
				{
					for (int i = 0; i < nwidth; i++)
					{
						ndata [nwidth * (nheight - j - 1) + i] =
								(255&0xff)<<24
								| (((int)brgb[nindex+2]&0xff)<<16)
								| (((int)brgb[nindex+1]&0xff)<<8)
								| (int)brgb[nindex]&0xff;
						nindex += 3;
					}
					nindex += npad;
				}
	
				image = createImage( new MemoryImageSource (nwidth, nheight,ndata, 0, nwidth));
			}
			else if (nbitcount == 8)
			{
				// Have to determine the number of colors, the clrsused
				// parameter is dominant if it is greater than zero.	If
				// zero, calculate colors based on bitsperpixel.
				int nNumColors = 0;
				
				if (nclrused > 0) nNumColors = nclrused;
				else nNumColors = (1&0xff)<<nbitcount;
				
				System.out.println("The number of Colors is"+nNumColors);
		
				// Some bitmaps do not have the sizeimage field calculated
				// Ferret out these cases and fix 'em.
				if (nsizeimage == 0)
				{
						nsizeimage = ((((nwidth*nbitcount)+31) & ~31 ) >> 3);
						nsizeimage *= nheight;
						System.out.println("nsizeimage (backup) is"+nsizeimage);
				}
		
				// Read the palatte colors.
				int npalette[] = new int [nNumColors];
				byte bpalette[] = new byte [nNumColors*4];
				fs.read (bpalette, 0, nNumColors*4);
				int nindex8 = 0;
				for (int n = 0; n < nNumColors; n++)
				{
					npalette[n] = (255&0xff)<<24
						| (((int)bpalette[nindex8+2]&0xff)<<16)
						| (((int)bpalette[nindex8+1]&0xff)<<8)
						| (int)bpalette[nindex8]&0xff;
		
					nindex8 += 4;
				}
	
				// Read the image data (actually indices into the palette)
				// Scan lines are still padded out to even 4-byte
				// boundaries.
				int npad8 = (nsizeimage / nheight) - nwidth;
				System.out.println("nPad is:"+npad8);
				
				int ndata8[] = new int [nwidth*nheight];
				byte bdata[] = new byte [(nwidth+npad8)*nheight];
				fs.read (bdata, 0, (nwidth+npad8)*nheight);
				nindex8 = 0;
				for (int j8 = 0; j8 < nheight; j8++)
				{
					for (int i8 = 0; i8 < nwidth; i8++)
					{
						ndata8 [nwidth*(nheight-j8-1)+i8] = npalette [((int)bdata[nindex8]&0xff)];
						nindex8++;
					}
				nindex8 += npad8;
				}
	
				image = createImage ( new MemoryImageSource (nwidth, nheight,ndata8, 0, nwidth));
			}
			else
			{
				System.out.println ("Not a 24-bit or 8-bit Windows Bitmap, aborting...");
				image = null;
			}

			fs.close();
			return image;
		}
		catch (Exception e) { System.out.println("Caught exception in loadbitmap!"); }
		return null;
	}
}
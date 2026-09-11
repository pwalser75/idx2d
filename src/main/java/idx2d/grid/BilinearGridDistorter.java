package idx2d.grid;
import idx2d.*;

public class BilinearGridDistorter implements GridDistorter
{
	public int width=0;
	public int height=0;
	private int toleranceX,toleranceY;
	
	private Grid8x8 grid;
	private int source[];	
	private int pixel[];

	private int offset;
	private int xpos,ypos;
	private float tx,ty,tw,th;
	private float dtx,dty;
	private float uL1,uL2,uR1,uR2,duL,duR,uL,uR;
	private float vL1,vL2,vR1,vR2,dvL,dvR,vL,vR;
	
	private float tw2,th2;

	private Texture distortedTexture;
	private Texture t;

	// C O N S T R U C T O R S

		public BilinearGridDistorter(int w,int h)
		{
			width=w;
			height=h;
			pixel=new int[width*height];
			distortedTexture=new Texture(width,height,pixel);
		}

		public Texture distort(Texture t, Grid8x8 g)
		{
			this.t=t;
			grid=g;
			tw=t.width;
			th=t.height;
			tw2=tw/2;
			th2=th/2;
			
			source=t.pixel;
			grid.convert(t);
			renderGrid();
			return distortedTexture;
		}

	// P R I V A T E   M E T H O D S

		private void renderGrid()
		{
			for (int n=grid.height-2;n>=0;n--)
				for (int m=grid.width-2;m>=0;m--) 
					renderArea(m,n);
		}

		private void renderArea(int gridX, int gridY)
		{
			float convert=1f/65536f;
			uL1=convert*grid.node[gridX][gridY].tx;
			uR1=convert*grid.node[gridX+1][gridY].tx;
			uL2=convert*grid.node[gridX][gridY+1].tx;
			uR2=convert*grid.node[gridX+1][gridY+1].tx;

			vL1=convert*grid.node[gridX][gridY].ty;
			vR1=convert*grid.node[gridX+1][gridY].ty;
			vL2=convert*grid.node[gridX][gridY+1].ty;
			vR2=convert*grid.node[gridX+1][gridY+1].ty;
			
			duL=uL2-uL1;
			duR=uR2-uR1;
			dvL=vL2-vL1;
			dvR=vR2-vR1;
			if (duL>tw2) duL-=tw2*2;
			if (duL<-tw2) duL+=tw2*2;
			if (duR>tw2) duR-=tw2*2;
			if (duR<-tw2) duR+=tw2*2;
			if (dvL>tw2) dvL-=tw2*2;
			if (dvL<-tw2) dvL+=tw2*2;
			if (dvR>tw2) dvR-=tw2*2;
			if (dvR<-tw2) dvR+=tw2*2;
			
			duL/=8;
			duR/=8;
			dvL/=8;
			dvR/=8;
			
			uL=uL1;
			uR=uR1;
			vL=vL1;
			vR=vR1;
			
			xpos=gridX<<3;
			ypos=gridY<<3;
			
			for(int i=0;i<8;i++)
			{
				renderLine(xpos,ypos+i,uL,uR,vL,vR);
				uL+=duL;
				uR+=duR;
				vL+=dvL;
				vR+=dvR;
			}
		}

		private void renderLine(int x, int y, float txL, float txR, float tyL, float tyR)
		{
			offset=y*width+x;
			tx=(txL%tw)+(tw*16);
			ty=(tyL%th)+(th*16);
			dtx=txR-txL;
			dty=tyR-tyL;
			if (dtx>tw2) dtx-=tw2*2;
			if (dtx<-tw2) dtx+=tw2*2;
			if (dty>tw2) dty-=tw2*2;
			if (dty<-tw2) dty+=tw2*2;
			
			dtx/=8;
			dty/=8;
			
			for(int i=0;i<8;i++)
			{
				pixel[offset+i]=t.getBilinearPixel(tx%tw,ty%th);
				tx+=dtx;
				ty+=dty;
			}
			
		}
			
}
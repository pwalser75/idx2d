package idx2d;

import java.awt.image.*;

public class FastImageProducer implements ImageProducer
{
	private ImageConsumer consumer;
	private int w,h;
	private ColorModel cm;
	private int[] pixel;
	private int hints,sfd;
	
	// C O N S T R U C T O R

	 	public FastImageProducer(int w, int h, ColorModel cm, int pixel[])
		{
			this.w=w;
			this.h=h;
			this.cm=cm;
			this.pixel=pixel;
			hints=ImageConsumer.TOPDOWNLEFTRIGHT
				|ImageConsumer.COMPLETESCANLINES
				|ImageConsumer.SINGLEPASS
				|ImageConsumer.SINGLEFRAME;
			sfd=ImageConsumer.SINGLEFRAMEDONE;
		}
	
	//  P U B L I C   M E T H O D S

		public synchronized void addConsumer(ImageConsumer consumer)
		{
			this.consumer=consumer;
		}

  		public final void startProduction(ImageConsumer imageconsumer)
    		{
    			if (consumer!=imageconsumer)
    			{
	    			consumer=imageconsumer;
	    			consumer.setDimensions(w,h);
	    			consumer.setProperties(null);
	    			consumer.setColorModel(cm);
	    			consumer.setHints(hints);
	    		}
    			consumer.setPixels(0, 0, w, h, cm, pixel, 0, w);
			consumer.imageComplete(sfd);
        		}
        	
        		
		public void update()
		{
			if (consumer!=null) startProduction(consumer);
		}
		
		public final boolean isConsumer(ImageConsumer imageconsumer)
		{
			return consumer==imageconsumer;
		}
		
		public final void requestTopDownLeftRightResend(ImageConsumer imageconsumer)
		{
		}
		
		public final void removeConsumer(ImageConsumer imageconsumer)
		{
		}	
}

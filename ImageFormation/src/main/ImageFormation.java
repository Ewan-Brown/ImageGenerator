package main;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
public class ImageFormation extends JPanel{
	private static final long serialVersionUID = 1L;
	static BufferedImage i;
	static int w = 0;
	static int h = 0;
	static Particle[][][] particles;
	static int[][] cells;
	static boolean finished = false;
	static int ticks = 0;
	static int imageNum = 0;
	static int renderTicks = 0;
	static ArrayList<BufferedImage> images = new ArrayList<>();
	static Random rand = new Random();
	static Path p;
	static boolean colorSpecificMode = true;
	static boolean quit = false;
	public static void main(String args[]){
//		final URI uri;
//		final URI exe;
//
//		try {
//			uri = getJarURI();
//			exe = getFile(uri, "Main.class");
//			System.out.println(exe);
//		} catch (URISyntaxException | IOException e) {
//			e.printStackTrace();
//		}
				try {
					p = Files.createTempDirectory("PS",new FileAttribute<?>[0]);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.out.println(p.toAbsolutePath());
				System.setProperty("sun.java2d.opengl","True");
				FileDialog fd = new FileDialog((java.awt.Frame) null);
				fd.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						quit = true;
						deleteFolder(p.toFile());
					}
				});
				fd.setTitle("Choose an image");
				fd.setVisible(true);
				File f = new File(fd.getDirectory() + fd.getFile());
				if(fd.getDirectory() == null || fd.getFile() == null)
					System.exit(0);
				BufferedImage img = null;
				try {
					img = ImageIO.read(f);
					img.getType();
				} catch (IOException | NullPointerException e) {}
				i = img;
				w = i.getWidth();
				h = i.getHeight();
				particles = new Particle[w][h][3];
				cells = new int[w][h];
				for(int x = 0; x < w;x++){
					for(int y = 0; y < h;y++){
						int rgb = i.getRGB(x, y);
						int red = (rgb >> 16) & 0x000000FF;
						int green = (rgb >> 8) & 0x000000FF;
						int blue = (rgb) & 0x000000FF;
						//				red = 0;
						//				green = 0;
						//				blue = 0;
						//				particles[x][y][0] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,red << 16);
						//				particles[x][y][1] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,green << 8);
						//				particles[x][y][2] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,blue);
						particles[x][y][0] = new Particle(0,rand.nextInt(h),x,y,red << 16);
						particles[x][y][1] = new Particle(w,rand.nextInt(h),x,y,green << 8);
						particles[x][y][2] = new Particle(0,rand.nextInt(h),x,y,blue);
					}
				}
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				ImageFormation m = new ImageFormation();
				frame.add(m);
				frame.setSize(w,h);
				frame.setVisible(true);
				frame.setFocusable(true);
				frame.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						quit = true;
						deleteFolder(p.toFile());
					}
				});
				while(true){
					renderTicks++;
					if(!finished){
						m.update();
					}
					m.repaint();
				}
	}
	static final int clearbits(final int x, final int p, final int n) {
		return x & ~((~0 << (32 - n)) >>> p);
	}
	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files!=null) { //some JVMs return null for empty dirs
			for(File f: files) {
				if(f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}
	public static double getDistance( double x1,  double y1,  double x2,  double y2) {
		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
	}
	int switcher = 0;
	public void update(){
		boolean flag = true;
		int count = 0;
		long t0 = System.nanoTime();
		for(int c = 0; c < w;c++){
			for(int r = 0; r < h;r++){
				for(int k = 0; k < 3;k++){
					//					Particle p = particles[c][r][k];
					Particle p = particles[c][r][k];
					if(k == switcher){
						double distX = p.x - p.targetX;
						double distY = p.y - p.targetY;
						if((int)p.x != p.targetX){
							p.x -= Math.signum(distX);
						}else{
							p.y -= Math.signum(distY);
						}
						boolean xGood = p.x == p.targetX;
						boolean yGood = p.y == p.targetY;
						if(!xGood || !yGood){
							flag = false;
						}
						if(xGood && yGood){
							count++;
						}
					}if((p.x >= 0) && (p.x < w) && (p.y >= 0) && (p.y < h) && (k <= switcher)){
						cells[(int)p.x][(int)p.y] = cells[(int)p.x][(int)p.y] | p.RGB;
					}
					//					System.out.println(k + " " + Integer.toBinaryString(p.RGB));
				}
				//				double dist = getDistance(p.x, p.y, p.targetX, p.targetY);
				//				double deltaX = (p.x - p.targetX) / dist;
				//				double deltaY = (p.y - p.targetY) / dist;
				//				p.vX -= deltaX / 30;
				//				p.vY -= deltaY / 30;
				//
				//				if(dist < 0.4){
				//					count++;
				//					double fX = Math.floor(p.x) - p.targetX;
				//					double fY = Math.floor(p.x) - p.targetX;
				//					if(fX == 0){
				//						p.x = p.targetX;
				//						p.vX = 0;
				//					}
				//					else{
				//						flag = false;
				//					}
				//					if(fY == 0){
				//						p.y = p.targetY;
				//						p.vY = 0;
				//					}
				//					else{
				//						flag = false;
				//					}
				//					p.vX -= p.vX / 30;
				//					p.vY -= p.vY / 30;
				//				}
				//				else{
				//					flag = false;
				//					p.vX -= p.vX / 50;
				//					p.vY -= p.vY / 50;
				//				}
				//				p.x += p.vX;
				//				p.y += p.vY;
			}
		}
		if(count == (w * h)){
			switcher++;
			if(switcher == 3) finished = true;
		}
		ETA = (double)count / (double)(w*h) / (3 - switcher);
		//		finished = flag;
		long t1 = System.nanoTime();
		int t = renderTicks % 3;
		if(t == 1){
			GraphicsConfiguration gc = GraphicsEnvironment.
					getLocalGraphicsEnvironment().getDefaultScreenDevice().
					getDefaultConfiguration();
			BufferedImage bNew = gc.createCompatibleImage(w, h);
			for(int c = 0; c < w;c++){
				for(int r = 0; r < h;r++){
					//					Particle p = particles[c][r];
					//					if(p.x > 0 && p.x < w && p.y > 0 && p.y < h){
					//											bNew.setRGB((int)Math.floor(p.x), (int)Math.floor(p.y), p.RGB);
					//					}
					bNew.setRGB(c,r,cells[c][r]);
					cells[c][r] = 0;
				}
			}
			imageNum++;
			try {
				if(!quit){
					ImageIO.write(bNew, "png", new File(p.toAbsolutePath().toString() + "/" + imageNum));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//						images.add(bNew);
		}
		long t2 = System.nanoTime();
//		System.out.print(((t1 - t0) / 1000000) + " ");
//		System.out.println(((t2 - t1) / 1000000) + " ");
	}
	long t0 = System.nanoTime();
	double ETA = 0;
	private static DecimalFormat df2 = new DecimalFormat("0.00000");
	public void paint(Graphics g){
		super.paint(g);
		long t1 = System.nanoTime();
		if(finished){
			try {
				g.drawImage(ImageIO.read(new File(p.toAbsolutePath().toString() + "/" + (ticks + 1))), 0, 0, null);
				if(((t1 - t0)/2) > 10000000){
					t0 = t1;
					ticks++;
					if(ticks > (imageNum - 1)) ticks = 0;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			//
		}
		else{
			g.drawString(df2.format(ETA * 100) +" % Complete...", getWidth()/2, getHeight()/2);
		}
		//		long t0 = System.nanoTime();
		//		GraphicsConfiguration gc = GraphicsEnvironment.
		//				getLocalGraphicsEnvironment().getDefaultScreenDevice().
		//				getDefaultConfiguration();
		//		VolatileImage retVal = gc.createCompatibleVolatileImage(w, h);
		//		Graphics gV = retVal.createGraphics();
		//		for(int c = 0; c < w;c++){
		//			for(int r = 0; r < h;r++){
		//				Particle p = particles[c][r];
		//				gV.setColor(p.RGB);
		//				gV.fillRect((int)Math.floor(p.x), (int)Math.floor(p.y), 1, 1);
		//			}
		//		}
		//		long t1 = System.nanoTime();
		//		g.drawImage(retVal, 0, 0, null);
		//		retVal.flush();
	}
	private static URI getJarURI()
			throws URISyntaxException
	{
		final ProtectionDomain domain;
		final CodeSource       source;
		final URL              url;
		final URI              uri;

		domain = ImageFormation.class.getProtectionDomain();
		source = domain.getCodeSource();
		url    = source.getLocation();
		uri    = url.toURI();

		return (uri);
	}

	private static URI getFile(final URI    where,
			final String fileName)
					throws ZipException,
					IOException
	{
		final File location;
		final URI  fileURI;

		location = new File(where);

		// not in a JAR, just return the path on disk
		if(location.isDirectory())
		{
			fileURI = URI.create(where.toString() + fileName);
		}
		else
		{
			final ZipFile zipFile;
			zipFile = new ZipFile(location);
			try{
				fileURI = extract(zipFile, fileName);
			}
			finally{
				zipFile.close();
			}
		}

		return (fileURI);
	}

	private static URI extract(final ZipFile zipFile,
			final String  fileName)
					throws IOException
	{
		final File         tempFile;
		final ZipEntry     entry;
		final InputStream  zipStream;
		OutputStream       fileStream;

		tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
		tempFile.deleteOnExit();
		entry    = zipFile.getEntry(fileName);

		if(entry == null)
		{
			throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
		}
		zipStream  = zipFile.getInputStream(entry);
		fileStream = null;
		try
		{
			final byte[] buf;
			int          i;

			fileStream = new FileOutputStream(tempFile);
			buf        = new byte[1024];
			i          = 0;

			while((i = zipStream.read(buf)) != -1)
			{
				fileStream.write(buf, 0, i);
			}
		}
		finally
		{
			close(zipStream);
			close(fileStream);
		}

		return (tempFile.toURI());
	}

	private static void close(final Closeable stream)
	{
		if(stream != null)
		{
			try
			{
				stream.close();
			}
			catch(final IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
}

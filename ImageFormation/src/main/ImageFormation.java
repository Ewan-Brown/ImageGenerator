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
				int z = rand.nextInt(3);
				int z2 = rand.nextInt(2);
				//				particles[x][y][0] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,red << 16);
				//				particles[x][y][1] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,green << 8);
				//				particles[x][y][2] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,blue);
				particles[x][y][0] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,red << 16);
				particles[x][y][1] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,green << 8);
				particles[x][y][2] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,blue);
				//				particles[x][y][z % 3] = new Particle(0,rand.nextInt(h),x,y,red << 16);
				//				particles[x][y][(z+1) % 3] = new Particle(rand.nextInt(w),0,x,y,green << 8);
				//				particles[x][y][(z+2) % 3] = new Particle(0,rand.nextInt(h),x,y,blue);
				//						particles[x][y][0] = new Particle(0,rand.nextInt(h),x,y,red << 16);
				//						particles[x][y][1] = new Particle(w,rand.nextInt(h),x,y,green << 8);
				//						particles[x][y][2] = new Particle(0,rand.nextInt(h),x,y,blue);
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
		int count = 0;
		long t0 = System.nanoTime();
		for(int c = 0; c < w;c++){
			for(int r = 0; r < h;r++){
				for(int k = 0; k < 3;k++){
					Particle p = particles[c][r][k];
					//					if(k == switcher){
					double distX = p.x - p.targetX;
					double distY = p.y - p.targetY;
					double distT = getDistance(p.x, p.y, p.targetX, p.targetY);
					if(distT > 3){
						p.x -= distX / 40;
						p.y -= distY / 40;
					}
					else{
						p.x = p.targetX;
						p.y = p.targetY;
					}
					boolean xGood = p.x == p.targetX;
					boolean yGood = p.y == p.targetY;
					if(!xGood || !yGood){
					}
					if(xGood && yGood){
						count++;
						//						}
						//					}if((p.x >= 0) && (p.x < w) && (p.y >= 0) && (p.y < h) && (k <= switcher)){
					}if((p.x >= 0) && (p.x < w) && (p.y >= 0) && (p.y < h)){
						cells[(int)p.x][(int)p.y] = cells[(int)p.x][(int)p.y] | p.RGB;
					}
				}
			}
		}
		if(count == (w * h) * 3){
			finished = true;
			//			switcher++;
			//			if(switcher == 3) finished = true;
		}
		ETA = ((double)count / (double)(w*h) / 3) + (0.333*switcher);
		int t = renderTicks % 3;
		if(t == 1){
			long t1 = System.nanoTime();
			GraphicsConfiguration gc = GraphicsEnvironment.
					getLocalGraphicsEnvironment().getDefaultScreenDevice().
					getDefaultConfiguration();
			BufferedImage bNew = gc.createCompatibleImage(w, h);
			for(int c = 0; c < w;c++){
				for(int r = 0; r < h;r++){
					//					}
					bNew.setRGB(c,r,cells[c][r]);
					cells[c][r] = 0;
				}
			}
			long t2 = System.nanoTime();
			imageNum++;
			try {
				if(!quit){
					ImageIO.write(bNew, "png", new File(p.toAbsolutePath().toString() + "/" + imageNum));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//						images.add(bNew);
			long t3 = System.nanoTime();
			System.out.print(((t1 - t0) / 1000000) + " ");
			System.out.print(((t2 - t1) / 1000000) + " ");
			System.out.println(((t3 - t2) / 1000000) + " ");
		}

	}
	long t0 = System.nanoTime();
	double ETA = 0;
	private static DecimalFormat df2 = new DecimalFormat("0.00000");
	public void paint(Graphics g){
		super.paint(g);
		long t1 = System.nanoTime();
		if(finished){
			try {
				//				g.drawImage(ImageIO.read(new File(p.toAbsolutePath().toString() + "/" + (imageNum-1 - ticks))), 0, 0, null);
				g.drawImage(ImageIO.read(new File(p.toAbsolutePath().toString() + "/" + (ticks + 1))), 0, 0, null);
				if(((t1 - t0)/2) > 10000000){
					t0 = t1;
					ticks++;
					if(ticks == imageNum - 1) ticks = 0;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			//
		}
		else{
			g.drawString(df2.format(ETA * 100) +" % Complete...", getWidth()/2, getHeight()/2);
		}
	}
}

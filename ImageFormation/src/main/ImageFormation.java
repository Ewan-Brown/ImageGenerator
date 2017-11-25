package main;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageFormation extends JPanel{
	private static final long serialVersionUID = 1L;
	static BufferedImage i;
	static int w = 0;
	static int h = 0;
	static Particle[][] particles;
	static Random rand = new Random();
	public static void main(String args[]){
		System.setProperty("sun.java2d.opengl","True");
		FileDialog fd = new FileDialog((java.awt.Frame) null);
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
		particles = new Particle[w][h];
		for(int x = 0; x < w;x++){
			for(int y = 0; y < h;y++){
				particles[x][y] = new Particle(rand.nextInt(w),rand.nextInt(h),x,y,i.getRGB(x, y));
			}
		}
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageFormation m = new ImageFormation();
		frame.add(m);
		frame.setSize(w,h);
		frame.setVisible(true);
		frame.setFocusable(true);
		while(true){
			long t0 = System.nanoTime();
			m.repaint();
			long t1 = System.nanoTime();
			m.update();
			long t2 = System.nanoTime();
//			System.out.print((t1 - t0) / 1000000 + " ");
//			System.out.println((t2 - t1) / 1000000);
		}
	}
	public static double getDistance( double x1,  double y1,  double x2,  double y2) {
		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
	}
	public void update(){
		for(int c = 0; c < w;c++){
			for(int r = 0; r < h;r++){
				Particle p = particles[c][r];
				double dist = getDistance(p.x, p.y, p.targetX, p.targetY); 
				double deltaX = (p.x - p.targetX) / dist;
				double deltaY = (p.y - p.targetY) / dist;
				p.vX -= deltaX / 30;
				p.vY -= deltaY / 30;
				
				if(dist < 1){
					double fX = Math.floor(p.x) - p.targetX;
					double fY = Math.floor(p.x) - p.targetX;
					if(fX == 0){
						p.x = p.targetX;
						p.vX = 0;
					}
					if(fY == 0){
						p.y = p.targetY;
						p.vY = 0;
					}
					p.vX -= p.vX / 2;
					p.vY -= p.vY / 2;
				}
				else{
					p.vX -= p.vX / 50;
					p.vY -= p.vY / 50;
				}
				p.x += p.vX;
				p.y += p.vY;
			}
		}
	}
	public void paint(Graphics g){
		super.paint(g);
		GraphicsConfiguration gc = GraphicsEnvironment.
				getLocalGraphicsEnvironment().getDefaultScreenDevice().
				getDefaultConfiguration();
		VolatileImage retVal = gc.createCompatibleVolatileImage(w, h);
		Graphics gV = retVal.createGraphics();
		for(int c = 0; c < w;c++){
			for(int r = 0; r < h;r++){
				Particle p = particles[c][r];
				gV.setColor(p.RGB);
				gV.fillRect((int)Math.floor(p.x), (int)Math.floor(p.y), 1, 1);
			}
		}
		g.drawImage(retVal, 0, 0, null);
	}

}

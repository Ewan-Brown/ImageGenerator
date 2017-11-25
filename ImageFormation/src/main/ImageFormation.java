package main;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageFormation extends JPanel{
	static BufferedImage i;
	static int w = 0;
	static int h = 0;
	static Particle[][] particles;
	static Random rand = new Random();
	public static void main(String args[]){
//		System.setProperty("sun.java2d.opengl","True");
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
			m.repaint();
			m.update();
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
				p.vX -= deltaX / 10;
				p.vY -= deltaY / 10;
				p.x += p.vX;
				p.y += p.vY;
				p.vX -= p.vX / 100;
				p.vY -= p.vY / 100;
			}
		}
	}
	public void paint(Graphics g){
		super.paint(g);
//		System.out.println("hey");
		for(int c = 0; c < w;c++){
			for(int r = 0; r < h;r++){
				Particle p = particles[c][r];
				g.setColor(p.RGB);
				g.fillRect((int)p.x, (int)p.y, 1, 1);
			}
		}
	}

}

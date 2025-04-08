package com.lucasj.PhysicsSimulation.UI;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import com.lucasj.PhysicsSimulation.Simulation.Simulation;

public class Window extends Canvas implements Runnable {

	private static final long serialVersionUID = -3867207337097595564L;
	
	private JFrame frame;
	private Dimension resolution;
	
	private boolean isRunning;
	private int fpslimit = 260; // Limits frames per second to 60
	private int currentFPS;
	
	private Thread thread;
	
	private Simulation simulation;
	
	public Window() {
		frame = new JFrame();
		// Default window size 1920x1080
		resolution = new Dimension(2000, 1080);
		setPreferredSize(resolution);
		frame.setMinimumSize(resolution);
		frame.add(this);
		
		simulation = new Simulation(this);
		
		frame.pack();
		frame.setName("Physics Simulation - Lucas Johnson");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
        if (isRunning) return;
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }
	
	public synchronized void stop() {
		if(!isRunning) return;
		isRunning = false;
		try {
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
        isRunning = true;
        double timePerFrame = 1000000000 / fpslimit;
        long lastFrame = System.nanoTime();
        long now = System.nanoTime();
        
        int frames = 0;
        long lastCheck = System.currentTimeMillis();
        
        while (true ) {
            timePerFrame = 1000000000 / fpslimit;
        	
        	now = System.nanoTime();
    		double deltaTime = (now - lastFrame) / 1_000_000_000.0;
        	if (now - lastFrame >= timePerFrame) {
        		
        		update(deltaTime);
        		render();
        		
        		lastFrame = now;
        		frames++;
        		
        	}
        	
        	if (System.currentTimeMillis() - lastCheck >= 1000) {
        		lastCheck = System.currentTimeMillis();
        		currentFPS = frames;
        		frames = 0;
        	}
        	
        }
    }
	
	private void update(double deltaTime) {
		frame.setTitle("Physics Simulation - Lucas Johnson          FPS: " + currentFPS);
		
		simulation.update(deltaTime);
	}
	
	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setStroke(new BasicStroke(3));
		
		//Background
		this.paintVignette(g, this.getWidth(), this.getHeight());
		
		simulation.render(g2d);
		
		g.dispose();
		bs.show();
	}
	
	// just had chatgpt do this real quick for a good visual effect
	public void paintVignette(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable anti-aliasing for smoother gradient rendering.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define the center of the gradient and calculate the radius (diagonal distance for full coverage).
        float centerX = width / 2f;
        float centerY = height / 2f;
        float radius = (float) Math.hypot(centerX, centerY);

        // Define the gradient fractions and colors.
        // At fraction 0 (center): DARK_GRAY; at fraction 1 (edge): BLACK.
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {Color.DARK_GRAY.darker().darker(), Color.BLACK};

        // Create the radial gradient paint.
        RadialGradientPaint gradient = new RadialGradientPaint(new Point2D.Float(centerX, centerY), radius, dist, colors);
        g2d.setPaint(gradient);

        // Fill the entire component with the gradient.
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
    }
	
	public Dimension getResolution() {
		return this.resolution;
	}

	public JFrame getFrame() {
		return frame;
	}

}

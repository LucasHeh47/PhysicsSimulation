package com.lucasj.PhysicsSimulation.UI;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
		resolution = new Dimension(1080, 1080);
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
		g2d.setColor(Color.gray);
		g2d.fillRect(0, 0, this.resolution.width, this.resolution.height);
		
		simulation.render(g2d);
		
		g.dispose();
		bs.show();
	}
	
	public Dimension getResolution() {
		return this.resolution;
	}

	public JFrame getFrame() {
		return frame;
	}

}

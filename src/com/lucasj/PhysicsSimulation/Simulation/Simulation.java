package com.lucasj.PhysicsSimulation.Simulation;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lucasj.PhysicsSimulation.Math.Vector2D;
import com.lucasj.PhysicsSimulation.UI.Window;

public class Simulation {
	private Window window;
	
	private List<Particle> particles;
	
	public static final float ACCELERATION_GRAVITY = 9.81f;
	
	// Fun constant to speed things up
	public static final float SPEED_MULTIPLIER = 2.0f;
	
	public Simulation(Window window) {
		this.window = window;
		
		particles = new ArrayList<Particle>();
		populateParticles(10);
	}
	
	public void update(double deltaTime) {
		for(Particle particle: particles) {
			particle.update(deltaTime);
		}
	}
	
	public void render(Graphics2D g2d) {
		for(Particle particle: particles) {
			particle.render(g2d);
		}
	}
	
	public void populateParticles(int amount) {
		Random rand = new Random();
		for (int i = 0; i <= amount; i++) {
			// Creates a new particle at a random location on the window
			Particle particle = new Particle(this, new Vector2D(rand.nextInt((int)window.getResolution().getWidth()), rand.nextInt((int)window.getResolution().getHeight())));
			particles.add(particle);
		}
	}
	
	public Dimension getResolution() {
		return window.getResolution();
	}
	
}

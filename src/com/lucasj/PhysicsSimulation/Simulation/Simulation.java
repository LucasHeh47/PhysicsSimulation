package com.lucasj.PhysicsSimulation.Simulation;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lucasj.PhysicsSimulation.Input.MouseHandler;
import com.lucasj.PhysicsSimulation.Math.Quadtree;
import com.lucasj.PhysicsSimulation.Math.Rectangle;
import com.lucasj.PhysicsSimulation.Math.Vector2D;
import com.lucasj.PhysicsSimulation.UI.Window;

public class Simulation {
	private Window window;
	
	public int population = 1000;
	
	// Instead of using an ArrayList for computations or a grid structure, i will use a Quadtree structure
	private List<Particle> particles;
	private Quadtree quadtree;
	
	//final int cellSize = 64;
	
	//private Map<Point, List<Particle>> grid;
	
	public static final int DEFAULT_PARTICLE_SIZE = 12;
	public static final float ACCELERATION_GRAVITY = 9.81f;
	public static final float ELASTICITY = 1f; // (No energy loss) 0 - 1 (immediate energy loss)
	public static final float FRICTION = 0.1f;
	
	// Fun constant to speed things up
	public static final float SPEED_MULTIPLIER = 1.0f;
	
	private Vector2D inputForce;
	private MouseHandler mouseHandler;
	
	private double deltaTime;
	
	public Simulation(Window window) {
		this.window = window;
		
		particles = new ArrayList<Particle>();
		populateParticles(population);
		
		this.setInputForce(new Vector2D(-1, -1));
		mouseHandler = new MouseHandler(this);
		window.addMouseMotionListener(mouseHandler);
		window.addMouseListener(mouseHandler);
		
		//grid = new HashMap<>();
	}
	
	public void update(double deltaTime) {
		
		this.deltaTime = deltaTime;
		
		Dimension res = window.getResolution();
		
		Vector2D center = new Vector2D(res.width /2.0, res.getHeight() / 2.0);
		Rectangle boundary = new Rectangle(center, res.width / 2.0, res.height / 2.0);
		int capacity = 4;
		quadtree = new Quadtree(this, boundary, capacity);
		
		for (Particle p : particles) {
			p.update(deltaTime);
			quadtree.insert(p);
		}
		
		for (Particle p : particles) {
			double range = p.getSize();
			Rectangle rangeRect = new Rectangle(p.getLocation(), range, range);
			List<Particle> candidates = quadtree.query(rangeRect);
			
			for(Particle other : candidates) {
				if(p != other) {
					double dist = p.getLocation().distanceTo(other.getLocation());
					double minDist = (p.getSize() + other.getSize()) / 2.0;
					if(dist * dist < minDist * minDist) {
						p.checkCollision(other);
					}
				}
			}
		}
	}
	
	public void render(Graphics2D g2d) {
		quadtree.render(g2d);
		for(Particle particle: particles) {
			particle.render(g2d);
		}
		mouseHandler.render(g2d);
	}
	
	public void populateParticles(int amount) {
		Random rand = new Random();
		for (int i = 0; i <= amount; i++) {
			// Creates a new particle at a random location on the window
			Particle particle = new Particle(this, new Vector2D(rand.nextInt((int)window.getResolution().getWidth()), rand.nextInt((int)window.getResolution().getHeight())));
			particle.setVelocity(new Vector2D(rand.nextInt(0, 100), 0));
			particle.setId(i);
			particles.add(particle);
		}
	}
	
	public Dimension getResolution() {
		return window.getResolution();
	}

	public Vector2D getInputForce() {
		return inputForce;
	}

	public void setInputForce(Vector2D inputForce) {
		this.inputForce = inputForce;
	}

	public double getDeltaTime() {
		return deltaTime;
	}

	public boolean addParticle(Particle e) {
		return particles.add(e);
	}
	
	
	
}

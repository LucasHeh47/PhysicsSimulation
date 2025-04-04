package com.lucasj.PhysicsSimulation.Simulation;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.lucasj.PhysicsSimulation.Input.MouseHandler;
import com.lucasj.PhysicsSimulation.Math.Vector2D;
import com.lucasj.PhysicsSimulation.UI.Window;

public class Simulation {
	private Window window;
	
	public int population = 200;
	
	// Instead of using an ArrayList for computations, i will use a Quadtree structure
	private List<Particle> particles;
	final int cellSize = 64;
	
	private Map<Point, List<Particle>> grid;
	
	public static final float ACCELERATION_GRAVITY = 9.81f;
	public static final float ELASTICITY = 0.1f; // (No energy loss) 0 - 1 (immediate energy loss)
	public static final float FRICTION = 0.1f;
	
	// Fun constant to speed things up
	public static final float SPEED_MULTIPLIER = 5.0f;
	
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
		grid = new HashMap<>();
	}
	
	public void update(double deltaTime) {
		
		grid.clear();
		this.deltaTime = deltaTime;
		
		// grid data structure with cell coordinates to compute particles near each other
		for (Particle p : particles) {
			p.update(deltaTime);
		    int cellX = (int) (p.getLocation().getX() / cellSize);
		    int cellY = (int) (p.getLocation().getY() / cellSize);
		    Point cell = new Point(cellX, cellY);
		    
		    grid.computeIfAbsent(cell, k -> new ArrayList<>()).add(p);
		}
		
		// each cell; check collisions only within the cell and cells bordering
		for (Map.Entry<Point, List<Particle>> entry : grid.entrySet()) {
	        Point cell = entry.getKey();
	        List<Particle> cellParticles = entry.getValue();
	        
	        // Check collisions within the cell
	        checkCollisions(cellParticles);
	        
	        // Only check 4 of the 8 adjacent cells to avoid duplicate checks
	        // (right, bottom, bottom-right, bottom-left)
	        Point[] neighborsToCheck = {
	            new Point(cell.x + 1, cell.y),     // right
	            new Point(cell.x, cell.y + 1),     // bottom
	            new Point(cell.x + 1, cell.y + 1),  // bottom-right
	            new Point(cell.x - 1, cell.y + 1)   // bottom-left
	        };
	        
	        for (Point neighbor : neighborsToCheck) {
	            List<Particle> neighborParticles = grid.get(neighbor);
	            if (neighborParticles != null) {
	                checkCollisionsBetween(cellParticles, neighborParticles);
	            }
	        }
	    }
	}
	
	private void checkCollisions(List<Particle> particles) {
	    int n = particles.size();
	    for (int i = 0; i < n; i++) {
	        Particle p1 = particles.get(i);
	        for (int j = i + 1; j < n; j++) {
	            Particle p2 = particles.get(j);
	            // Add distance check first for early rejection
	            double dist = p1.getLocation().distanceTo(p2.getLocation());
	            double minDist = (p1.getSize() + p2.getSize()) / 2.0;
	            if (dist * dist < minDist * minDist) {
	                p1.checkCollision(p2);
	            }
	        }
	    }
	}
	
	private void checkCollisionsBetween(List<Particle> list1, List<Particle> list2) {
	    for (Particle p1 : list1) {
	        for (Particle p2 : list2) {
	            // Add distance check first for early rejection
	            double dist = p1.getLocation().distanceTo(p2.getLocation());
	            double minDist = (p1.getSize() + p2.getSize()) / 2.0;
	            if (dist * dist < minDist * minDist) {
	                p1.checkCollision(p2);
	            }
	        }
	    }
	}
	
	public void render(Graphics2D g2d) {
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

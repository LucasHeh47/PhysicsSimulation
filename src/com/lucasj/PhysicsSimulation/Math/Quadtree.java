package com.lucasj.PhysicsSimulation.Math;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.PhysicsSimulation.Simulation.Particle;
import com.lucasj.PhysicsSimulation.Simulation.Simulation;

public class Quadtree {
	
	private Simulation sim;
	
	private Rectangle boundary;
	
	private int capacity;
	private List<Particle> particles;
	private boolean subdivided;
	
    // Child nodes
    private Quadtree northEast;
    private Quadtree northWest;
    private Quadtree southEast;
    private Quadtree southWest;

	public Quadtree(Simulation sim, Rectangle boundary, int capacity) {
		this.sim = sim;
		this.boundary = boundary;
		this.capacity = capacity;
		this.particles = new ArrayList<>();
		this.subdivided = false;
	}
	
	public boolean insert(Particle particle) {
		
		if (!boundary.contains(particle)) {
            return false;
        }
		
		if(particles.size() < capacity) {
			particles.add(particle);
			return true;
		} else {
			if(!subdivided) {
				subdivide();
			}
			if(northEast.insert(particle)) return true;
			if(northWest.insert(particle)) return true;
			if(southEast.insert(particle)) return true;
			if(southWest.insert(particle)) return true;
		}
		return false;
	}
	
	private void subdivide() {
		double x = boundary.center.getX();
		double y = boundary.center.getY();
		double hw = boundary.halfWidth;
		double hh = boundary.halfHeight;
		
		Rectangle ne = new Rectangle(new Vector2D(x + hw/2, y-hh/2), hw/2, hh/2);
		Rectangle nw = new Rectangle(new Vector2D(x - hw/2, y-hh/2), hw/2, hh/2);
		Rectangle se = new Rectangle(new Vector2D(x + hw/2, y+hh/2), hw/2, hh/2);
		Rectangle sw = new Rectangle(new Vector2D(x - hw/2, y+hh/2), hw/2, hh/2);
		
		northEast = new Quadtree(sim, ne, capacity);
		northWest = new Quadtree(sim, nw, capacity);
		southEast = new Quadtree(sim, se, capacity);
		southWest = new Quadtree(sim, sw, capacity);
		
		subdivided = true;
	}
	
	// Query all points within boundary
	public List<Particle> query(Rectangle range) {
		List<Particle> found = new ArrayList<>();
		
		if(!boundary.intersects(range)) {
			return found;
		}
		
		for(Particle p : particles) {
			if(range.contains(p)) {
				found.add(p);
			}
		}
		
		if(subdivided) {
			found.addAll(northEast.query(range));
			found.addAll(southEast.query(range));
			found.addAll(northWest.query(range));
			found.addAll(southWest.query(range));
		}
		
		return found;
	}
	
	public void render(Graphics2D g2d) {
		int x = (int) boundary.center.getX();
		int y = (int) boundary.center.getY();
		int hw = (int) boundary.halfWidth;
		int hh = (int) boundary.halfHeight;
		
		if(subdivided) {
			northEast.render(g2d);
			southEast.render(g2d);
			northWest.render(g2d);
			southWest.render(g2d);
		} else {
			g2d.setColor(Color.black);
			g2d.drawRect(x - hw, y-hw, hw*2, hh*2);
		}
	}
}

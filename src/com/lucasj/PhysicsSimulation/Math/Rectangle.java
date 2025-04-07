package com.lucasj.PhysicsSimulation.Math;

import com.lucasj.PhysicsSimulation.Simulation.Particle;

public class Rectangle {
    
	public Vector2D center;
    public double halfWidth;
    public double halfHeight;
    
    public Rectangle(Vector2D center, double halfWidth, double halfHeight) {
        this.center = center;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
    }
    
    // Returns true if the given particle is inside this rectangle.
    public boolean contains(Particle particle) {
        return (particle.getLocation().getX() >= center.getX() - halfWidth &&
        		particle.getLocation().getX() <= center.getX() + halfWidth &&
        		particle.getLocation().getY() >= center.getY() - halfHeight &&
        		particle.getLocation().getY() <= center.getY() + halfHeight);
    }
    
    // Returns true if this rectangle intersects with another rectangle.
    public boolean intersects(Rectangle other) {
        return !(other.center.getX() - other.halfWidth > center.getX() + halfWidth ||
                 other.center.getX() + other.halfWidth < center.getX() - halfWidth ||
                 other.center.getY() - other.halfHeight > center.getY() + halfHeight ||
                 other.center.getY() + other.halfHeight < center.getY() - halfHeight);
    }
}
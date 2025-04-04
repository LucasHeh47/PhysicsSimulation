package com.lucasj.PhysicsSimulation.Simulation;

import java.awt.Color;
import java.awt.Graphics2D;

import com.lucasj.PhysicsSimulation.Debug;
import com.lucasj.PhysicsSimulation.Math.Vector2D;

public class Particle {

	private Simulation sim;
	
	private long id;
	
	private Vector2D location;
	private Vector2D velocity;
	private Vector2D acceleration;
	
	private final int floor;
	private final int right_wall;
	
	private int size = 32;
	
	private boolean onGround = false;
	
	private boolean isUserControlled = false; // Only needed for mouse force
	
	public Particle(Simulation sim, Vector2D loc) {
		this.sim = sim;
		floor = sim.getResolution().height;
		right_wall = sim.getResolution().width;
		this.location = loc;
		this.velocity = new Vector2D(0);
		this.acceleration = new Vector2D(0);
	}
	
	public Particle(Simulation sim, Vector2D loc, int size) {
		this.sim = sim;
		floor = sim.getResolution().height;
		right_wall = sim.getResolution().width;
		this.location = loc;
		this.velocity = new Vector2D(0);
		this.acceleration = new Vector2D(0);
		this.size = size;
	}
	
	public void update(double deltaTime) {
		if(isUserControlled) return;
//		Debug.Log(this, "Location: " + this.location);
//		Debug.Log(this, "Velocity: " + this.velocity);
//		Debug.Log(this, "Acceleration: " + this.acceleration);
		applyGravity();
		if(!onGround) {
			this.velocity = this.velocity.add(acceleration);
		} else {
			this.velocity = Vector2D.zero();
			applyFriction();
			//Debug.Log(this, "GROUNDED: " + this.location);
			return;
		}
		this.location = this.location.add(velocity.multiply(deltaTime * Simulation.SPEED_MULTIPLIER));
		
		// bounce back after hitting bottom
		if(location.getYint() + size >= floor) {
		    location.setY(floor - size);
		    applyFriction();
			this.velocity.setY(this.velocity.getY() * -1 + Simulation.ELASTICITY);
		}
		// bounce downward when hitting ceiling
		if(location.getYint() <= 0) {
			location.setY(0+size);
			this.velocity.setY(this.velocity.getY() * -1);
		}
		// bounce off walls
		if(location.getXint() <= 0) {
			location.setX(0);
			this.velocity.setX(this.velocity.getX() * -1);
		}
		if(location.getXint() >= right_wall-size) {
			location.setX(right_wall-size);
			this.velocity.setX(this.velocity.getX() * -1);
		}
	}
	
	public void render(Graphics2D g2d) {
		g2d.setColor(Color.blue);
		if(isUserControlled) {
			g2d.setColor(new Color(0, 0, 255, 30));
		}
		g2d.fillOval(location.getXint(), location.getYint(), size, size);
	}
	
	private void applyGravity() {
		this.velocity = this.velocity.add(new Vector2D(0, Simulation.ACCELERATION_GRAVITY));
	}
	
	private void applyFriction() {
		this.velocity.setX(this.velocity.getX() * (1-Simulation.FRICTION));
	}
	
	public void checkCollision(Particle other) {
	    // Avoid self-collision
	    if (this == other) return;

	    Vector2D locDiff = other.getLocation().subtract(this.location);
	    double distance = locDiff.magnitude();
	    double minDistance = this.size/2.0 + other.size/2.0;

	    // Only process collision if particles are overlapping
	    if (distance < minDistance && distance > 0.0001) {
	        // Normalized collision normal (points from this to other)
	        Vector2D collisionNormal = locDiff.divide(distance);

	        // Separate the particles (push them apart)
	        double overlap = minDistance - distance;
	        Vector2D separation = collisionNormal.multiply(overlap * 0.5);
	        this.location = this.location.subtract(separation);
	        other.location = other.location.add(separation);
	        
	        
	        // Swap velocities with elasticity penalty
	        /* Note: Over simplified. Later can be changed from a simple velocity swap
	         * to a impulse calculation such as
	         * j = (-1(1 + e) * (Vrel * n)) / ((1/m1) + (1/m2))
	         * with m being the size of the object for now (maybe a color later on)
	         */
	       
	        // V1:
	        
	        Vector2D tempVel = this.velocity;
	        this.velocity = other.velocity.multiply(1-Simulation.ELASTICITY);
	        other.velocity = tempVel.multiply(1-Simulation.ELASTICITY);
	        
	        // V2: ( Something aint right and idk what )
	       
//	        // Masses
//	        double m1 = this.size;
//	        double m2 = other.getSize();
//	        
//	        // Vrel (Relative Velocity)
//	        Vector2D relativeVelocity = this.velocity.subtract(other.getVelocity());
//	        double velocityAmongNormal = relativeVelocity.dot(collisionNormal);
//	        
//	        // Only apply impulse if particles are moving towards each other
//	        if (velocityAmongNormal > 0) return;
//	        
//	        // e (elasticity)
//	        double e = Simulation.ELASTICITY;
//	        // impulse scalar using formula above
//	        double j = -(1 + e) * velocityAmongNormal / ((1 / m1) + (1 / m2));
//	        j *= 1.2;
//	        // impulse vector
//	        Vector2D impulse = collisionNormal.multiply(j);
//	        this.velocity = this.velocity.add(impulse.divide(m1));
//	        other.velocity = other.velocity.subtract(impulse.divide(m2));
	    }
	}
	
	public Vector2D getLocation() {
		return this.location;
	}

	public void setLocation(Vector2D location) {
		this.location = location;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	public Vector2D getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2D acceleration) {
		this.acceleration = acceleration;
	}
	
	public int getSize() {
		return this.size;
	}

	public boolean isUserControlled() {
		return isUserControlled;
	}

	public void setUserControlled(boolean isUserControlled) {
		this.isUserControlled = isUserControlled;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}

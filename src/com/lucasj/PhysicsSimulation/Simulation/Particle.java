package com.lucasj.PhysicsSimulation.Simulation;

import java.awt.Color;
import java.awt.Graphics2D;

import com.lucasj.PhysicsSimulation.Debug;
import com.lucasj.PhysicsSimulation.Math.Vector2D;
import com.lucasj.PhysicsSimulation.Utils.ColorInterpolation;

public class Particle {

	private Simulation sim;
	
	private long id;
	
	private Vector2D location;
	private Vector2D velocity;
	private Vector2D acceleration;
	
	private final int floor;
	private final int right_wall;
	
	private int size = Simulation.DEFAULT_PARTICLE_SIZE;
	private int mass = 10;
	
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
		if(isUserControlled) {
			this.velocity = Vector2D.zero();
			this.acceleration = Vector2D.zero();
			return;
		}
//		Debug.Log(this, "Location: " + this.location);
//		Debug.Log(this, "Velocity: " + this.velocity);
//		Debug.Log(this, "Acceleration: " + this.acceleration);
		if(Simulation.GRAVITY) applyGravity();
		if(!onGround) {
			this.velocity = this.velocity.add(acceleration);
		} else {
			applyFriction();
			//Debug.Log(this, "GROUNDED: " + this.location);
			return;
		}
		this.location = this.location.add(velocity.multiply(deltaTime * Simulation.SPEED_MULTIPLIER));
		
		// bounce back after hitting bottom
		if(location.getYint() + size >= floor) {
		    location.setY(floor - size);
		    applyFriction();
			this.velocity.setY(this.velocity.getY() * -1.0 * Simulation.ELASTICITY);
		}
		// bounce downward when hitting ceiling
		if(location.getYint() <= 0) {
			location.setY(1);
			this.velocity.setY(this.velocity.getY() * -1);
		}
		// bounce off walls
		if(location.getXint() <= 0) {
			location.setX(1);
			this.velocity.setX(this.velocity.getX() * -1);
		}
		if(location.getXint() >= right_wall-size) {
			location.setX(right_wall-size);
			this.velocity.setX(this.velocity.getX() * -1);
		}
	}
	
	public void render(Graphics2D g2d) {
		g2d.setColor(ColorInterpolation.getInterpolatedColor(mass));
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
	
	// Collision between different particles
	public void checkCollision(Particle other) {
	    // Avoid self-collision
	    if (this == other) return;

	    // Difference in location
	    // p2 - p1
	    Vector2D locDiff = other.getLocation().subtract(this.location);
	    // sqrt(x^2 + y^2)
	    double distance = locDiff.magnitude();
	    // Minimum distance to "collide" with x being diameter of particle
	    // x1/2.0 + x2/2.0
	    double minDistance = this.size/2.0 + other.size/2.0;

	    if (distance < minDistance && distance > 0.0001) {
	        // Normalized collision normal (points from this to other)
	        Vector2D collisionNormal = locDiff.divide(distance);

	        // Separate the particles (push them apart)
	        double overlap = minDistance - distance;
	        Vector2D separation = collisionNormal.multiply(overlap);
	        this.location = this.location.subtract(separation);
	        other.location = other.location.add(separation);

	        // Masses
	        double m1 = this.mass;
	        double m2 = other.getMass();
	        
	        double e = Simulation.ELASTICITY;
	        
	        /* 
	         * j = (-1(1 + e) * (Vrel) / ((1/m1) + (1/m2))
	         * with m being the size of the object for now (maybe a color later on)
	         */
	        
	        // Something aint right and idk what
//	        
	        // Vrel (Relative Velocity)
	        Vector2D relativeVelocity = this.velocity.subtract(other.getVelocity());
	        double velocityAmongNormal = relativeVelocity.dot(collisionNormal);
	        
	        // Only apply impulse if particles are moving towards each other
	        if (velocityAmongNormal <= 0) return;
	        
	        // impulse scalar using formula above
	        // Changing formula from 1+e to e seemed to help
	        double j = -((e) * velocityAmongNormal / ((1 / m1) + (1 / m2)));
	        // impulse vector
	        Vector2D impulse = collisionNormal.multiply(j);
	        
	        // Problem with colliding; particles were not bouncing off each other but rather sort of "going around each other"
	        // multiplying by -1 seemed to help
	        this.velocity = this.velocity.add(impulse.divide(m1)).multiply(-1);
	        other.velocity = other.velocity.subtract(impulse.divide(m2)).multiply(1);
	        Debug.Log(this, this.velocity.toString());
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

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}
	
}

package com.lucasj.PhysicsSimulation.Simulation;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.PhysicsSimulation.Debug;
import com.lucasj.PhysicsSimulation.Math.Rectangle;
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
	
	private List<Particle> neighbors;
	
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
//		Debug.Log(this, "Location: " + this.location);
//		Debug.Log(this, "Velocity: " + this.velocity);
//		Debug.Log(this, "Acceleration: " + this.acceleration);
		if(Simulation.GRAVITY) applyGravity();
		if(Simulation.BOIDS_FLOCKING_ALGORITHM) applyBoidsFlockingAlgorithm();
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
		g2d.fillOval(location.getXint(), location.getYint(), size, size);
	}
	
	private void applyBoidsFlockingAlgorithm() {
		// Particles are already being seperated upon collisions
		
		// If simulation is disabled, the neighbors list hasnt been created yet
		if(!Simulation.GRAVITY) {
			float radius = Simulation.CENTER_OF_MASS_RADIUS;
			Rectangle queryRect = new Rectangle(this.getLocation(), radius, radius);
			
			List<Particle> candidates = sim.getQuadtree().query(queryRect);
			
			List<Particle> particlesWithinRadius = new ArrayList<>();
			for (Particle p : candidates) {
				if(this.location.distanceTo(p.getLocation()) <= radius) {
					particlesWithinRadius.add(p);
				}
			}
			this.neighbors = particlesWithinRadius;
		}
		
		Vector2D alignment = Vector2D.zero();
		Vector2D cohesion = Vector2D.zero();
		
		int countAlignment = 0;
		for(Particle other : neighbors) {
			if(other == this) return;
			double distance = this.location.distanceTo(other.getLocation());
			
			alignment = alignment.add(other.velocity);
			cohesion = cohesion.add(other.location);
			
			countAlignment++;
		}
		
		if(countAlignment > 0) {
			alignment = alignment.multiply(1.0 / countAlignment);
			double magnitudeScaleFactor = 1000 / alignment.magnitude();
			alignment = alignment.multiply(magnitudeScaleFactor);
			
			alignment = alignment.subtract(this.velocity);
			
			cohesion = cohesion.multiply(1.0 / countAlignment);
			cohesion = cohesion.subtract(this.location);
			magnitudeScaleFactor = 1000 / cohesion.magnitude();
			cohesion = cohesion.multiply(magnitudeScaleFactor);
			cohesion = cohesion.subtract(this.velocity);
			
		}
		
		Vector2D accelerationForce = alignment.multiply(200).add(cohesion.multiply(100));
		
		applyForce(accelerationForce);
		
		
	}
	
	private void applyGravity() {
		this.velocity = this.velocity.add(new Vector2D(0, Simulation.ACCELERATION_GRAVITY_DOWNWARD * sim.getDeltaTime()));

		// Center of mass based off a radius of X from the particle
		float radius = Simulation.CENTER_OF_MASS_RADIUS;
		Rectangle queryRect = new Rectangle(this.getLocation(), radius, radius);
		
		List<Particle> candidates = sim.getQuadtree().query(queryRect);
		
		List<Particle> particlesWithinRadius = new ArrayList<>();
		for (Particle p : candidates) {
			if(this.location.distanceTo(p.getLocation()) <= radius) {
				particlesWithinRadius.add(p);
			}
		}
		this.neighbors = particlesWithinRadius;
		
		double totalMass = 0.0;
		Vector2D weightedPosition = Vector2D.zero();
		
		for (Particle p : particlesWithinRadius) {
			double mass = p.getMass();
			totalMass += mass;
			weightedPosition = weightedPosition.add(p.getLocation().multiply(mass));
		}
		
		if(totalMass == 0.0) {
			return;
		}
		
		Vector2D localCenterOfMass = weightedPosition.multiply(1.0 / totalMass);
		
		//Vector2D direction = this.location.subtract(localCenterOfMass);
		Vector2D direction = localCenterOfMass.subtract(this.location);
		
		if(direction.magnitude() > 0) {
			Vector2D pullDirection = direction.normalize();
			double pullStrength = Simulation.ACCELERATION_GRAVITY_TOWARD_LARGER_MASSES;
			Vector2D pullForce = pullDirection.multiply(pullStrength);
			this.velocity = this.velocity.add(pullForce);
		}
		
		
		// User controlled
		if(sim.getInputForce().getX() != -1) {
			if(sim.getMouseHandler().pullOrPush) {
				direction = sim.getInputForce().subtract(this.location);
			} else {
				direction = this.location.subtract(sim.getInputForce());
			}
			Vector2D pullDirection = direction.normalize();
			double pullStrength = Simulation.USER_CONTROLLED_POWER;
			Vector2D pullForce = pullDirection.multiply(pullStrength);
			this.velocity = this.velocity.add(pullForce);
		}
	}
	
	private void applyFriction() {
		this.velocity.setX(this.velocity.getX() * (1-Simulation.FRICTION));
	}
	
	private void applyForce(Vector2D force) {
	    Vector2D acceleration = force.divide(mass); // F = ma, a = F/m
	    velocity = velocity.add(acceleration.multiply(sim.getDeltaTime()));
		
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

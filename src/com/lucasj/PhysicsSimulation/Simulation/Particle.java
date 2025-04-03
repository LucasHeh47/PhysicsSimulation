package com.lucasj.PhysicsSimulation.Simulation;

import java.awt.Color;
import java.awt.Graphics2D;

import com.lucasj.PhysicsSimulation.Debug;
import com.lucasj.PhysicsSimulation.Math.Vector2D;

public class Particle {

	private Simulation sim;
	
	private Vector2D location;
	private Vector2D velocity;
	private Vector2D acceleration;
	
	private final int floor;
	
	private int size = 32;
	
	private boolean isGrounded = false;
	
	public Particle(Simulation sim, Vector2D loc) {
		this.sim = sim;
		floor = sim.getResolution().height;
		this.location = loc;
		this.velocity = new Vector2D(0);
		this.acceleration = new Vector2D(0);
	}
	
	public void update(double deltaTime) {
		Debug.Log(this, "Location: " + this.location);
		Debug.Log(this, "Velocity: " + this.velocity);
		Debug.Log(this, "Acceleration: " + this.acceleration);
		applyGravity();
		if(!isGrounded) {
			this.velocity = this.velocity.add(acceleration);
		} else {
			this.velocity = Vector2D.zero();
			Debug.Log(this, "GROUNDED: " + this.location);
			return;
		}
		this.location = this.location.add(velocity.multiply(deltaTime * Simulation.SPEED_MULTIPLIER));
		
		// bounce back after hitting bottom
		if(location.getYint() + size >= floor) {
			this.velocity.setY(this.velocity.getY() * -0.9);
		}

		// Let particles rest on surface
		if(location.getY() + size >= floor && (velocity.getY() <= 10 && velocity.getY() >= -10)) {
			isGrounded = true;
			location.setY(floor-size);
			return;
		}
	}
	
	public void render(Graphics2D g2d) {
		g2d.setColor(Color.blue);
		g2d.fillOval(location.getXint(), location.getYint(), size, size);
	}
	
	private void applyGravity() {
		this.acceleration = new Vector2D(0, 0);
		this.acceleration = this.acceleration.add(new Vector2D(0, Simulation.ACCELERATION_GRAVITY));
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
	
}

package com.lucasj.PhysicsSimulation.Input;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.lucasj.PhysicsSimulation.Math.Vector2D;
import com.lucasj.PhysicsSimulation.Simulation.Particle;
import com.lucasj.PhysicsSimulation.Simulation.Simulation;

public class MouseHandler implements MouseMotionListener, MouseListener {

	private Simulation sim;
	public boolean isControlling = false;
	
	public MouseHandler(Simulation sim) {
		this.sim = sim;
	}
	
	public void render(Graphics2D g2d) {
		Vector2D loc = sim.getInputForce();
		if(loc.getX() == -1) return;
		
		// not done
		Particle particle = new Particle(sim, loc.subtract(new Vector2D(64)), 128);
		particle.setUserControlled(true);
		particle.setId(-1);
		sim.addParticle(particle);
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		sim.setInputForce(new Vector2D(e.getX(), e.getY()));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isControlling = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		sim.setInputForce(new Vector2D(-1, -1));
		isControlling = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
